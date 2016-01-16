package fr.radnap.sim8.rooms;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

/**
 * @author Radnap
 */
public class ControlRoom extends RepairableRoom {


	public ControlRoom(TextureAtlas atlas, float width, float height) {
		super("ControlRoom", atlas, width, height, 100);

		addActionButton("laser", new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
			}
		});

		addActionButton("rockets", new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
			}
		});

		addActionButton("leave", new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				((PilotRoom) rooms.get("PilotRoom")).moveToClosestStar();
			}
		});

	}


	@Override
	public void initialize() {

	}

	public void arriveToPlanet() {
		enable("leave");
		if (Math.random() < 0.5f)
			encounterEnemy((int) (Math.random() * 4 + 1));

		belowButtons.clearChildren();
		belowButtons.add("Planet within sight !").row();
	}

	public void leavePlanet() {
		disable("leave");

		belowButtons.clearChildren();
		belowButtons.add("Starting the reactor, leaving the area").row();
	}

	public void encounterEnemy(int enemyType) {
		((Hull) rooms.get("Hull")).seeEnemy(enemyType);

	}
}
