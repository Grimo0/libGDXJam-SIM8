package fr.radnap.sim8.rooms;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

/**
 * @author Radnap
 */
public class Hull extends RepairableRoom {

	private Image stars;
	private Image travel;
	private TextureRegionDrawable planetDrawable;
	private Image planet;
	private Image ship;
	private TextureRegionDrawable enemyDrawable;
	private Image enemy;


	public Hull(TextureAtlas atlas, float width, float height) {
		super("Hull", atlas, width, height, 100);

		addActionButton("takeOff", new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				enable("land");
				disable("takeOff");
				belowButtons.clearChildren();
				belowButtons.add("Leave the planet surface.").row();
			}
		});

		addActionButton("land", new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				enable("takeOff");
				disable("land");
				belowButtons.clearChildren();
				belowButtons.add("Land on the planet.").row();
			}
		});

		stars = new Image(atlas.findRegion("stars"));
		stars.setSize(2 * width * stars.getWidth() / stars.getHeight(), 2 * width);
		stars.setOrigin(stars.getWidth() / 2f, stars.getHeight() / 2f);
		stars.setPosition(width - stars.getWidth() / 2f, (height - stars.getHeight()) / 2f);
		addActorBefore(aboveButtons, stars);

		travel = new Image(atlas.findRegion("travel"));
		travel.setVisible(false);
		travel.setPosition((width - travel.getWidth()) / 2f, (height - travel.getHeight()) / 2f);
		addActorBefore(aboveButtons, travel);

		planetDrawable = new TextureRegionDrawable();
		planet = new Image();
		addActorBefore(aboveButtons, planet);

		ship = new Image(atlas.findRegion("blueship"));
		ship.setPosition(5f, height - ship.getHeight() + 1f);
		addActorBefore(aboveButtons, ship);

		enemyDrawable = new TextureRegionDrawable();
		enemy = new Image();
		enemy.setRotation(90f);
		addActorBefore(aboveButtons, enemy);
	}


	@Override
	public void initialize() {
	}

	@Override
	public void act(float delta) {
		super.act(delta);
		if (!stars.hasActions()) {
			stars.addAction(Actions.rotateBy(-.5f, 2f));
			planet.addAction(Actions.rotateBy(1.5f, 2f));
		}
		if (!ship.hasActions()) {
			ship.addAction(Actions.moveBy(0, (getHeight() - ship.getHeight() - ship.getY()) * 2f, 1f));
			enemy.addAction(Actions.moveBy(0, (getHeight() * .7f - enemy.getWidth() * .5f - enemy.getY()) * 2f, 1f));
			Action sequence = Actions.sequence(Actions.moveBy(((getWidth() + enemy.getHeight()) / 2f - enemy.getX()) * 2f, 0, .5f),
					Actions.moveBy(-((getWidth() + enemy.getHeight()) / 2f - enemy.getX()) * 2f, 0, .5f));
			enemy.addAction(sequence);
		}
	}

	public void travel() {
		seePlanet(-1);
		seeEnemy(-1);
		travel.setVisible(true);
		ship.setVisible(false);
		belowButtons.setVisible(false);
		buttonsGroup.setVisible(false);
	}

	public void arriveTo(int planetNumber) {
		travel.setVisible(false);
		ship.setVisible(true);
		belowButtons.setVisible(true);
		buttonsGroup.setVisible(true);
		seePlanet(planetNumber);
	}

	/**
	 * @param number -1 to remove the planet
	 */
	public void seePlanet(int number) {
		if (number == -1) {
			disable("takeOff");
			disable("land");
			planet.setDrawable(null);
			return;
		}
		enable("land");

		TextureAtlas.AtlasRegion region = atlas.findRegion("planets/planet", number);
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
	 * @param enemyType -1 to remove the enemy
	 */
	public void seeEnemy(int enemyType) {
		if (enemyType == -1) {
			enemy.setDrawable(null);
			return;
		}

		TextureAtlas.AtlasRegion region = atlas.findRegion("enemy", enemyType);
		if (region != null) {
			enemyDrawable.setRegion(region);
			enemy.setDrawable(enemyDrawable);
			enemy.setSize(enemy.getPrefWidth(), enemy.getPrefHeight());
			enemy.setPosition((getWidth() + enemy.getHeight()) / 2f + .5f, getHeight() * .7f - enemy.getWidth() * .5f + 1f);
			enemy.clearActions();
		}
	}
}
