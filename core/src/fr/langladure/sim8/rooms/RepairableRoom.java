package fr.langladure.sim8.rooms;

import com.badlogic.gdx.scenes.scene2d.ui.Button;

/**
 * @author Radnap
 */
public abstract class RepairableRoom extends Room {

	private int status;


	public RepairableRoom() {
		status = 100;

		Button repair = new Button();
		actions.put(repair, new RoomAction() {
			@Override
			public void action() {
				status += 10;
				if (status > 100)
					status = 100;
			}
		});
	}


	public int getStatus() {
		return status;
	}

	@Override
	public void takeDamage(int damage) {
		status -= damage;
		if (status < 0)
			status = 0;
	}
}
