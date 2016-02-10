package fr.radnap.sim8.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import fr.radnap.sim8.SIM8;

/**
 * @author Radnap
 */
public class LoadingScreen extends AbstractScreen {

	protected final Stage stage;

	private float ratio;

	protected Image loadingBar;
	protected Label clickLabel;

	private AbstractScreen nextScreen;
	private boolean fadeWhenLoaded;

	private float percent;


	/**
	 * Will load all the assets it needs during instantiation.
	 */
	public LoadingScreen(SIM8 game) {
		super(game);
		fadeWhenLoaded = true;

		stage = new Stage(viewport, game.batch);

		// Tell the assetManager to load assets for the loading screen
		game.assetManager.load("loading/loadingPack.atlas", TextureAtlas.class);
		// Wait until they are finished loading
		game.assetManager.finishLoading();

		// Get our textureatlas from the assetManager
		TextureAtlas atlas = game.assetManager.get("loading/loadingPack.atlas", TextureAtlas.class);


		/// Create the screen background and adapt the world to it
		Image screenBg = new Image(atlas.findRegion("loadingBg"));

		ratio = SCREEN_WIDTH / screenBg.getWidth();
		screenBg.setSize(SCREEN_WIDTH, SCREEN_HEIGHT);

		stage.addActor(screenBg);


		// Place the loading bar
		loadingBar = new Image(atlas.findRegion("loadingBar"));
		stage.addActor(loadingBar);
		loadingBar.setWidth(SCREEN_WIDTH - 12 * ratio);
		loadingBar.setX(6*ratio);
		loadingBar.setY(stage.getHeight() / 4f - .5f * loadingBar.getHeight());

		FreeTypeFontGenerator.FreeTypeFontParameter fontParams = new FreeTypeFontGenerator.FreeTypeFontParameter();
		fontParams.minFilter = Texture.TextureFilter.Linear;
		fontParams.magFilter = Texture.TextureFilter.Linear;
		fontParams.size = (int) (loadingBar.getHeight());
		fontParams.shadowColor = new Color(0x5f3f3fff);
		fontParams.shadowOffsetX = 2;
		fontParams.shadowOffsetY = 2;
		BitmapFont font = SIM8.title2Gen.generateFont(fontParams);

		clickLabel = new Label(">Click<", new Label.LabelStyle(font, new Color(0.9f, 0.9f, 0.85f, 1f)));
		stage.addActor(clickLabel);
		clickLabel.setX((stage.getWidth() - clickLabel.getWidth()) / 2f);
		clickLabel.setY(loadingBar.getY() + 2f * loadingBar.getHeight());
		clickLabel.setAlignment(Align.center);

		ClickListener goToNextScreenListener = new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				goToNextScreen();
			}

			@Override
			public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
				((Label) event.getListenerActor()).setFontScale(1.1f);
			}

			@Override
			public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
				((Label) event.getListenerActor()).setFontScale(1f);
			}
		};
		clickLabel.addListener(goToNextScreenListener);
	}

	public void setNextScreen(AbstractScreen nextScreen) {
		this.nextScreen = nextScreen;
	}

	public void setFadeWhenLoaded(boolean fadeWhenLoaded) {
		this.fadeWhenLoaded = fadeWhenLoaded;
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);

		loadingBar.setWidth(SCREEN_WIDTH - 12 * ratio);
	}

	@Override
	public void show() {
		super.show();
		percent = 0;
		clickLabel.setVisible(false);

		Gdx.input.setInputProcessor(stage);

		// Add everything to be loaded
		nextScreen.loadAssets();
	}

	protected void goToNextScreen() {
		System.gc();
		nextScreen.create();
		loadingBar.setScaleX(0f);
		game.setScreen(nextScreen);
	}

	@Override
	public void render(float delta) {
		super.render(delta);
		stage.setDebugAll(SIM8.DEVMODE);

		if (game.assetManager.update()) { // Load some, will return true if done loading;
			if (!fadeWhenLoaded && percent > 0.95f)
				clickLabel.setVisible(true);
			if (fadeWhenLoaded) {
				goToNextScreen();
			}
		}

		// Interpolate the percentage to make it more smooth
		percent = Interpolation.linear.apply(percent, game.assetManager.getProgress(), 0.1f);

		// Update positions (and size) to match the percentage
		loadingBar.setScaleX(percent);

		// Show the loading screen
		stage.act(delta);
		stage.draw();
	}

	@Override
	public void hide() {
		super.hide();
	}

	@Override
	public void dispose() {
		super.dispose();

		// Dispose the loading assets as we no longer need them
		game.assetManager.unload("loading/loadingPack.atlas");
		if (stage != null) {
			stage.dispose();
		}
	}
}
