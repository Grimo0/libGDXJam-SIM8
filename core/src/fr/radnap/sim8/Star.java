package fr.radnap.sim8;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

/**
 * @author Radnap
 */
public class Star extends Image {
	private int planetNumber;
	private EnemyShip enemyShip;


	public Star(Drawable drawable, int planetNumber, EnemyShip enemyShip) {
		super(drawable);
		this.planetNumber = planetNumber;
		this.enemyShip = enemyShip;
		if (enemyShip != null)
			enemyShip.setStar(this);
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
}
