package fr.radnap.sim8;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

/**
 * @author Radnap
 */
public class Star extends Image {

	private int planetNumber;
	private EnemyShip enemyShip;
	private int resources;


	public Star(Drawable drawable, int planetNumber, EnemyShip enemyShip) {
		super(drawable);
		this.planetNumber = planetNumber;
		this.enemyShip = enemyShip;
		if (enemyShip != null)
			enemyShip.setStar(this);
		resources = 5 + (int) (Math.random() * 10);
	}


	public int getPlanetNumber() {
		return planetNumber;
	}

	public EnemyShip getEnemyShip() {
		return enemyShip;
	}

	public void setEnemyShip(EnemyShip enemyShip) {
		this.enemyShip = enemyShip;
	}

	public int takeResources() {
		int taken = 1 + (int) (Math.random() * 3);
		resources -= taken;
		if (resources < 0) {
			taken += resources;
			resources = 0;
		}

		return taken;
	}

	public boolean hasResources() {
		return resources > 0;
	}
}
