package fr.radnap.sim8.rooms;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

/**
 * @author Radnap
 */
public class PilotRoom extends RepairableRoom {


	public PilotRoom(TextureAtlas atlas, float width, float height) {
		super("PilotRoom", atlas, width, height, 100);

		addActionButton("boost", new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
			}
		});

		addActionButton("takeOff", new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
			}
		});

		addActionButton("land", new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
			}
		});

		belowButtons.add("Planet within sight").row();
		belowButtons.add("Planet within sight").row();
		belowButtons.add("Planet within sight").row();
		belowButtons.add("Planet within sight").row();
	}
}
