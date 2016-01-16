package fr.radnap.sim8.rooms;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

/**
 * @author Radnap
 */
public class StoreRoom extends Room {

	private int resources;
	private Label resourcesLabel;


	public StoreRoom(TextureAtlas atlas, float width, float height) {
		super("StoreRoom", atlas, width, height);

		resources = 85;

		addActionButton("harvest", new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
			}
		});

		aboveButtons.right().padTop(5f).padRight(7f);
		aboveButtons.defaults().space(5f);
		resourcesLabel = aboveButtons.add("", "number").getActor();
	}


	@Override
	public void initialize() {

	}

	@Override
	public void act(float delta) {
		super.act(delta);
		resourcesLabel.setText(String.valueOf(resources) + "r");
	}

	public int getResources() {
		return resources;
	}

	public int use(int amount) {
		if (resources > amount) {
			resources -= amount;
			return amount;
		}
		amount = resources;
		resources = 0;
		return amount;
	}
}
