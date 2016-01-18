package fr.radnap.sim8.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import fr.radnap.sim8.PlayerShip;
import fr.radnap.sim8.SIM8;


/**
 * @author Radnap
 */
public class GameScreen extends AbstractScreen {

	private Stage stage;

	private Sprite background;
	private float ratio;
	public static Skin skin = null;
	private PlayerShip playerShip;
	private Image overlay;


	public GameScreen(SIM8 game) {
		super(game);
	}


	public Stage getStage() {
		return stage;
	}

	@Override
	public void loadAssets() {
		super.loadAssets();
		game.assetsFinder.load("game");
	}

	@Override
	public void create() {
		stage = new Stage(viewport, game.batch);

		TextureAtlas atlas = game.assetManager.get("game/gamePack.atlas", TextureAtlas.class);

		background = atlas.createSprite("metal_panel");

		ratio = SCREEN_WIDTH / (4f * background.getWidth());
		background.setSize(background.getWidth() * ratio, background.getHeight() * ratio);


		if (skin == null) {
			skin = new Skin();

			FreeTypeFontGenerator.FreeTypeFontParameter fontParams = new FreeTypeFontGenerator.FreeTypeFontParameter();
			fontParams.minFilter = Texture.TextureFilter.Linear;
			fontParams.magFilter = Texture.TextureFilter.Linear;
			fontParams.shadowColor = new Color(.5f, 0f, 0f, 0.4f);
			fontParams.shadowOffsetX = 1;
			fontParams.shadowOffsetY = 2;
			fontParams.size = 22;

			Label.LabelStyle labelStyle = new Label.LabelStyle();
			labelStyle.font = SIM8.commonGen.generateFont(fontParams);
			labelStyle.fontColor = new Color(1f, 1f, 1f, 1f);
			skin.add("default", labelStyle);

			labelStyle = new Label.LabelStyle();
			fontParams.size = 30;
			labelStyle.font = SIM8.numberGen.generateFont(fontParams);
			labelStyle.fontColor = new Color(1f, 1f, 1f, 1f);
			skin.add("number", labelStyle);

			labelStyle = new Label.LabelStyle();
			fontParams.size = 150;
			labelStyle.font = SIM8.numberGen.generateFont(fontParams);
			labelStyle.fontColor = new Color(1f, 1f, 1f, 1f);
			skin.add("title", labelStyle);
		}

		playerShip = new PlayerShip(this, atlas, game.assetManager);
		overlay = new Image(atlas.findRegion("overlay"));
		overlay.setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
		overlay.setTouchable(Touchable.disabled);
		overlay.setColor(1f, 1f, 1f, 0f);
		stage.addActor(overlay);
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
	}

	@Override
	public void show() {
		super.show();
		Gdx.input.setInputProcessor(stage);
		playerShip.initialize();
	}

	public void gameOver(String comment) {
		overlay.setTouchable(Touchable.enabled);
		overlay.addAction(Actions.fadeIn(.3f));

		Label label = new Label("You Fail", skin, "title");
		label.setPosition((SCREEN_WIDTH - label.getWidth()) / 2f, (SCREEN_HEIGHT + label.getHeight()) / 2f);
		stage.addActor(label);

		Label commentLabel = new Label(comment, skin);
		commentLabel.setAlignment(Align.center);
		commentLabel.setSize(commentLabel.getWidth() * 1.5f, commentLabel.getHeight() * 3f);
		commentLabel.setPosition((SCREEN_WIDTH - commentLabel.getWidth()) / 2f, SCREEN_HEIGHT / 2f - commentLabel.getHeight());
		stage.addActor(commentLabel);

		label = new Label("> Back to the menu <", skin);
		label.setAlignment(Align.center);
		label.setSize(label.getWidth() * 1.5f, label.getHeight() * 3f);
		label.setPosition((SCREEN_WIDTH - label.getWidth()) / 2f, commentLabel.getY() - 2f * label.getHeight());
		label.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				super.clicked(event, x, y);
				game.loadingScreen.setFadeWhenLoaded(true);
				game.loadingScreen.setNextScreen(game.mainMenuScreen);
				game.setScreen(game.loadingScreen);
			}
		});
		stage.addActor(label);
	}

	public void ending() {
		overlay.setTouchable(Touchable.enabled);
		overlay.addAction(Actions.fadeIn(.3f));

		Label label = new Label("Success", skin, "title");
		label.setPosition((SCREEN_WIDTH - label.getWidth()) / 2f, (SCREEN_HEIGHT + label.getHeight()) / 2f);
		stage.addActor(label);

		Label commentLabel = new Label("You manage to take the crew back to the headquarters.", skin);
		commentLabel.setAlignment(Align.center);
		commentLabel.setSize(commentLabel.getWidth() * 1.5f, commentLabel.getHeight() * 3f);
		commentLabel.setPosition((SCREEN_WIDTH - commentLabel.getWidth()) / 2f, SCREEN_HEIGHT / 2f - commentLabel.getHeight());
		stage.addActor(commentLabel);

		label = new Label("> Back to the menu <", skin);
		label.setAlignment(Align.center);
		label.setSize(label.getWidth() * 1.5f, label.getHeight() * 3f);
		label.setPosition((SCREEN_WIDTH - label.getWidth()) / 2f, commentLabel.getY() - 2f * label.getHeight());
		label.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				super.clicked(event, x, y);
				game.loadingScreen.setFadeWhenLoaded(true);
				game.loadingScreen.setNextScreen(game.mainMenuScreen);
				game.setScreen(game.loadingScreen);
			}
		});
		stage.addActor(label);
	}

	@Override
	public void render(float delta) {
		super.render(delta);
		stage.setDebugAll(SIM8.DEVMODE);

		game.batch.setProjectionMatrix(camera.combined);
		game.batch.begin();
		for (float x = 0; x < SCREEN_WIDTH; x += background.getWidth() - 1f * ratio) {
			for (float y = -background.getHeight() / 2f; y < SCREEN_HEIGHT; y += background.getHeight() - 1f * ratio) {
				background.setPosition(x, y);
				background.draw(game.batch);
			}
		}
		game.batch.end();

		stage.act(delta);
		stage.draw();
	}

	@Override
	public void dispose() {
		super.dispose();

		game.assetsFinder.unload("game");
		if (stage != null)
			stage.dispose();
	}
}
