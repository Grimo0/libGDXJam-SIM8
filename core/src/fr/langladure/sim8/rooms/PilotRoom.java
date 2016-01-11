package fr.langladure.sim8.rooms;

import com.badlogic.gdx.scenes.scene2d.ui.Button;

/**
 * @author Radnap
 */
public class PilotRoom extends RepairableRoom {


	public PilotRoom() {
		/// Boost
		Button button = new Button();
		actions.put(button, new RoomAction() {
			@Override
			public void action() {

			}
		});

		/// Take Off
		button = new Button();
		actions.put(button, new RoomAction() {
			@Override
			public void action() {

			}
		});

		/// Landing
		button = new Button();
		actions.put(button, new RoomAction() {
			@Override
			public void action() {

			}
		});
	}
}
