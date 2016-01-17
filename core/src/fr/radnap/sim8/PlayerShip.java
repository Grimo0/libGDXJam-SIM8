package fr.radnap.sim8;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import fr.radnap.sim8.rooms.*;

/**
 * @author Radnap
 */
public class PlayerShip implements Ship {

	private PilotRoom pilotRoom;
	private ControlRoom controlRoom;
	private StoreRoom storeRoom;
	private Hull hull;
	private RestRoom restRoom;


	public PlayerShip(TextureAtlas atlas, AssetManager assetManager, Stage stage) {
		pilotRoom = new PilotRoom(this, atlas, assetManager, 880f, 550f);
		stage.addActor(pilotRoom);
		pilotRoom.setPosition(64f, 60f + 375f + 30f);

		controlRoom = new ControlRoom(this, atlas, assetManager, 880f, 550f);
		stage.addActor(controlRoom);
		controlRoom.setPosition(64f + 880f + 32f, 60f + 375f + 30f);

		storeRoom = new StoreRoom(this, atlas, assetManager, 600f, 375f);
		stage.addActor(storeRoom);
		storeRoom.setPosition(40f, 60f);

		hull = new Hull(this, atlas, assetManager, 600f, 375f);
		stage.addActor(hull);
		hull.setPosition(40f + 600f + 24f, 60f);

		restRoom = new RestRoom(this, atlas, assetManager, 600f, 375f);
		stage.addActor(restRoom);
		restRoom.setPosition(40f + 2f * 24f + 2f * 600f, 60f);
	}


	public void initialize() {
		pilotRoom.initialize();
		controlRoom.initialize();
		storeRoom.initialize();
		hull.initialize();
		restRoom.initialize();
	}

	public Hull getHull() {
		return hull;
	}

	@Override
	public int getRocketDamages() {
		return 10;
	}

	@Override
	public int getLaserDamages() {
		return 1;
	}

	public void takeDamages(int damages) {
		if (Math.random() < .2f) {
			controlRoom.takeDamages(damages);
		} else {
			hull.takeDamages(damages);
		}
	}

	public void endFight() {
		controlRoom.endFight();
	}
}
