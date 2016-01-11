package fr.langladure.sim8.rooms;

import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.OrderedMap;

/**
 * @author Radnap
 */
public abstract class Room extends Table {

	public interface RoomAction {
		void action();
	}

	protected OrderedMap<Button, RoomAction> actions;


	public Room() {
		this.actions = new OrderedMap<>(5);
	}

	public abstract void takeDamage(int damage);
}
