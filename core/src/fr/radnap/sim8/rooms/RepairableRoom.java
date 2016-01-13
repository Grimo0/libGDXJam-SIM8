package fr.radnap.sim8.rooms;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

/**
 * @author Radnap
 */
public abstract class RepairableRoom extends Room {

	private final TextureRegionDrawable statusBarDrawable;
	private final TextureRegionDrawable statusBarLowDrawable;
	private float status;
	private Image statusBar;


	public RepairableRoom(String name, TextureAtlas atlas, float width, float height, final int repairCost) {
		super(name, atlas, width, height);
		status = 1f;

		statusBarDrawable = new TextureRegionDrawable(atlas.findRegion("statusBar"));
		statusBarLowDrawable = new TextureRegionDrawable(atlas.findRegion("statusBarLow"));
		statusBar = new Image(statusBarDrawable);
		aboveButtons.add(statusBar)
				.width(getWidth() - 10f).height(5f);

		addActionButton("repair", new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				int used = ((StoreRoom) rooms.get("StoreRoom")).use((int) (.1f * repairCost));

				if (status <= 0.25f && status + used > 0.25f) {
					statusBar.setDrawable(statusBarDrawable);
					aboveButtons.padTop(aboveButtons.getPadTop() - .5f * aboveButtons.getPrefHeight())
							.padBottom(aboveButtons.getPadBottom() + .5f * aboveButtons.getPrefHeight());
					statusBar.setScaleY(1f);
					invalidate();
				}
				status += used / ((float) repairCost);
				if (status > 1f)
					status = 1f;
			}
		});
	}


	public float getStatus() {
		return status;
	}

	@Override
	public void takeDamage(float damage) {
		if (status > 0.25f && status - damage <= 0.25f) {
			statusBar.setDrawable(statusBarLowDrawable);
			aboveButtons.padTop(aboveButtons.getPadTop() + .5f * aboveButtons.getPrefHeight())
					.padBottom(aboveButtons.getPadBottom() - .5f * aboveButtons.getPrefHeight());
			statusBar.setScaleY(2f);
			invalidate();
		}
		status -= damage;
		if (status < 0f)
			status = 0f;
	}

	@Override
	public void act(float delta) {
		super.act(delta);
		if (status == 1f)
			disable("repair");
		else
			enable("repair");
		statusBar.setScaleX(Math.max(status, 0.001f));
	}
}
