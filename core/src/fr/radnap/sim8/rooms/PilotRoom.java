package fr.radnap.sim8.rooms;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.run;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

/**
 * @author Radnap
 */
public class PilotRoom extends Room {

	private Group map;
	private TextureRegionDrawable starDrawable;
	private TextureRegionDrawable starDrawableVisited;
	private Color[] colors;
	private float[] sizes;
	private Array<Star> stars;
	private Star currentStar;
	private Animation currentAnimation;
	private TextureRegionDrawable currentDrawable;
	private Image current;

	private class Star extends Image {
		private int planetNumber;

		public Star(Drawable drawable, int planetNumber) {
			super(drawable);
			this.planetNumber = planetNumber;
		}
	}


	public PilotRoom(TextureAtlas atlas, float width, float height) {
		super("PilotRoom", atlas, width, height);

		map = new Group();
		addActorBefore(aboveButtons, map);

		map.addActor(new Image(atlas.findRegion("nebula")));

		starDrawable = new TextureRegionDrawable(atlas.findRegion("star"));
		starDrawableVisited = new TextureRegionDrawable(atlas.findRegion("starVisited"));

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

		generateStars();
		currentStar = stars.get(0);

		currentAnimation = new Animation(0.2f, atlas.findRegions("currentPosition"));
		currentDrawable = new TextureRegionDrawable(currentAnimation.getKeyFrame(0));
		current = new Image(currentDrawable);
		current.setPosition((currentStar.getX() + (currentStar.getWidth() - current.getWidth()) / 2f), currentStar.getY() + currentStar.getHeight());
		map.addActor(current);

	}


	@Override
	public void initialize() {
		arriveTo(currentStar);
	}

	@Override
	public void act(float delta) {
		super.act(delta);
		currentDrawable.setRegion(currentAnimation.getKeyFrame(stateTime, true));
	}

	private void generateStars() {
		stars = new Array<>();
		Star star;
		int size = (int) (20 + Math.random() * 10);
		int random = 5;
		for (int i = 0; i < size; i++) {
			star = new Star(this.starDrawable, (int) (Math.random() * 30 + 1));
			star.setPosition(10f + (10 - random + 2 * i) * 20f - star.getWidth() / 2f,
					10f + (random + i + 3) * 20f - star.getHeight() / 2f);
			star.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					leaveFor((Star) event.getListenerActor());
				}
			});
			map.addActor(star);
			stars.add(star);

			random = (int) (Math.random() * 10);
		}
	}

	public boolean hasArrived() {
		return !current.hasActions();
	}

	public void moveToClosestStar() {
		float shortest = Float.MAX_VALUE;
		Star closest = null;
		float distanceSq;
		for (Star star : stars) {
			if (star == currentStar) continue;

			distanceSq = (star.getX() + star.getWidth() / 2f - currentStar.getX() - currentStar.getWidth() / 2f) *
					(star.getX() + star.getWidth() / 2f - currentStar.getX() - currentStar.getWidth() / 2f)
					+ (star.getY() + star.getHeight() / 2f - currentStar.getY() - currentStar.getHeight() / 2f) *
					(star.getY() + star.getHeight() / 2f - currentStar.getY() - currentStar.getHeight() / 2f);
			if (distanceSq < shortest) {
				shortest = distanceSq;
				closest = star;
			}
		}
		leaveFor(closest);
	}

	private void leaveFor(final Star star) {
		float distance = (star.getX() + star.getWidth() / 2f - currentStar.getX() - currentStar.getWidth() / 2f) *
				(star.getX() + star.getWidth() / 2f - currentStar.getX() - currentStar.getWidth() / 2f)
				+ (star.getY() + star.getHeight() / 2f - currentStar.getY() - currentStar.getHeight() / 2f) *
				(star.getY() + star.getHeight() / 2f - currentStar.getY() - currentStar.getHeight() / 2f);
		distance = (float) Math.sqrt(distance);
		current.clearActions();
		current.addAction(sequence(
				moveTo((star.getX() + (star.getWidth() - current.getWidth()) / 2f), star.getY() + star.getHeight(), distance / 50f),
				run(new Runnable() {
					@Override
					public void run() {
						arriveTo(star);
					}
				})));

		((Hull) rooms.get("Hull")).travel();
		((ControlRoom) rooms.get("ControlRoom")).leavePlanet();
	}

	private void arriveTo(Star star) {
		if (star.getDrawable() != starDrawableVisited) {
			star.setDrawable(starDrawableVisited);
			float sizeFactor = sizes[(int) (Math.random() * sizes.length)];
			star.moveBy(star.getWidth() * (1 - sizeFactor) / 2f, star.getHeight() * (1 - sizeFactor) / 2f);
			star.setSize(star.getWidth() * sizeFactor, star.getHeight() * sizeFactor);
			star.setColor(colors[(int) (Math.random() * colors.length)]);
		}

		currentStar = star;

		((Hull) rooms.get("Hull")).arriveTo(star.planetNumber);
		((ControlRoom) rooms.get("ControlRoom")).arriveToPlanet();
	}
}
