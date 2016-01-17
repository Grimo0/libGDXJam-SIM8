package fr.radnap.sim8;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

/**
 * @author Radnap
 */
public class Rocket extends Image {

	private final Ship target;
	private final Sound launchSound;
	private final Sound explodeSound;
	private TextureRegion rocketRegion;
	private Animation explosionAnimation;
	private TextureRegionDrawable explosionDrawable;
	private float explosionTime;


	public Rocket(TextureRegion rocketRegion, Ship target, Animation explosionAnimation, Sound launchSound, Sound explodeSound) {
		super((Drawable) null);
		this.target = target;
		this.rocketRegion = rocketRegion;
		this.explosionAnimation = explosionAnimation;
		explosionDrawable = new TextureRegionDrawable();
		explosionTime = -1f;
		this.launchSound = launchSound;
		this.explodeSound = explodeSound;
	}


	@Override
	public void act(float delta) {
		super.act(delta);

		if (explosionTime >= 0f) {
			if (explosionAnimation.isAnimationFinished(explosionTime)) {
				explosionTime = -1f;
				setDrawable(null);
				if (target != null)
					target.takeDamages(target.getRocketDamages());
			} else {
				explosionDrawable.setRegion(explosionAnimation.getKeyFrame(explosionTime));
				explosionTime += delta;
			}
		}
	}

	public void launch() {
		if (Options.sound)
			launchSound.play(.1f);

		explosionDrawable.setRegion(rocketRegion);
		setDrawable(explosionDrawable);
		setSize(getPrefWidth(), getPrefHeight());
	}

	public void explode() {
		if (Options.sound)
			explodeSound.play(.04f);

		explosionTime = 0f;
		explosionDrawable.setRegion(explosionAnimation.getKeyFrame(0f));
		setDrawable(explosionDrawable);
		setSize(getPrefWidth(), getPrefHeight());
	}
}