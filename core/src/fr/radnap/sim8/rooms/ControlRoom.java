package fr.radnap.sim8.rooms;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
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
	}
}
