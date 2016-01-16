package fr.radnap.sim8.rooms;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.backends.lwjgl.audio.Wav;
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
import fr.radnap.sim8.SIM8;
import fr.radnap.sim8.screens.GameScreen;

/**
 * @author Radnap
 */
public abstract class Room extends Table {

	protected static ObjectMap<String, Room> rooms = new ObjectMap<>();

	protected final String name;
	protected final TextureAtlas atlas;

	private Animation backgroundAnimation;
	private TextureRegionDrawable backgroundDrawable;
	protected float stateTime;

	private Sound buttonSound;
	private Sound buttonReleasedSound;
	protected ObjectMap<String, Button> buttons;
	protected HorizontalGroup buttonsGroup;
	protected Table aboveButtons;
	protected Table belowButtons;


	public Room(String name, TextureAtlas atlas, float width, float height) {
		rooms.put(name, this);
		this.name = name;
		setSize(width, height);

		backgroundAnimation = new Animation(0.4f, atlas.findRegions(name));
		backgroundDrawable = new TextureRegionDrawable(backgroundAnimation.getKeyFrame(0));
		setBackground(backgroundDrawable);
		setClip(true);

		buttonSound = Gdx.audio.newSound(Gdx.files.internal("sounds/click1.wav"));
		buttonReleasedSound = Gdx.audio.newSound(Gdx.files.internal("sounds/rollover3.wav"));

		buttons = new OrderedMap<>();
		this.atlas = atlas;

		aboveButtons = new Table(GameScreen.skin);
		aboveButtons.top().left();
		add(aboveButtons).pad(4f, 5f, 4f, 5f).top().left().expand().width(width - 9f);//.height((height - 116f) * .5f);
		row();

		buttonsGroup = new HorizontalGroup();
		buttonsGroup.space(25f);
		add(buttonsGroup).height(96).expandX();
		row();

		Image roomFrame = new Image(atlas.createPatch("roomFrame"));
		roomFrame.setTouchable(Touchable.disabled);
		addActor(roomFrame);
		roomFrame.setSize(width + 1, height + 1);

		belowButtons = new Table(GameScreen.skin);
		belowButtons.setBackground(new NinePatchDrawable(atlas.createPatch("glassPanel")));
		belowButtons.top().left().pad(10f, 7f, 10f, 7f);
		belowButtons.defaults().space(5f);
		add(belowButtons).pad(10f, 0f, 0f, 0f).bottom().left().width(width + 1).height(71f);
		row();
	}


	public abstract void initialize();

	@Override
	public void act(float delta) {
		super.act(delta);
		stateTime += delta;
		backgroundDrawable.setRegion(backgroundAnimation.getKeyFrame(stateTime, true));
	}

	protected void addActionButton(String action, ChangeListener changeListener) {
		addActionButton(action, changeListener, new ClickListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				if (Options.sound)
				buttonSound.play(.4f);
				return super.touchDown(event, x, y, pointer, button);
			}
		});
	}

	/**
	 * Add a button to the group and attache it the ChangeListener.
	 * By default, it is disabled
	 */
	protected void addActionButton(String action, ChangeListener changeListener, ClickListener clickListener) {
		Button.ButtonStyle style = new Button.ButtonStyle();
		style.up = new TextureRegionDrawable(atlas.findRegion("buttons/" + action + "ButtonUp"));
		style.down = new TextureRegionDrawable(atlas.findRegion("buttons/" + action + "ButtonDown"));
		style.disabled = new TextureRegionDrawable(atlas.findRegion("buttons/" + action + "ButtonDisable"));
		Button button = new Button(style);
		button.setDisabled(true);
		button.addListener(changeListener);
		button.addListener(clickListener);

		buttonsGroup.addActor(button);
		buttons.put(action, button);
	}

	public void disable(String action) {
		Button button = buttons.get(action);
		if (button != null && !button.isDisabled()) {
			button.setDisabled(true);
		}
	}

	public void enable(String action) {
		Button button = buttons.get(action);
		if (button != null && button.isDisabled()) {
			button.setDisabled(false);
		}
	}

	@Override
	public boolean remove() {
		rooms.remove(name);
		buttonSound.dispose();
		return super.remove();
	}
}
