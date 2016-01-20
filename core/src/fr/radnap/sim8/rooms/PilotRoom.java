package fr.radnap.sim8.rooms;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import fr.radnap.sim8.EnemyShip;
import fr.radnap.sim8.PlayerShip;
import fr.radnap.sim8.Star;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.addAction;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveBy;

/**
 * @author Radnap
 */
public class PilotRoom extends Room {

	private boolean canMove;

	private Group map;
	private Star destination;
	private TextureRegionDrawable destinationDrawable;
	private TextureRegionDrawable starDrawable;
	private TextureRegionDrawable starVisitedDrawable;
	private Color[] colors;
	private float[] sizes;

	private Image reachable;
	private Array<Star> stars;
	private Star currentStar;
	private Animation currentAnimation;
	private TextureRegionDrawable currentDrawable;
	private Image current;

	private boolean travelling;


	public PilotRoom(PlayerShip ship, TextureAtlas atlas, AssetManager assetManager, float width, float height) {
		super(ship, "PilotRoom", atlas, assetManager, width, height);
		travelling = false;

		canMove = false;
		map = new Group();
		addActorBefore(aboveButtons, map);

		Image nebula = new Image(atlas.findRegion("nebula"));
		map.addActor(nebula);
		map.setSize(nebula.getPrefWidth(), nebula.getPrefHeight());

		destinationDrawable = new TextureRegionDrawable(atlas.findRegion("destination"));
		starDrawable = new TextureRegionDrawable(atlas.findRegion("star"));
		starVisitedDrawable = new TextureRegionDrawable(atlas.findRegion("starVisited"));

		colors = new Color[5];
		colors[0] = Color.WHITE;
		colors[1] = new Color(1f, .71f, .73f, 1f);
		colors[2] = new Color(1f, .91f, .71f, 1f);
		colors[3] = new Color(.75f, .82f, 1f, 1f);
		colors[4] = new Color(.75f, 1f, .71f, 1f);

		sizes = new float[3];
		sizes[0] = .7f;
		sizes[1] = 1f;
		sizes[2] = 1.3f;

		reachable = new Image(atlas.findRegion("reachable"));
		reachable.setSize(reachable.getWidth() * .9f, reachable.getHeight() * .9f);
		reachable.setOrigin(reachable.getWidth() / 2f, reachable.getHeight() / 2f);
		map.addActor(reachable);

		generateStars(assetManager);
		currentStar = stars.get(0);
		currentStar.setEnemyShip(null);

		currentAnimation = new Animation(0.2f, atlas.findRegions("currentPosition"));
		currentDrawable = new TextureRegionDrawable(currentAnimation.getKeyFrame(0));
		current = new Image(currentDrawable);
		current.setPosition((currentStar.getX() + (currentStar.getWidth() - current.getWidth()) / 2f), currentStar.getY() + currentStar.getHeight());
		reachable.setPosition(currentStar.getX() + (currentStar.getWidth() - reachable.getWidth()) / 2f,
				currentStar.getY() + (currentStar.getHeight() - reachable.getHeight()) / 2f);
		map.addActor(current);

		// TUTORIAL
		map.addAction(sequence(
				delay(.5f),
				moveTo(Math.max(getWidth() - map.getWidth(), getWidth() / 2f - destination.getX()),
						Math.max(getHeight() - map.getHeight(), getHeight() / 2f - destination.getY()), 2f),
				delay(2f),
				moveTo(0, 0, 3f),
				run(new Runnable() {
					@Override
					public void run() {
						canMove = true;
					}
				})
		));
		belowButtons.add("You need to reach the destination").row();
		belowButtons.add("with the crew alive.").row();
	}


	@Override
	public void initialize() {
		arriveTo(currentStar);
	}

	@Override
	public void act(float delta) {
		super.act(delta);
		currentDrawable.setRegion(currentAnimation.getKeyFrame(stateTime, true));
		reachable.rotateBy(2f * delta);
	}

	private void generateStars(AssetManager assetManager) {
		stars = new Array<>();
		Star star = null;
		int size = (int) (20 + Math.random() * 10);
		int random = 5;
		for (int i = 0; i < size; i++) {
			EnemyShip enemyShip = null;
			if (Math.random() < 0.7f)
				enemyShip = new EnemyShip(atlas, assetManager, ship, (int) (Math.random() * 8 + 1));
			star = new Star(starDrawable, (int) (Math.random() * 30 + 1), enemyShip);
			star.setPosition(10f + (10 - random + 2 * i) * 20f - star.getWidth() / 2f,
					10f + (random + i + 3) * 20f - star.getHeight() / 2f);
			star.addListener(new ClickListener() {
				@Override
				public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
					Star star = (Star) event.getListenerActor();
					if (!canMove || Math.sqrt(distanceSq(star)) > .94f * reachable.getWidth() / 2f)
						return;

					super.enter(event, x, y, pointer, fromActor);
					event.getListenerActor().sizeBy(4f, 4f);
					event.getListenerActor().moveBy(-2f, -2f);
				}

				@Override
				public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
					Star star = (Star) event.getListenerActor();
					if (!canMove || Math.sqrt(distanceSq(star)) > .94f * reachable.getWidth() / 2f)
						return;

					super.exit(event, x, y, pointer, toActor);
					event.getListenerActor().sizeBy(-4f, -4f);
					event.getListenerActor().moveBy(2f, 2f);
				}

				@Override
				public void clicked(InputEvent event, float x, float y) {
					Star star = (Star) event.getListenerActor();
					if (!canMove || Math.sqrt(distanceSq(star)) > .94f * reachable.getWidth() / 2f)
						return;

					if (!travelling)
						leaveFor(star);
				}
			});
			star.addListener(buttonSoundClickListener);
			map.addActor(star);
			stars.add(star);

			random = (int) (Math.random() * 10);
		}
		destination = star;
		assert destination != null;
		destination.setDrawable(destinationDrawable);
	}

	public void moveToClosestStar() {
		if (!canMove) return;

		leaveFor(closestStar());
	}

	public Star closestStar() {
		float shortest = Float.MAX_VALUE;
		Star closest = null;
		float distanceSq;
		for (Star star : stars) {
			if (star == currentStar) continue;

			distanceSq = distanceSq(star);
			if (distanceSq < shortest) {
				shortest = distanceSq;
				closest = star;
			}
		}
		return closest;
	}

	private void leaveFor(final Star star) {
		if (!canMove || star == currentStar) return;

		if (currentStar.getEnemyShip() != null) {
			belowButtons.clearChildren();
			final Label leaveLabel = belowButtons.add("We can't leave while we're attacked.").getActor();
			addAction(sequence(delay(5f, run(new Runnable() {
				@Override
				public void run() {
					leaveLabel.remove();
				}
			}))));
			return;
		}

		ControlRoom controlRoom = (ControlRoom) rooms.get("ControlRoom");
		double distance = Math.sqrt(distanceSq(star));
		if (!controlRoom.canUse(controlRoom.costFor(distance))) {
			belowButtons.clearChildren();
			final Label leaveLabel = belowButtons.add("We don't have enough resources.").getActor();
			addAction(sequence(delay(5f, run(new Runnable() {
				@Override
				public void run() {
					leaveLabel.remove();
				}
			}))));
			return;
		}

		controlRoom.use(controlRoom.costFor(distance));

		travelling = true;

		current.clearActions();
		float duration = (float) (distance / 150f);
		current.addAction(sequence(
				moveTo(star.getX() + (star.getWidth() - current.getWidth()) / 2f,
						star.getY() + star.getHeight(), duration),
				run(new Runnable() {
					@Override
					public void run() {
						arriveTo(star);
					}
				})));
		reachable.addAction(moveTo(star.getX() + (star.getWidth() - reachable.getWidth()) / 2f,
				star.getY() + (star.getHeight() - reachable.getHeight()) / 2f, duration));
		map.addAction(moveTo(
				MathUtils.clamp(map.getX() + (currentStar.getX() - star.getX()) * .5f, getWidth() - map.getWidth(), 0),
				MathUtils.clamp(map.getY() + (currentStar.getY() - star.getY()) * .5f, getHeight() - map.getHeight(), 0),
				duration));

		((Hull) rooms.get("Hull")).travel();
		controlRoom.leavePlanet();
	}

	private void arriveTo(Star star) {
		travelling = false;

		if (star.getDrawable() != starVisitedDrawable) {
			star.setDrawable(starVisitedDrawable);
			float sizeFactor = sizes[(int) (Math.random() * sizes.length)];
			star.moveBy(star.getWidth() * (1 - sizeFactor) / 2f, star.getHeight() * (1 - sizeFactor) / 2f);
			star.setSize(star.getWidth() * sizeFactor, star.getHeight() * sizeFactor);
			star.setColor(colors[(int) (Math.random() * colors.length)]);
		}

		currentStar = star;

		((Hull) rooms.get("Hull")).arriveTo(star.getPlanetNumber());
		ControlRoom controlRoom = (ControlRoom) rooms.get("ControlRoom");
		controlRoom.arriveToPlanet(currentStar);
		controlRoom.encounterEnemy(star.getEnemyShip());
		if (hasArrived()) {
			ship.ending();
			return;
		}

		Star closestStar = closestStar();
		if (controlRoom.costFor(Math.sqrt(distanceSq(closestStar))) > controlRoom.getResources() + currentStar.getResources())
			ship.gameOver("You have no more resources, these planet will be the last you'll ever see.");
	}

	public boolean hasArrived() {
		return currentStar.getEnemyShip() == null && currentStar == destination;
	}

	protected float distanceSq(Star star) {
		return (star.getX() + star.getWidth() / 2f - currentStar.getX() - currentStar.getWidth() / 2f) *
				(star.getX() + star.getWidth() / 2f - currentStar.getX() - currentStar.getWidth() / 2f)
				+ (star.getY() + star.getHeight() / 2f - currentStar.getY() - currentStar.getHeight() / 2f) *
				(star.getY() + star.getHeight() / 2f - currentStar.getY() - currentStar.getHeight() / 2f);
	}
}
