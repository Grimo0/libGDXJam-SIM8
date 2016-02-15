package fr.radnap.sim8.rooms;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import fr.radnap.sim8.EnemyShip;
import fr.radnap.sim8.Options;
import fr.radnap.sim8.PlayerShip;
import fr.radnap.sim8.Rocket;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

/**
 * @author Radnap
 */
public class Hull extends RepairableRoom {

	private Image stars;

	private TextureRegionDrawable planetDrawable;
	private Image planet;

	private Image shipHull;
	private EnemyShip enemy;

	private Image laser1;
	private Image laser2;
	private Image laser3;
	private Sound laserSound;
	private Sound laserTouchedSound;

	private Rocket rocket1;
	private Rocket rocket2;

	private Image travel;
	private Sound travelSound;


	public Hull(PlayerShip ship, TextureAtlas atlas, AssetManager assetManager, float width, float height) {
		super(ship, "Hull", atlas, assetManager, width, height, .5f);

		stars = new Image(atlas.findRegion("hull/stars"));
		stars.setSize(2 * width * stars.getWidth() / stars.getHeight(), 2 * width);
		stars.setOrigin(stars.getWidth() / 2f, stars.getHeight() / 2f);
		stars.setPosition(width - stars.getWidth() / 2f, (height - stars.getHeight()) / 2f);
		addActorBefore(belowButtons, stars);

		planetDrawable = new TextureRegionDrawable();
		planet = new Image();
		addActorAfter(stars, planet);

		shipHull = new Image(atlas.findRegion("hull/blueship"));
		shipHull.setName("PlayerShipHull");
		shipHull.setPosition(5f, (height - shipHull.getHeight()) / 2f + 2f);
		addActorAfter(planet, shipHull);

		laser1 = new Image(atlas.findRegion("hull/laser"));
		laser1.setVisible(false);
		laser1.setOrigin(laser1.getWidth() / 2f, laser1.getHeight() / 2f);
		addActorAfter(shipHull, laser1);
		laser2 = new Image(atlas.findRegion("hull/laser"));
		laser2.setVisible(false);
		laser2.setOrigin(laser2.getWidth() / 2f, laser2.getHeight() / 2f);
		addActorAfter(shipHull, laser2);
		laser3 = new Image(atlas.findRegion("hull/laser"));
		laser3.setVisible(false);
		laser3.setOrigin(laser3.getWidth() / 2f, laser3.getHeight() / 2f);
		addActorAfter(shipHull, laser3);

		laserSound = assetManager.get("sounds/laser2.mp3", Sound.class);
		laserTouchedSound = assetManager.get("sounds/laserTouched.mp3", Sound.class);
		Sound rocketsSound = assetManager.get("sounds/laser.mp3", Sound.class);
		Sound rocketsExplodeSound = assetManager.get("sounds/explosion.mp3", Sound.class);

		Animation explosionAnimation = new Animation(.1f, atlas.findRegions("hull/explosion"));
		TextureRegion rocketRegion = atlas.findRegion("hull/rocket");
		rocket1 = new Rocket(rocketRegion, enemy, explosionAnimation, rocketsSound, rocketsExplodeSound);
		rocket1.setOrigin(rocket1.getWidth() / 2f, rocket1.getHeight() / 2f);
		addActorAfter(shipHull, rocket1);
		rocket2 = new Rocket(rocketRegion, enemy, explosionAnimation, rocketsSound, rocketsExplodeSound);
		rocket2.setOrigin(rocket2.getWidth() / 2f, rocket2.getHeight() / 2f);
		addActorAfter(shipHull, rocket2);

		travel = new Image(atlas.findRegion("hull/travel"));
		travel.setVisible(false);
		travel.setSize(width, width * travel.getHeight() / travel.getWidth());
		travel.setPosition((width - travel.getWidth()) / 2f, (height - travel.getHeight()) / 2f);
		addActorBefore(belowButtons, travel);

		travelSound = assetManager.get("sounds/warpout.mp3");
	}


	@Override
	public void disableRoom() {
		if (getStatus() <= 0)
			ship.gameOver("The ship is wrecked.");
		else
			super.disableRoom();
	}

	@Override
	public void act(float delta) {
		if (disabled) return;

		super.act(delta);
		stars.rotateBy(-0.25f * delta);
		planet.rotateBy(.75f * delta);
		if (!shipHull.hasActions()) {
			shipHull.addAction(Actions.moveBy(0, ((getHeight() - shipHull.getHeight()) / 2f - shipHull.getY()) * 2f, 1f));
		}
	}

	protected void travel() {
		if (Options.sound)
			travelSound.play(.1f);
		seePlanet(-1);
		if (enemy != null) {
			enemy.remove();
			enemy = null;
		}
		travel.setVisible(true);
		shipHull.setVisible(false);
		belowButtons.setVisible(false);
		buttonsTable.setVisible(false);
	}

	protected void arriveTo(int planetNumber) {
		travelSound.stop();
		travel.setVisible(false);
		shipHull.setVisible(true);
		belowButtons.setVisible(true);
		buttonsTable.setVisible(true);
		seePlanet(planetNumber);
	}

	/**
	 * @param planetNumber -1 to remove the planet
	 */
	protected void seePlanet(int planetNumber) {
		if (planetNumber == -1) {
			disable("takeOff");
			disable("land");
			planet.setDrawable(null);
			return;
		}
		enable("land");

		TextureAtlas.AtlasRegion region = atlas.findRegion("planets/planet", planetNumber);
		if (region != null) {
			planetDrawable.setRegion(region);
			planet.setDrawable(planetDrawable);
			planet.setSize(planet.getPrefWidth(), planet.getPrefHeight());
			planet.setOrigin(planet.getWidth() / 2f, planet.getHeight() / 2f);
			planet.setPosition(getWidth() - planet.getWidth() / 2f, (getHeight() - planet.getHeight()) / 2f);
			planet.clearActions();
		} else {
			planet.setDrawable(null);
		}
	}

	/**
	 * @param enemy -1 to remove the enemy
	 */
	protected void seeEnemy(EnemyShip enemy) {
		if (enemy == null) return;

		this.enemy = enemy;

		addActorBefore(shipHull, enemy);
		enemy.setPosition(getWidth() * .6f - enemy.getWidth() / 2f, getHeight() * .7f - enemy.getHeight() / 2f);
	}

	protected void fireLasers() {
		fireLaser(laser1);
		shipHull.addAction(sequence(
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
		if (enemy == null) return;

		if (Options.sound)
			laserSound.play(.015f);

		float attackOriginX = shipHull.getX() + 44;
		float attackOriginY = shipHull.getY() + 245;

		laser.setPosition(attackOriginX - laser.getWidth() / 2f, attackOriginY - laser.getHeight() / 2f);
		laser.setVisible(true);
		float x = (float) (enemy.getX() + enemy.getWidth() * .25f + Math.random() * enemy.getWidth() * .25f);
		float y = (float) (enemy.getY() + enemy.getHeight() * .35f + Math.random() * enemy.getHeight() * .3f);
		double distance = Vector2.dst2(attackOriginX, attackOriginY, x, y);
		if (y > attackOriginY)
			laser.setRotation(90f - (float) (Math.acos(Vector2.dst2(x, attackOriginY, x, y) / distance) * 180 / Math.PI));
		else
			laser.setRotation(90f + (float) (Math.acos(Vector2.dst2(x, attackOriginY, x, y) / distance) * 180 / Math.PI));
		laser.addAction(sequence(
				moveTo(x, y, (float) (distance / 500f)),
				Actions.run(new Runnable() {
					@Override
					public void run() {
						enemy.takeDamages(ship.getLaserDamages());
						laser.setVisible(false);
						if (Options.sound)
							laserTouchedSound.play(.008f);
					}
				})
		));
	}

	protected void fireRockets() {
		fireRocket(rocket1);
		shipHull.addAction(sequence(
				delay(.1f),
				run(new Runnable() {
					@Override
					public void run() {
						fireRocket(rocket2);
					}
				})
		));
	}

	private void fireRocket(final Rocket rocket) {
		if (enemy == null) return;

		float attackOriginX = shipHull.getX() + 44;
		float attackOriginY = shipHull.getY() + 245;

		rocket.setPosition(attackOriginX - rocket.getWidth() / 2f, attackOriginY - rocket.getHeight() / 2f);
		rocket.launch();
		float x = (float) (enemy.getX() + enemy.getWidth() * .25f + Math.random() * enemy.getWidth() * .3f);
		float y = (float) (enemy.getY() + enemy.getHeight() * .35f + Math.random() * enemy.getHeight() * .3f);
		double distance = Vector2.dst2(attackOriginX, attackOriginY, x, y);
		if (y > attackOriginY)
			rocket.setRotation(-(float) (Math.acos(Vector2.dst2(x, attackOriginY, x, y) / distance) * 180 / Math.PI));
		else
			rocket.setRotation(-(float) (Math.acos(Vector2.dst2(x, attackOriginY, x, y) / distance) * 180 / Math.PI));
		rocket.addAction(sequence(
				moveTo(x, y, (float) (distance / 300f)),
				Actions.run(new Runnable() {
					@Override
					public void run() {
						rocket.explode();
					}
				})
		));

	}
}
