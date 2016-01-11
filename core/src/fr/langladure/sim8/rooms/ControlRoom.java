package fr.langladure.sim8.rooms;

import com.badlogic.gdx.scenes.scene2d.ui.Button;

/**
 * @author Radnap
 */
public class ControlRoom extends RepairableRoom {


	public ControlRoom() {
		/// Shoot with laser
		Button button = new Button();
		actions.put(button, new RoomAction() {
			@Override
			public void action() {

			}
		});

		/// Launch torpedos
		button = new Button();
		actions.put(button, new RoomAction() {
			@Override
			public void action() {

			}
		});
	}
}
