package fr.langladure.sim8.screens;

import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import fr.langladure.sim8.SIM8;


/**
 * @author Radnap
 */
public class GameScreen extends AbstractScreen {

	private PooledEngine engine;

	private Sprite background;


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
		TextureAtlas atlas = game.assetManager.get("game/gamePack.atlas", TextureAtlas.class);

		background = atlas.createSprite("bg");

		float ratio = SCREEN_WIDTH / background.getWidth();
		background.setSize(SCREEN_WIDTH, SCREEN_HEIGHT);

		/*float worldWidth = 1;
		viewport.setWorldSize(worldWidth, worldWidth * Gdx.graphics.getHeight() / Gdx.graphics.getWidth());
		updateViewPort(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());*/


		///// Background /////
		/*background = atlas.createSprite("bg");
		background.setPosition(0f, 0f);
		if (Gdx.graphics.getWidth() / background.getWidth() < Gdx.graphics.getHeight() / background.getHeight()) {
			background.setSize(viewport.getWorldHeight() * background.getWidth() / background.getHeight(), viewport.getWorldHeight());
		} else {
			background.setSize(viewport.getWorldWidth(), viewport.getWorldWidth() * background.getHeight() / background.getWidth());
		}*/

	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
	}

	@Override
	public void show() {
		super.show();
		InputMultiplexer multiplexer = new InputMultiplexer();
		Gdx.input.setInputProcessor(multiplexer);
	}

	@Override
	public void render(float delta) {
		super.render(delta);

		engine.update(delta);

		game.batch.setProjectionMatrix(camera.combined);
		game.batch.begin();
//		background.setPosition(camera.position.x - background.getWidth() / 2f, camera.position.y - background.getHeight() / 2f);
		background.draw(game.batch);
		game.batch.end();

	}

	@Override
	public void dispose() {
		super.dispose();

		game.assetsFinder.unload("game");
	}
}
