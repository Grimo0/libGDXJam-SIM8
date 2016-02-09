package fr.radnap.sim8.rooms;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import fr.radnap.sim8.EnemyShip;
import fr.radnap.sim8.PlayerShip;
import fr.radnap.sim8.Star;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveBy;

/**
 * @author Radnap
 */
public class ControlRoom extends RepairableRoom {

	private Color warningColor;
	private final Label conso;
	private int resources;
	private Label resourcesLabel;

	private Animation loading;

	private float laserTime;
	private TextureRegionDrawable laserLoadingDrawable;
	private Image laserLoading;

	private float rocketsTime;
	private TextureRegionDrawable rocketsLoadingDrawable;
	private Image rocketsLoading;
	private Star currentStar;


	public ControlRoom(final PlayerShip ship, TextureAtlas atlas, AssetManager assetManager, float width, float height) {
		super(ship, "ControlRoom", atlas, assetManager, width, height, .5f);

		warningColor = new Color(0xff2010ff);

		resources = 85;
		resourcesLabel = aboveButtons.add(resources + "r", "number").padRight(10f).right().expandX().getActor();
		aboveButtons.row();
		conso = aboveButtons.add("", "number").space(1f).colspan(100).padRight(25f).right().getActor();
		conso.setColor(Color.SCARLET);
		conso.getColor().a = 0f;
		aboveButtons.row();

		Button laser = addActionButton("laser", new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				((Hull) rooms.get("Hull")).fireLasers();
				laserTime = 1f;
				laserLoading.setDrawable(laserLoadingDrawable);
				laserLoading.setSize(laserLoading.getPrefWidth(), laserLoading.getPrefHeight());
				disable("laser");
			}
		}, null);

		Button rockets = addActionButton("rockets", new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				((Hull) rooms.get("Hull")).fireRockets();
				rocketsTime = 5f;
				rocketsLoading.setDrawable(rocketsLoadingDrawable);
				rocketsLoading.setSize(rocketsLoading.getPrefWidth(), rocketsLoading.getPrefHeight());
				disable("rockets");
			}
		}, null);

		addActionButton("harvest", new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if (currentStar == null) return;

				if (currentStar.getEnemyShip() != null) {
					final Cell<Label> leaveLabel = ship.print("Impossible for now.", false);
					addAction(sequence(delay(5f, run(new Runnable() {
						@Override
						public void run() {
							leaveLabel.clearActor();
							belowButtons.getCells().removeValue(leaveLabel, false);
						}
					}))));
					return;
				}

				int taken = currentStar.takeResources();
				if (taken == 0) return;

				recolt(taken);

				if (!currentStar.hasResources())
					disable("harvest");
			}
		});

		validate();

		belowButtons.setBackground(new NinePatchDrawable(atlas.createPatch("textBox")));
		belowButtons.top().left().pad(10f, 7f, 10f, 7f);
		belowButtons.defaults().left().space(5f);
		belowButtons.setSize(width - 9f, 90f);
		belowButtons.setPosition(5f, 5f);

		buttonsTable.setY(belowButtons.getY() + belowButtons.getHeight() + belowButtons.getPadTop());

		loading = new Animation(.05f, atlas.findRegions("buttons/loading"));

		laserLoadingDrawable = new TextureRegionDrawable(loading.getKeyFrame(0));
		laserLoading = new Image((Drawable) null);
		laserLoading.setTouchable(Touchable.disabled);
		laserLoading.setPosition(buttonsTable.getX() + laser.getX(), buttonsTable.getY() + laser.getY());
		addActorAfter(buttonsTable, laserLoading);

		rocketsLoadingDrawable = new TextureRegionDrawable(loading.getKeyFrame(0));
		rocketsLoading = new Image((Drawable) null);
		rocketsLoading.setTouchable(Touchable.disabled);
		rocketsLoading.setPosition(buttonsTable.getX() + rockets.getX(), buttonsTable.getY() + rockets.getY());
		addActorAfter(buttonsTable, rocketsLoading);
	}


	@Override
	public void act(float delta) {
		super.act(delta);

		if (laserTime > 0) {
			laserTime -= delta;
			if (laserTime < 0) {
				laserTime = 0;
				laserLoading.setDrawable(null);
				enable("laser");
			} else {
				laserLoadingDrawable.setRegion(loading.getKeyFrame(stateTime, true));
			}
		}

		if (rocketsTime > 0) {
			rocketsTime -= delta;
			if (rocketsTime < 0) {
				rocketsTime = 0;
				rocketsLoading.setDrawable(null);
				enable("rockets");
			} else {
				rocketsLoadingDrawable.setRegion(loading.getKeyFrame(stateTime, true));
			}
		}
	}

	public int getResources() {
		return resources;
	}

	public int costFor(double distance) {
		return (int) (distance / 20f);
	}

	public boolean canUse(int amount) {
		return amount < resources;
	}

	public int use(int amount) {
		if (resources <= amount) {
			amount = resources;
			resources = 0;
			resourcesLabel.setColor(warningColor);
		} else {
			resources -= amount;
			resourcesLabel.setColor(Color.WHITE);
		}

		resourcesLabel.setText(resources + "r");
		conso.setText("-" + amount);
		conso.clearActions();
		conso.addAction(sequence(
				parallel(fadeIn(.1f), Actions.moveBy(0, 3f, .1f)),
				Actions.moveBy(0, -3f, .1f),
				delay(1f),
				run(new Runnable() {
					@Override
					public void run() {
						conso.setText("");
						conso.getColor().a = 0f;
					}
				})
		));

		return amount;
	}

	public void recolt(int amount) {
		resources += amount;
		resourcesLabel.setColor(Color.WHITE);

		resourcesLabel.setText(resources + "r");
		conso.setText("-" + amount);
		conso.clearActions();
		conso.addAction(sequence(
				parallel(fadeIn(.1f), Actions.moveBy(0, 3f, .1f)),
				Actions.moveBy(0, -3f, .1f),
				delay(1f),
				run(new Runnable() {
					@Override
					public void run() {
						conso.setText("");
						conso.getColor().a = 0f;
					}
				})
		));
	}

	public void arriveToPlanet(Star star) {
		enable("leave");
		currentStar = star;
		if (currentStar.hasResources())
			enable("harvest");
	}

	public void leavePlanet() {
		endFight();
		currentStar = null;
	}

	public void encounterEnemy(EnemyShip enemyShip) {
		if (enemyShip == null) return;

		((Hull) rooms.get("Hull")).seeEnemy(enemyShip);

		enable("laser");
		enable("rockets");

//		belowButtons.clearChildren();
		print("An enemy's ship is attacking us !", true);
	}

	public void endFight() {
		laserTime = 0f;
		laserLoading.setDrawable(null);
		disable("laser");

		rocketsTime = 0f;
		rocketsLoading.setDrawable(null);
		disable("rockets");

		belowButtons.clearChildren();
	}

	public Cell<Label> print(String s, boolean warning) {
		Cell<Label> add = belowButtons.add(s);
		if (warning)
			add.getActor().setColor(warningColor);
		add.row();
		return add;
	}
}
