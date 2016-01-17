package fr.radnap.sim8.rooms;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import fr.radnap.sim8.PlayerShip;

/**
 * @author Radnap
 */
public class RestRoom extends Room {


	public RestRoom(PlayerShip ship, TextureAtlas atlas, AssetManager assetManager, float width, float height) {
		super(ship, "RestRoom", atlas, assetManager, width, height);

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
