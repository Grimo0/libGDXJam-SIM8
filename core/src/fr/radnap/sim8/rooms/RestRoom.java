package fr.radnap.sim8.rooms;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

/**
 * @author Radnap
 */
public class RestRoom extends Room {


	public RestRoom(TextureAtlas atlas, float width, float height) {
		super("RestRoom", atlas, width, height);

		addActionButton("heal", new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
			}
		});
	}


	@Override
	public void initialize() {

	}
}
