package fr.radnap.sim8.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
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


	public GameScreen(SIM8 game) {
		super(game);
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
		}

		playerShip = new PlayerShip(atlas, game.assetManager, stage);
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
		stage.dispose();
	}
}
