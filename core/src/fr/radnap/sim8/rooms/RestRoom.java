package fr.radnap.sim8.rooms;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import fr.radnap.sim8.PlayerShip;

/**
 * @author Radnap
 */
public class RestRoom extends Room {

	private int stressLevel;

	private Animation okAnimation;
	private Animation cautionAnimation;
	private Animation dangerAnimation;
	private Animation currentAnimation;
	private TextureRegionDrawable ekgDrawable;
	private Image ekg;

	private Animation loading;
	private float healLoadingTime;
	private TextureRegionDrawable healLoadingDrawable;
	private Image healLoading;


	public RestRoom(PlayerShip ship, TextureAtlas atlas, AssetManager assetManager, float width, float height) {
		super(ship, "RestRoom", atlas, assetManager, width, height);

		Button heal = addActionButton("heal", new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				decreaseStress();
			}
		});

		validate();

		loading = new Animation(.05f, atlas.findRegions("buttons/loading"));
		healLoadingTime = 0f;
		healLoadingDrawable = new TextureRegionDrawable(loading.getKeyFrame(0f));
		healLoading = new Image((Drawable) null);
		healLoading.setTouchable(Touchable.disabled);
		healLoading.setPosition(buttonsTable.getX() + heal.getX(), buttonsTable.getY() + heal.getY());
		addActorAfter(buttonsTable, healLoading);

		stressLevel = 1;

		okAnimation = new Animation(.1f, atlas.findRegions("restRoom/ekgOk"), Animation.PlayMode.LOOP);
		cautionAnimation = new Animation(.1f, atlas.findRegions("restRoom/ekgCaution"), Animation.PlayMode.LOOP);
		dangerAnimation = new Animation(.1f, atlas.findRegions("restRoom/ekgDanger"), Animation.PlayMode.LOOP);

		ekgDrawable = new TextureRegionDrawable(okAnimation.getKeyFrame(0f));
		ekg = new Image(ekgDrawable);
		belowButtons.top().left();
		belowButtons.add(ekg).width(4f*ekg.getPrefWidth()).height(4f*ekg.getPrefHeight()).pad(5f).left();
	}


	@Override
	public void initialize() {
		super.initialize();
		if(stressLevel > 8) currentAnimation = dangerAnimation;
		else if (stressLevel > 4) currentAnimation = cautionAnimation;
		else currentAnimation = okAnimation;
	}

	@Override
	public void act(float delta) {
		super.act(delta);
		ekgDrawable.setRegion(currentAnimation.getKeyFrame(stateTime));
		if (healLoadingTime > 0) {
			healLoadingTime -= delta;
			if (healLoadingTime < 0) {
				healLoadingTime = 0;
				healLoading.setDrawable(null);
				if (stressLevel > 1)
					enable("heal");
			} else {
				healLoadingDrawable.setRegion(loading.getKeyFrame(stateTime, true));
			}
		}
	}

	public void increaseStress() {
		stressLevel++;
		if (stressLevel > 12) ship.gameOver("The crew died.");
		else if(stressLevel > 8) currentAnimation = dangerAnimation;
		else if (stressLevel > 4) currentAnimation = cautionAnimation;

		okAnimation.setFrameDuration(okAnimation.getFrameDuration() - .007f);
		cautionAnimation.setFrameDuration(cautionAnimation.getFrameDuration() - .007f);
		dangerAnimation.setFrameDuration(dangerAnimation.getFrameDuration() - .007f);

		enable("heal");
	}

	public void decreaseStress() {
		if (stressLevel == 1) return;

		stressLevel--;
		if (stressLevel <= 4) currentAnimation = okAnimation;
		else if (stressLevel <= 8) currentAnimation = cautionAnimation;

		okAnimation.setFrameDuration(okAnimation.getFrameDuration() + .007f);
		cautionAnimation.setFrameDuration(cautionAnimation.getFrameDuration() + .007f);
		dangerAnimation.setFrameDuration(dangerAnimation.getFrameDuration() + .007f);

		disable("heal");
		healLoadingTime = 2f;
		if (stressLevel == 1) return;

		healLoading.setDrawable(healLoadingDrawable);
		healLoading.setSize(healLoading.getPrefWidth(), healLoading.getPrefHeight());
	}
}
