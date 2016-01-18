package fr.radnap.sim8.rooms;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import fr.radnap.sim8.PlayerShip;
import fr.radnap.sim8.SIM8;

/**
 * @author Radnap
 */
public abstract class RepairableRoom extends Room {

	private final NinePatchDrawable statusBarDrawable;
	private final NinePatchDrawable statusBarLowDrawable;
	private final float repairCost;
	private int status;
	private Image statusBar;
	private final Button repairButton;


	/**
	 * @param repairCost for 1%.
	 */
	public RepairableRoom(PlayerShip ship, String name, TextureAtlas atlas, AssetManager assetManager, float width, float height, float repairCost) {
		super(ship, name, atlas, assetManager, width, height);
		status = 100;
		this.repairCost = repairCost;

		statusBarDrawable = new NinePatchDrawable(atlas.createPatch("statusBar"));
		statusBarLowDrawable = new NinePatchDrawable(atlas.createPatch("statusBarLow"));
		statusBar = new Image(statusBarDrawable);
		aboveButtons.add(statusBar).width(getWidth() - 9f).height(9f).colspan(100).row();

		Button.ButtonStyle style = new Button.ButtonStyle();
		style.up = new TextureRegionDrawable(atlas.findRegion("buttons/repairButtonUp"));
		style.down = new TextureRegionDrawable(atlas.findRegion("buttons/repairButtonDown"));
		style.disabled = new TextureRegionDrawable(atlas.findRegion("buttons/repairButtonDisable"));
		repairButton = new Button(style);
		repairButton.setDisabled(true);
		repairButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				repair();
			}
		});
		repairButton.addListener(buttonSoundClickListener);

		aboveButtons.add(repairButton).pad(5f).left();
	}


	public float getStatus() {
		return status;
	}

	public void takeDamages(int damage) {
		status -= damage;
		if (status <= 0) {
			status = 0;
			disableRoom();
		}
		if (status <= 25) {
			statusBar.setDrawable(statusBarLowDrawable);
//			aboveButtons.getCell(statusBar).height(9f * 1.5f);
//			aboveButtons.invalidate();
		}
		repairButton.setDisabled(false);
		statusBar.setScaleX(Math.max(status / 100f, 0.001f));
	}

	protected void repair() {
		float repaired = ((ControlRoom) rooms.get("ControlRoom")).use((int) (Math.min(100 - status, 10) * repairCost)) / repairCost;

		if (status <= 25 && status + repaired > 25) {
			statusBar.setDrawable(statusBarDrawable);
//			aboveButtons.getCell(statusBar).height(9f);
//			aboveButtons.invalidate();
		}
		if (status == 0 && repaired > 0)
			enableRoom();
		status += repaired;
		if (status > 100) {
			repairButton.setDisabled(true);
			status = 100;
		}
		if (status <= 25) {
//			aboveButtons.getCell(statusBar).height(9f * 1.5f);
//			aboveButtons.invalidate();
		}
		statusBar.setScaleX(Math.max(status / 100f, 0.001f));
	}
}
