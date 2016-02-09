package fr.radnap.sim8;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import fr.radnap.sim8.rooms.*;
import fr.radnap.sim8.screens.GameScreen;

/**
 * @author Radnap
 */
public class PlayerShip implements Ship {

	private boolean gameOver;

	private int consecutiveDamages;

	private final GameScreen gameScreen;

	private PilotRoom pilotRoom;
	private ControlRoom controlRoom;
	private Hull hull;
	private RestRoom restRoom;


	public PlayerShip(GameScreen gameScreen, TextureAtlas atlas, AssetManager assetManager) {
		this.gameScreen = gameScreen;
		gameOver = false;
		consecutiveDamages = 0;

		pilotRoom = new PilotRoom(this, atlas, assetManager, 880f, 550f);
		gameScreen.getStage().addActor(pilotRoom);
		pilotRoom.setPosition(64f, 60f + 395f + 30f);

		controlRoom = new ControlRoom(this, atlas, assetManager, 880f, 550f);
		gameScreen.getStage().addActor(controlRoom);
		controlRoom.setPosition(64f + 880f + 32f, 60f + 395f + 30f);

		hull = new Hull(this, atlas, assetManager, 632f, 395f);
		gameScreen.getStage().addActor(hull);
		hull.setPosition(240f, 60f);

		restRoom = new RestRoom(this, atlas, assetManager, 632f, 395f);
		gameScreen.getStage().addActor(restRoom);
		restRoom.setPosition(2f * 240f + 600f, 60f);
	}


	public void initialize() {
		pilotRoom.initialize();
		controlRoom.initialize();
		hull.initialize();
		restRoom.initialize();
	}

	public Hull getHull() {
		return hull;
	}

	public boolean isGameOver() {
		return gameOver;
	}

	@Override
	public int getRocketDamages() {
		return 15;
	}

	@Override
	public int getLaserDamages() {
		return 2;
	}

	public Cell<Label> print(String s, boolean warning) {
		return controlRoom.print(s, warning);
	}

	public void takeDamages(int damages) {
		if (Math.random() < .2f) {
			controlRoom.takeDamages(2 * damages);
		}
		hull.takeDamages(damages);
		consecutiveDamages += damages;
		if (consecutiveDamages > 10) {
			restRoom.increaseStress();
			consecutiveDamages = 0;
		}
	}

	public void endFight() {
		consecutiveDamages = 0;
		controlRoom.endFight();
		if (pilotRoom.hasArrived())
			ending();
	}

	public void gameOver(String comment) {
		gameOver = true;
		gameScreen.gameOver(comment);
	}

	public void ending() {
		gameScreen.ending();
	}
}
