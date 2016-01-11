package fr.langladure.sim8.rooms;

import com.badlogic.gdx.scenes.scene2d.ui.Button;

/**
 * @author Radnap
 */
public class RestRoom extends Room {


	public RestRoom() {
		/// Stabilize crew
		Button button = new Button();
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
