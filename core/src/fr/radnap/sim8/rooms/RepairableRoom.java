package fr.radnap.sim8.rooms;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

/**
 * @author Radnap
 */
public abstract class RepairableRoom extends Room {

	private final NinePatchDrawable statusBarDrawable;
	private final NinePatchDrawable statusBarLowDrawable;
	private float status;
	private Image statusBar;


	public RepairableRoom(String name, TextureAtlas atlas, float width, float height, final float repairCost) {
		super(name, atlas, width, height);
		status = 1f;

		statusBarDrawable = new NinePatchDrawable(atlas.createPatch("statusBar"));
		statusBarLowDrawable = new NinePatchDrawable(atlas.createPatch("statusBarLow"));
		statusBar = new Image(statusBarDrawable);
		aboveButtons.add(statusBar).width(getWidth() - 9f).height(9f);

		addActionButton("repair", new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				float repaired = ((StoreRoom) rooms.get("StoreRoom")).use((int) (.1f * repairCost)) / repairCost;

				if (status <= 0.25f && status + repaired > 0.25f) {
					statusBar.setDrawable(statusBarDrawable);
					aboveButtons.getCell(statusBar).height(9f);
					aboveButtons.invalidate();
				}
				status += repaired;
				if (status > 1f)
					status = 1f;
				if (status <= 0.25f) {
					aboveButtons.getCell(statusBar).height(9f * (1f + (0.25f - status) / 0.25f));
					aboveButtons.invalidate();
				}
			}
		});
	}


	public float getStatus() {
		return status;
	}

	public void takeDamage(float damage) {
		status -= damage;
		if (status < 0f)
			status = 0f;
		if (status <= 0.25f) {
			statusBar.setDrawable(statusBarLowDrawable);
			aboveButtons.getCell(statusBar).height(9f * (1f + (0.25f - status) / 0.25f));
			aboveButtons.invalidate();
		}
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
