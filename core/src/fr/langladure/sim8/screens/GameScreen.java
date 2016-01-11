package fr.langladure.sim8.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import fr.langladure.sim8.rooms.*;
import fr.langladure.sim8.SIM8;


/**
 * @author Radnap
 */
public class GameScreen extends AbstractScreen {

	private Stage stage;

	private Sprite background;

	private PilotRoom pilotRoom;
	private ControlRoom controlRoom;
	private RestRoom restRoom;
	private Hull hull;
	private StoreRoom storeRoom;
	private float ratio;


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

		pilotRoom = new PilotRoom();
		stage.addActor(pilotRoom);
		pilotRoom.setBackground(new TextureRegionDrawable(atlas.findRegion("PilotRoom")));
		pilotRoom.setPosition(64f, 76f + 340f + 38f);
		pilotRoom.setSize(880f, 550f);

		controlRoom = new ControlRoom();
		stage.addActor(controlRoom);
		controlRoom.setBackground(new TextureRegionDrawable(atlas.findRegion("ControlRoom")));
		controlRoom.setPosition(64f + 880f + 32f, 76f + 340f + 38f);
		controlRoom.setSize(880f, 550f);

		restRoom = new RestRoom();
		stage.addActor(restRoom);
		restRoom.setBackground(new TextureRegionDrawable(atlas.findRegion("RestRoom")));
		restRoom.setPosition(98f, 76f);
		restRoom.setSize(542f, 340f);

		hull = new Hull();
		stage.addActor(hull);
		hull.setBackground(new TextureRegionDrawable(atlas.findRegion("Hull")));
		hull.setPosition(98f + 528f + 49f, 76f);
		hull.setSize(542f, 340f);

		storeRoom = new StoreRoom();
		stage.addActor(storeRoom);
		storeRoom.setBackground(new TextureRegionDrawable(atlas.findRegion("StoreRoom")));
		storeRoom.setPosition(98f + 2f * 49f + 2f * 528f, 76f);
		storeRoom.setSize(542f, 340f);

	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
	}

	@Override
	public void show() {
		super.show();
		InputMultiplexer multiplexer = new InputMultiplexer(stage);
		Gdx.input.setInputProcessor(multiplexer);
	}

	@Override
	public void render(float delta) {
		super.render(delta);
		stage.setDebugAll(SIM8.DEVMODE);

		game.batch.setProjectionMatrix(camera.combined);
		game.batch.begin();
		for (float x = 0; x < SCREEN_WIDTH; x += background.getWidth() - 01f * ratio) {
			for (float y = -background.getHeight() / 2f; y < SCREEN_HEIGHT; y += background.getHeight() - 01f * ratio) {
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
	}
}
