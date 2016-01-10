package fr.langladure.sim8.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import fr.langladure.sim8.SIM8;

/**
 * @author Radnap
 */
public class MainMenuScreen extends AbstractScreen {

	private Stage stage;
	private Label title;
	private Label credits;
	private Table table;

	private int selected;


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
		Image screenBg = new Image(atlas.findRegion("mainMenuBg"));

		float ratio = SCREEN_WIDTH / screenBg.getWidth();
		screenBg.setSize(SCREEN_WIDTH, SCREEN_HEIGHT);

		stage.addActor(screenBg);


		// Skin and fonts
		Skin skin = new Skin();

		FreeTypeFontGenerator.FreeTypeFontParameter fontParams = new FreeTypeFontGenerator.FreeTypeFontParameter();
//		fontParams.minFilter = Texture.TextureFilter.Linear;
//		fontParams.magFilter = Texture.TextureFilter.Linear;
		fontParams.shadowColor = new Color(140f/255f, 41f/255f, 32f/255f, 1f);
		fontParams.shadowOffsetX = (int) (2*ratio);
		fontParams.shadowOffsetY = (int) (2*ratio);

		Label.LabelStyle labelStyle = new Label.LabelStyle();
		fontParams.size = (int) (0.15f * stage.getHeight());
		labelStyle.font = SIM8.titleGen.generateFont(fontParams);
		labelStyle.fontColor = new Color(163f/255f, 48f/255f, 38f/255f, 1f);
		skin.add("title", labelStyle);

		labelStyle = new Label.LabelStyle();
		fontParams.size = (int) (0.05f * stage.getHeight());
		fontParams.shadowOffsetX = (int) (1*ratio);
		fontParams.shadowOffsetY = (int) (1*ratio);
		labelStyle.font = SIM8.title2Gen.generateFont(fontParams);
		labelStyle.fontColor = new Color(163f/255f, 48f/255f, 38f/255f, 1f);
		skin.add("credits", labelStyle);

		labelStyle = new Label.LabelStyle();
		fontParams.size = (int) (0.095f * stage.getHeight());
		fontParams.shadowColor = new Color(29f/255f, 46f/255f, 66f/255f, 1f);
		labelStyle.font = SIM8.title2Gen.generateFont(fontParams);
		labelStyle.fontColor = new Color(63f/255f, 90f/255f, 135f/255f, 1f);
		skin.add("default", labelStyle);


		title = new Label(SIM8.NAME, skin, "title");
		stage.addActor(title);
		title.setX((stage.getWidth() - title.getWidth()) / 2f);
		title.setY(0.87f * stage.getHeight() - 0.5f * title.getHeight());


		credits = new Label("Radnap #LD34", skin, "credits");
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

		TextButton newGame = new TextButton("Nouvelle partie", skin);
		newGame.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				game.loadingScreen.setFadeWhenLoaded(false);
				game.loadingScreen.setNextScreen(game.gameScreen);
				game.setScreen(game.loadingScreen);
			}
		});
		table.add(newGame).row();

		TextButton options = new TextButton("Options", skin);
		options.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				game.setScreen(game.optionScreen);
			}
		});
		table.add(options).row();
		game.optionScreen.create();

		TextButton exit = new TextButton("Quitter", skin);
		exit.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				Gdx.app.exit();
			}
		});
		table.add(exit).row();

		table.layout();

		// Cursor
		selected = 0;
		final Image cursor = new Image(game.assetManager.get("./cursor.png", Texture.class));
		stage.addActor(cursor);
		cursor.setSize(cursor.getWidth() * ratio, cursor.getHeight() * ratio);
		cursor.setX(table.getX() - cursor.getWidth() - 2 * ratio);
		Actor item = table.getChildren().items[selected];
		item.setColor(1f, 1f, 1f, 0.8f);
		cursor.setY(table.getY() + item.getY() + (item.getHeight() - cursor.getHeight()) / 2);
		stage.setKeyboardFocus(item);

		stage.addListener(new InputListener() {
			@Override
			public boolean keyDown(InputEvent event, int keycode) {
				if (keycode == Input.Keys.DOWN) {
					table.getChildren().items[selected].setColor(1f, 1f, 1f, 1f);

					selected++;
					if ( selected > table.getChildren().size - 1)
						selected = 0;
					Actor item = table.getChildren().items[selected];
					item.setColor(1f, 1f, 1f, 0.8f);
					cursor.setY(table.getY() + item.getY() + (item.getHeight() - cursor.getHeight()) / 2);
					stage.setKeyboardFocus(item);

					return true;
				} else if (keycode == Input.Keys.UP) {
					table.getChildren().items[selected].setColor(1f, 1f, 1f, 1f);

					selected--;
					if (selected < 0)
						selected = table.getChildren().size - 1;
					Actor item = table.getChildren().items[selected];
					item.setColor(1f, 1f, 1f, 0.8f);
					cursor.setY(table.getY() + item.getY() + (item.getHeight() - cursor.getHeight()) / 2);
					stage.setKeyboardFocus(item);

					return true;
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
