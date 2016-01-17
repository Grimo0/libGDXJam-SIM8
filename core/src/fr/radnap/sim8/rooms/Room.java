package fr.radnap.sim8.rooms;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
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

	private Animation backgroundAnimation;
	private TextureRegionDrawable backgroundDrawable;
	protected float stateTime;
	private Image disabled;

	protected Sound buttonSound;
	protected ClickListener buttonSoundClickListener;
	protected ObjectMap<String, Button> buttons;
	protected Table buttonsTable;
	protected Table aboveButtons;
	protected Table belowButtons;


	public Room(PlayerShip ship, String name, TextureAtlas atlas, AssetManager assetManager, float width, float height) {
		this.ship = ship;
		rooms.put(name, this);
		setName(name);
		this.atlas = atlas;
		setSize(width, height);

		backgroundAnimation = new Animation(0.4f, atlas.findRegions(name));
		backgroundDrawable = new TextureRegionDrawable(backgroundAnimation.getKeyFrame(0));
		setBackground(backgroundDrawable);
		setClip(true);

		buttonSound = assetManager.get("./sounds/click.wav");

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
//		row();

		disabled = new Image(atlas.findRegion("disabledRoom"));
		disabled.setVisible(false);
		disabled.setSize(width, height);
		addActor(disabled);

		aboveButtons = new Table(GameScreen.skin);
		aboveButtons.top().left();
		add(aboveButtons).pad(4f, 5f, 4f, 5f).top().left().expand().width(width - 9f);//.height((height - 116f) * .5f);
		row();

		Image roomFrame = new Image(atlas.createPatch("roomFrame"));
		roomFrame.setTouchable(Touchable.disabled);
		addActor(roomFrame);
		roomFrame.setSize(width + 1, height + 1);

		belowButtons = new Table(GameScreen.skin);
		belowButtons.setBackground(new NinePatchDrawable(atlas.createPatch("glassPanel")));
		belowButtons.top().left().pad(10f, 7f, 10f, 7f);
		belowButtons.defaults().left().space(5f);
		add(belowButtons).pad(10f, 0f, 0f, 0f).bottom().left().width(width + 1).height(71f);
		row();

		validate();

		buttonsTable.setY(belowButtons.getY() + belowButtons.getHeight() + belowButtons.getPadTop());
	}


	public abstract void initialize();

	@Override
	public void act(float delta) {
		super.act(delta);
		stateTime += delta;
		backgroundDrawable.setRegion(backgroundAnimation.getKeyFrame(stateTime, true));
	}

	public void disableRoom() {
//		for (ObjectMap.Entry<String, Button> button : buttons) {
//			button.value.setDisabled(true);
//		}
		disabled.setVisible(true);
	}

	public void enableRoom() {
		disabled.setVisible(false);
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
