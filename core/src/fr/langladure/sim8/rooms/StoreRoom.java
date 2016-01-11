package fr.langladure.sim8.rooms;

import com.badlogic.gdx.scenes.scene2d.ui.Button;

/**
 * @author Radnap
 */
public class StoreRoom extends Room {


	public StoreRoom() {
		/// Harvest fuel
		Button button = new Button();
		actions.put(button, new RoomAction() {
			@Override
			public void action() {

			}
		});

		/// Harvest resources
		button = new Button();
		actions.put(button, new RoomAction() {
			@Override
			public void action() {

			}
		});
	}

	@Override
	public void takeDamage(int damage) {

	}
}
