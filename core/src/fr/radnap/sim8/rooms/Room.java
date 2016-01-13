package fr.radnap.sim8.rooms;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.OrderedMap;
import fr.radnap.sim8.screens.GameScreen;

/**
 * @author Radnap
 */
public abstract class Room extends Table {

	protected static ObjectMap<String, Room> rooms = new ObjectMap<>();

	private String name;
	private final TextureAtlas atlas;
	private Animation backgroundAnimation;
	private TextureRegionDrawable backgroundDrawable;
	private float stateTime;
	protected Table aboveButtons;
	protected Table belowButtons;
	private HorizontalGroup buttonsGroup;
	private ObjectMap<String, Button> buttons;


	public Room(String name, TextureAtlas atlas, float width, float height) {
		rooms.put(name, this);
		this.name = name;
		setSize(width, height);

		backgroundAnimation = new Animation(0.3f, atlas.findRegions(name));
		backgroundDrawable = new TextureRegionDrawable(backgroundAnimation.getKeyFrame(0));
		setBackground(backgroundDrawable);
		setClip(true);

		buttons = new OrderedMap<>();
		this.atlas = atlas;

		aboveButtons = new Table(GameScreen.skin);
		aboveButtons.top().left();
		add(aboveButtons).pad(5f).top().left().expand().height((height - 116f) * .5f).width(width - 10f);
		row();

		buttonsGroup = new HorizontalGroup();
		buttonsGroup.space(25f);
		add(buttonsGroup).height(96).expandX();
		row();

		belowButtons = new Table(GameScreen.skin);
		belowButtons.setBackground(new NinePatchDrawable(atlas.createPatch("glassPanel")));
		belowButtons.top().left().pad(7f).padTop(8f);
		belowButtons.defaults().space(4f);
		add(belowButtons).pad(6f).bottom().left().expand().height(100f).width(width - 12f);
		row();

		Image roomFrame = new Image(atlas.createPatch("roomFrame"));
		addActor(roomFrame);
		roomFrame.setSize(width, height);
	}


	@Override
	public void act(float delta) {
		super.act(delta);
		stateTime += delta;
		backgroundDrawable.setRegion(backgroundAnimation.getKeyFrame(stateTime, true));
	}

	public abstract void takeDamage(float damage);

	/**
	 * Add a button to the group and attache it the ChangeListener.
	 * By default, it is disabled
	 */
	protected void addActionButton(String action, ChangeListener changeListener) {
		Button.ButtonStyle style = new Button.ButtonStyle();
		style.up = new TextureRegionDrawable(atlas.findRegion("buttons/" + action + "ButtonUp"));
		style.down = new TextureRegionDrawable(atlas.findRegion("buttons/" + action + "ButtonDown"));
		style.disabled = new TextureRegionDrawable(atlas.findRegion("buttons/" + action + "ButtonDisable"));
		Button button = new Button(style);
		button.setDisabled(true);
		button.addListener(changeListener);

		buttonsGroup.addActor(button);
		buttons.put(action, button);
	}

	public void disable(String action) {
		Button button = buttons.get(action);
		if (button != null) {
			button.setDisabled(true);
		}
	}

	public void enable(String action) {
		Button button = buttons.get(action);
		if (button != null) {
			button.setDisabled(false);
		}
	}

	@Override
	public boolean remove() {
		rooms.remove(name);
		return super.remove();
	}
}
