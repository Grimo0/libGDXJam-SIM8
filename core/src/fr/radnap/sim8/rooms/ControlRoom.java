package fr.radnap.sim8.rooms;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import fr.radnap.sim8.EnemyShip;
import fr.radnap.sim8.PlayerShip;

/**
 * @author Radnap
 */
public class ControlRoom extends RepairableRoom {

	private Animation loading;

	private float laserTime;
	private TextureRegionDrawable laserLoadingDrawable;
	private Image laserLoading;

	private float rocketsTime;
	private TextureRegionDrawable rocketsLoadingDrawable;
	private Image rocketsLoading;

	private EnemyShip enemyShip;


	public ControlRoom(PlayerShip ship, TextureAtlas atlas, AssetManager assetManager, float width, float height) {
		super(ship, "ControlRoom", atlas, assetManager, width, height, .5f);

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

		addActionButton("leave", new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				((PilotRoom) rooms.get("PilotRoom")).moveToClosestStar();
			}
		});

		validate();

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
	public void initialize() {
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


	public void arriveToPlanet() {
		enable("leave");
	}

	public void leavePlanet() {
		endFight();
	}

	public void encounterEnemy(EnemyShip enemyShip) {
		if (enemyShip == null) return;

		this.enemyShip = enemyShip;

		((Hull) rooms.get("Hull")).seeEnemy(enemyShip);

		enable("laser");
		enable("rockets");

		belowButtons.clearChildren();
		Cell<Label> add = belowButtons.add("An enemy's ship is attacking us !");
		add.getActor().setColor(Color.SCARLET);
		add.row();
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
}
