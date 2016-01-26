package fr.radnap.sim8.rooms;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.OrderedMap;
import fr.radnap.sim8.Options;
import fr.radnap.sim8.PlayerShip;
import fr.radnap.sim8.screens.GameScreen;

/**
 * @author Radnap
 */
public abstract class Room extends Table {

	protected PlayerShip ship;
	protected static ObjectMap<String, Room> rooms = new ObjectMap<>();

	protected final TextureAtlas atlas;
	protected boolean disabled;

	private Animation backgroundAnimation;
	private TextureRegionDrawable backgroundDrawable;
	protected float stateTime;
	private Image disabledOverlay;

	protected ObjectMap<String, Button> buttons;
	protected Sound buttonSound;
	protected ClickListener buttonSoundClickListener;
	protected Table buttonsTable;
	protected Table aboveButtons;
	protected Table belowButtons;


	public Room(PlayerShip ship, String name, TextureAtlas atlas, AssetManager assetManager, float width, float height) {
		this.ship = ship;
		rooms.put(name, this);

		this.atlas = atlas;
		disabled = false;

		setName(name);
		setSize(width, height);

		backgroundAnimation = new Animation(0.4f, atlas.findRegions(name));
		backgroundDrawable = new TextureRegionDrawable(backgroundAnimation.getKeyFrame(0));
		setBackground(backgroundDrawable);
		setClip(true);

		buttonSound = assetManager.get("sounds/click.wav");

		buttonSoundClickListener = new ClickListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				if (Options.sound)
					buttonSound.play(.4f);
				return super.touchDown(event, x, y, pointer, button);
			}
		};

		buttons = new OrderedMap<>();

		buttonsTable = new Table();
		buttonsTable.center().bottom();
		buttonsTable.defaults().space(25f);
		addActor(buttonsTable);//.expandX();//.height(96);

		belowButtons = new Table(GameScreen.skin);
		belowButtons.top().left();
		belowButtons.setSize(width - 9f, height - 9f);
		belowButtons.setPosition(5f, height - 5f - belowButtons.getHeight());
		addActor(belowButtons);

		disabledOverlay = new Image(atlas.findRegion("disabledRoom"));
		disabledOverlay.setVisible(false);
		disabledOverlay.setSize(width, height);
		addActor(disabledOverlay);

		aboveButtons = new Table(GameScreen.skin);
		aboveButtons.top().left();
		aboveButtons.setSize(width - 9f, (height - 116f) * .5f);
		aboveButtons.setPosition(5f, height - 5f - aboveButtons.getHeight());
		addActor(aboveButtons);

		NinePatch roomFrame1 = atlas.createPatch("roomFrame");
		Image roomFrame = new Image(roomFrame1);
		roomFrame.setTouchable(Touchable.disabled);
		roomFrame.setSize(width, height);
		roomFrame.setPosition(0f, 0f);
		addActor(roomFrame);

		validate();

		buttonsTable.setY(50f);
	}


	public void initialize() {
		Label actor = new Label(getName(), GameScreen.skin);
		actor.setPosition(getX() + 10f, getY() + getHeight());
		getStage().addActor(actor);
	}

	@Override
	public void act(float delta) {
		if (disabled) return;

		super.act(delta);
		stateTime += delta;
		backgroundDrawable.setRegion(backgroundAnimation.getKeyFrame(stateTime, true));
	}

	public void disableRoom() {
		disabledOverlay.setVisible(true);
		disabled = true;
	}

	public void enableRoom() {
		disabledOverlay.setVisible(false);
		disabled = false;
	}

	protected Button addActionButton(String action, ChangeListener changeListener) {
		return addActionButton(action, changeListener, buttonSoundClickListener);
	}

	/**
	 * Add a button to the group and attache it the ChangeListener.
	 * By default, it is disabled
	 */
	protected Button addActionButton(String action, ChangeListener changeListener, ClickListener clickListener) {
		Button.ButtonStyle style = new Button.ButtonStyle();
		style.up = new TextureRegionDrawable(atlas.findRegion("buttons/" + action + "ButtonUp"));
		style.down = new TextureRegionDrawable(atlas.findRegion("buttons/" + action + "ButtonDown"));
		style.disabled = new TextureRegionDrawable(atlas.findRegion("buttons/" + action + "ButtonDisable"));
		Button button = new Button(style);
		button.setDisabled(true);
		button.addListener(changeListener);
		if (clickListener != null)
			button.addListener(clickListener);

		buttons.put(action, button);
		buttonsTable.add(button);
		buttonsTable.setX(getWidth() / 2f);
		return button;
	}

	protected void disable(String action) {
		Button button = buttons.get(action);
		if (button != null && !button.isDisabled()) {
			button.setDisabled(true);
		}
	}

	protected void enable(String action) {
		Button button = buttons.get(action);
		if (button != null && button.isDisabled()) {
			button.setDisabled(false);
		}
	}

	@Override
	public boolean remove() {
		rooms.remove(getName());
		buttonSound.dispose();
		return super.remove();
	}
}
