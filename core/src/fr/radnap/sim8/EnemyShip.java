package fr.radnap.sim8;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

/**
 * @author Radnap
 */
public class EnemyShip extends Group implements Ship {

	private PlayerShip playerShip;
	private Star star;
	private int enemyType;
	private int life;
	private int damages;
	private float fireRate;
	private float stateTime;
	private Image ship;

	private Image laser1;
	private Image laser2;
	private Image laser3;
	private Sound laserSound;
	private Sound laserTouchedSound;


	public EnemyShip(TextureAtlas atlas, AssetManager assetManager, PlayerShip playerShip, int enemyType) {
		this.playerShip = playerShip;
		this.star = null;
		this.enemyType = enemyType;
		life = 20 + ((enemyType - 1) % 4) * 5;
		damages = 2 + ((enemyType - 1) % 4) / 2;
		fireRate = .3f * ((enemyType - 1) % 4) + 1f;
		stateTime = 0f;

		ship = new Image(atlas.findRegion("enemy", enemyType));
		addActor(ship);
		ship.setPosition(.5f, 1f);
		setSize(ship.getWidth(), ship.getHeight());

		laser1 = new Image(atlas.findRegion("laser"));
		laser1.setVisible(false);
		laser1.setOrigin(laser1.getWidth() / 2f, laser1.getHeight() / 2f);
		addActorAfter(ship, laser1);
		laser2 = new Image(atlas.findRegion("laser"));
		laser2.setVisible(false);
		laser2.setOrigin(laser2.getWidth() / 2f, laser2.getHeight() / 2f);
		addActorAfter(ship, laser2);
		laser3 = new Image(atlas.findRegion("laser"));
		laser3.setVisible(false);
		laser3.setOrigin(laser3.getWidth() / 2f, laser3.getHeight() / 2f);
		addActorAfter(ship, laser3);

		laserSound = assetManager.get("./sounds/laser2.mp3", Sound.class);
		laserTouchedSound = assetManager.get("./sounds/laserTouched.mp3", Sound.class);
	}


	@Override
	public void act(float delta) {
		super.act(delta);
		stateTime += delta;
		if (stateTime > fireRate) {
			stateTime = 0;
			fire();
		}
		if (!ship.hasActions()) {
			ship.addAction(Actions.moveBy(0, -ship.getY() * 2f, 1f));
			ship.addAction(sequence(
					Actions.moveBy((- ship.getX()) * 2f, 0, .5f),
					Actions.moveBy((+ ship.getX()) * 2f, 0, .5f)));
		}
	}

	@Override
	public int getLaserDamages() {
		return damages;
	}

	@Override
	public int getRocketDamages() {
		return 0;
	}

	private void fire() {
		if (playerShip.isGameOver()) return;

		fireLaser(laser1);
		ship.addAction(sequence(
				delay(.07f),
				run(new Runnable() {
					@Override
					public void run() {
						fireLaser(laser2);
					}
				}),
				delay(.07f),
				run(new Runnable() {
					@Override
					public void run() {
						fireLaser(laser3);
					}
				})
		));
	}

	private void fireLaser(final Image laser) {
		if (Options.sound)
			laserSound.play(.01f);

		float attackOriginX = ship.getX();
		float attackOriginY = ship.getY() + ship.getHeight() / 2f;

		laser.setPosition(attackOriginX - laser.getWidth() / 2f, attackOriginY - laser.getHeight() / 2f);
		laser.setVisible(true);
		final Actor enemy = getParent().findActor("PlayerShipHull");
		float x = (float) (enemy.getX() + enemy.getWidth() * .75f + Math.random() * enemy.getWidth() * .25f - getX());
		float y = (float) (ship.getY() + ship.getHeight() * .3f + Math.random() * ship.getHeight() * .4f);
		double distance = SIM8.distance(attackOriginX, attackOriginY, x, y);
		if (y > attackOriginY)
			laser.setRotation(90f + (float) (Math.acos(SIM8.distance(x, attackOriginY, x, y) / distance) * 180 / Math.PI));
		else
			laser.setRotation(90f - (float) (Math.acos(SIM8.distance(x, attackOriginY, x, y) / distance) * 180 / Math.PI));
		laser.addAction(sequence(
				moveTo(x, y, (float) (distance / 500f)),
				Actions.run(new Runnable() {
					@Override
					public void run() {
						playerShip.takeDamages(getLaserDamages());
						laser.setVisible(false);
						if (Options.sound)
							laserTouchedSound.play(.01f);
					}
				})
		));
	}

	private void destroy() {
		remove();
		star.setEnemyShip(null);
		playerShip.endFight();
	}

	public int getEnemyType() {
		return enemyType;
	}

	public int getLife() {
		return life;
	}

	public void setStar(Star star) {
		this.star = star;
	}

	public void takeDamages(int damages) {
		life -= damages;
		if (life < 0) {
			destroy();
		}
	}
}
