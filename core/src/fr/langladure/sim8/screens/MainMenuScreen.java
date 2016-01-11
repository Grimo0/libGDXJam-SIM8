package fr.langladure.sim8.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TiledDrawable;
import fr.langladure.sim8.SIM8;

import java.security.Key;

/**
 * @author Radnap
 */
public class MainMenuScreen extends AbstractScreen {

	private Stage stage;
	private Table table;

	private int selected;
	private Image cursor;
	private Sprite screenBg;

	private class MenuItemListener extends InputListener {
		@Override
		public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
			Actor item = table.getChildren().items[selected];
			if (item != event.getListenerActor()) {
				InputEvent buttonEvent = new InputEvent();
				buttonEvent.setPointer(-1);
				buttonEvent.setType(InputEvent.Type.exit);
				item.fire(buttonEvent);
			}

			item = event.getListenerActor();
			selected = table.getChildren().indexOf(item, true);
			cursor.setY(table.getY() + item.getY() + (item.getHeight() - cursor.getHeight()) / 2);
			stage.setKeyboardFocus(fromActor);
		}
	}


	public MainMenuScreen(SIM8 game) {
		super(game);
	}

	@Override
	public void loadAssets() {
		if (stage != null)
			return;

		super.loadAssets();
		game.assetsFinder.load("mainMenu");

		game.optionScreen.loadAssets();
	}

	@Override
	public void create() {
		if (stage != null)
			return;

		super.create();

		stage = new Stage(viewport, game.batch);

		TextureAtlas atlas = game.assetManager.get("mainMenu/mainMenuPack.atlas", TextureAtlas.class);


		// Create the screen background and adapt the world to it
		screenBg = atlas.createSprite("metal_panel");

		float ratio = SCREEN_WIDTH / (4f * screenBg.getWidth());
		screenBg.setScale(ratio);


		// Skin and fonts
		Skin skin = new Skin();

		FreeTypeFontGenerator.FreeTypeFontParameter fontParams = new FreeTypeFontGenerator.FreeTypeFontParameter();
		fontParams.minFilter = Texture.TextureFilter.Linear;
		fontParams.magFilter = Texture.TextureFilter.Linear;
		fontParams.shadowColor = new Color(0f, 0f, 0f, 0.3f);
		fontParams.shadowOffsetX = (int) (2*ratio);
		fontParams.shadowOffsetY = (int) (2*ratio);

		Label.LabelStyle labelStyle = new Label.LabelStyle();
		fontParams.size = (int) (0.2f * stage.getHeight());
		labelStyle.font = SIM8.titleGen.generateFont(fontParams);
		labelStyle.fontColor = new Color(1f, 42f/255f, 0f, 1f);
		skin.add("title", labelStyle);

		labelStyle = new Label.LabelStyle();
		fontParams.size = (int) (0.05f * stage.getHeight());
		fontParams.shadowOffsetX = (int) (1*ratio);
		fontParams.shadowOffsetY = (int) (1*ratio);
		labelStyle.font = SIM8.title2Gen.generateFont(fontParams);
		labelStyle.fontColor = Color.BLACK;
		skin.add("credits", labelStyle);

		TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
		fontParams.size = (int) (0.095f * stage.getHeight());
		fontParams.shadowColor = new Color(29f/255f, 46f/255f, 66f/255f, 1f);
		textButtonStyle.font = SIM8.title2Gen.generateFont(fontParams);
		textButtonStyle.overFontColor = Color.BLACK;
		textButtonStyle.fontColor = new Color(0.14f, 0.14f, 0.18f, 1f);
		skin.add("default", textButtonStyle);


		Label title = new Label(SIM8.NAME, skin, "title");
		stage.addActor(title);
		title.setX((stage.getWidth() - title.getWidth()) / 2f);
		title.setY(0.85f * stage.getHeight() - 0.5f * title.getHeight());


		Label credits = new Label("Radnap #LD34", skin, "credits");
		stage.addActor(credits);
		credits.setX((stage.getWidth() - credits.getWidth()) / 2f);
		credits.setY((0.1f * stage.getHeight() - credits.getHeight()) / 2f);


		////////// MENU //////////
		table = new Table(skin);
		stage.addActor(table);
		table.left().bottom();
		// Setting the default value of the cells
		table.defaults().left().spaceTop(Value.percentHeight(0.3f));
		table.setX(0.15f * stage.getWidth());
		table.setY(0.3f * stage.getHeight());

		TextButton newGame = new TextButton("New game", skin);
		newGame.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				game.loadingScreen.setFadeWhenLoaded(false);
				game.loadingScreen.setNextScreen(game.gameScreen);
				game.setScreen(game.loadingScreen);
			}
		});
		newGame.addListener(new MenuItemListener());
		table.add(newGame).row();

		final TextButton options = new TextButton("Options", skin);
		options.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				game.setScreen(game.optionScreen);
			}
		});
		options.addListener(new MenuItemListener());
		table.add(options).row();
		game.optionScreen.create();

		TextButton exit = new TextButton("Exit", skin);
		exit.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				Gdx.app.exit();
			}
		});
		exit.addListener(new MenuItemListener());
		table.add(exit).row();

		table.layout();

		// Cursor
		selected = 0;
		cursor = new Image(game.assetManager.get("./cursor.png", Texture.class));
		stage.addActor(cursor);
		cursor.setSize(cursor.getWidth() * ratio, cursor.getHeight() * ratio);
		cursor.setX(table.getX() - cursor.getWidth() - 2 * ratio);
		Actor item = table.getChildren().items[selected];
		InputEvent buttonEvent = new InputEvent();
		buttonEvent.setPointer(-1);
		buttonEvent.setType(InputEvent.Type.enter);
		item.fire(buttonEvent);
//		cursor.setY(table.getY() + item.getY() + (item.getHeight() - cursor.getHeight()) / 2);
//		stage.setKeyboardFocus(item);

		stage.addListener(new InputListener() {
			@Override
			public boolean keyDown(InputEvent event, int keycode) {
				if (keycode == Input.Keys.DOWN) {
					if (selected < table.getChildren().size - 1) {
						InputEvent buttonEvent = new InputEvent();
						buttonEvent.setPointer(-1);
						buttonEvent.setType(InputEvent.Type.enter);
						table.getChildren().items[selected + 1].fire(buttonEvent);
					}
					return true;
				} else if (keycode == Input.Keys.UP) {
					if (selected > 0) {
						InputEvent buttonEvent = new InputEvent();
						buttonEvent.setPointer(-1);
						buttonEvent.setType(InputEvent.Type.enter);
						table.getChildren().items[selected - 1].fire(buttonEvent);
					}
					return true;
				} else if (keycode == Input.Keys.ENTER) {
					table.getChildren().items[selected].fire(new ChangeListener.ChangeEvent());
				}

				return super.keyDown(event, keycode);
			}
		});
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
	}

	@Override
	public void show() {
		super.show();

		Gdx.input.setInputProcessor(stage);
	}

	@Override
	public void render(float delta) {
		super.render(delta);
		stage.setDebugAll(SIM8.DEVMODE);

		game.batch.setProjectionMatrix(camera.combined);
		game.batch.begin();
		for (float x = 0; x < SCREEN_WIDTH; x += screenBg.getWidth() * screenBg.getScaleX()) {
			for (float y = -screenBg.getHeight() / 2f; y < SCREEN_HEIGHT; y += screenBg.getHeight() * screenBg.getScaleY()) {
				screenBg.setPosition(x, y);
				screenBg.draw(game.batch);
			}
		}
		game.batch.end();

		stage.act(delta);
		stage.draw();
	}

	@Override
	public void dispose() {
		super.dispose();

		game.assetsFinder.unload("mainMenu");
		if (stage != null) {
			stage.dispose();
			stage = null;
		}
	}
}
