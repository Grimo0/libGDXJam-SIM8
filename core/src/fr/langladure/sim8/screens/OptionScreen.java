package fr.langladure.sim8.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TiledDrawable;
import fr.langladure.sim8.SIM8;

/**
 * @author Radnap
 */
public class OptionScreen extends AbstractScreen {

	private Stage stage;
	private Cell optionsCell;
	private Table optionsTable;
	private Table graphicTable;

	private int selected;


	public OptionScreen(SIM8 game) {
		super(game);
	}

	@Override
	public void loadAssets() {
		if (stage != null)
			return;

		super.loadAssets();
		game.assetsFinder.load("mainMenu");
	}

	@Override
	public void create() {
		if (stage != null)
			return;

		super.create();

		stage = new Stage(viewport, game.batch);

		TextureAtlas atlas = game.assetManager.get("mainMenu/mainMenuPack.atlas", TextureAtlas.class);

		// Create the screen background and adapt the world to it
		Image screenBg = new Image(new TiledDrawable(atlas.findRegion("metal_panel")));

		float ratio = SCREEN_WIDTH / (4f * screenBg.getWidth());
		screenBg.setY(-screenBg.getHeight() / 2f);
		screenBg.setSize(SCREEN_WIDTH, SCREEN_HEIGHT + screenBg.getHeight() / 2f);

		stage.addActor(screenBg);

		/// Create skin and fonts
		Skin skin = new Skin();

		FreeTypeFontGenerator.FreeTypeFontParameter fontParams = new FreeTypeFontGenerator.FreeTypeFontParameter();
		fontParams.minFilter = Texture.TextureFilter.Linear;
		fontParams.magFilter = Texture.TextureFilter.Linear;
		fontParams.size = (int) (0.1f * stage.getHeight());
		fontParams.shadowColor = new Color(0f, 0f, 0f, 0.3f);
		fontParams.shadowOffsetX = 3;
		fontParams.shadowOffsetY = 3;
		BitmapFont font = SIM8.title2Gen.generateFont(fontParams);

		TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
		textButtonStyle.font = font;
		textButtonStyle.overFontColor = Color.BLACK;
		textButtonStyle.fontColor = new Color(0.14f, 0.14f, 0.18f, 1f);
		skin.add("default", textButtonStyle);


		final Table table = new Table(skin);
		stage.addActor(table);
		table.setFillParent(true);
		table.left().bottom();
		// Setting the default value of the cells
		table.defaults().left().spaceTop(Value.percentHeight(0.5f));
		table.setX(0.15f * stage.getWidth());
		table.setY(0.2f * stage.getHeight());


		optionsTable = new Table(skin);
		optionsTable.defaults().left().spaceBottom(Value.percentHeight(0.15f));

		TextButton soundButton = new TextButton("Son : oui", skin);
		soundButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				TextButton soundButton = (TextButton) actor;
				if (soundButton.getLabel().toString().endsWith("oui"))
					soundButton.setText("Son : non");
				else
					soundButton.setText("Son : oui");
			}
		});
		optionsTable.add(soundButton).row();

		TextButton graphicButton = new TextButton("Résolution", skin);
		graphicButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				optionsCell.setActor(graphicTable);
			}
		});
		optionsTable.add(graphicButton).row();

		TextButton backButton = new TextButton("Retour", skin);
		backButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				game.setScreen(game.mainMenuScreen);
			}
		});
		optionsTable.add(backButton).row();


		graphicTable = new Table(skin);
		graphicTable.defaults().left().spaceBottom(Value.percentHeight(0.15f));

		TextButton lowResolution = new TextButton(" 800x600", skin);
		lowResolution.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				Gdx.graphics.setWindowedMode(800, 600);
			}
		});
		graphicTable.add(lowResolution).row();

		TextButton highResolution = new TextButton(" 1280x768", skin);
		highResolution.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				Gdx.graphics.setWindowedMode(1280, 768);
			}
		});
		graphicTable.add(highResolution).row();

		backButton = new TextButton("Retour", skin);
		backButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				optionsCell.setActor(optionsTable);
			}
		});
		graphicTable.add(backButton).row();


		optionsCell = table.add(optionsTable);
		optionsCell.row();
		table.layout();

		// Cursor
		selected = 0;
		final Image cursor = new Image(game.assetManager.get("./cursor.png", Texture.class));
		stage.addActor(cursor);
		cursor.setSize(cursor.getWidth() * ratio, cursor.getHeight() * ratio);
		cursor.setX(table.getX() - cursor.getWidth() - 2 * ratio);
		Actor item = optionsTable.getChildren().items[selected];
		item.setColor(Color.BLACK);
		cursor.setY(table.getY() + optionsTable.getY() + item.getY() + (item.getHeight() - cursor.getHeight()) / 2);
		stage.setKeyboardFocus(item);

		stage.addListener(new InputListener() {
			@Override
			public boolean keyDown(InputEvent event, int keycode) {
				Table cellTable = (Table) optionsCell.getActor();
				if (keycode == Input.Keys.DOWN && selected < cellTable.getChildren().size - 1) {
					cellTable.getChildren().items[selected].setColor(Color.WHITE);

					selected++;
					Actor item = cellTable.getChildren().items[selected];
					item.setColor(Color.BLACK);
					cursor.setY(table.getY() + cellTable.getY() + item.getY() + (item.getHeight() - cursor.getHeight()) / 2);
					stage.setKeyboardFocus(item);

					return true;
				} else if (keycode == Input.Keys.UP && selected > 0) {
					cellTable.getChildren().items[selected].setColor(Color.WHITE);

					selected--;
					Actor item = cellTable.getChildren().items[selected];
					item.setColor(Color.BLACK);
					cursor.setY(table.getY() + cellTable.getY() + item.getY() + (item.getHeight() - cursor.getHeight()) / 2);
					stage.setKeyboardFocus(item);

					return true;
				}

				return super.keyDown(event, keycode);
			}
		});
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
