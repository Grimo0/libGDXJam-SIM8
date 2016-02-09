package fr.radnap.sim8;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import fr.radnap.sim8.screens.*;

public class SIM8 extends Game {

	public static final String NAME = "SIM8";
	public static final String VERSION = "0.6.4";
	public static boolean RELEASE = false;
	public static boolean DEVMODE = false;

	public static Logger logger;

	public static StringBuilder stringBuilder;
	public static FreeTypeFontGenerator titleGen;
	public static FreeTypeFontGenerator title2Gen;
	public static FreeTypeFontGenerator commonGen;
	public static FreeTypeFontGenerator numberGen;

	public static FileHandleResolver resolver;
	public SpriteBatch batch;
	public AssetManager assetManager;

	public MainMenuScreen mainMenuScreen;
	public OptionScreen optionScreen;
	public LoadingScreen loadingScreen;
	public GameScreen gameScreen;

	@Override
	public void create() {

		if (!RELEASE && Gdx.app.getType() == Application.ApplicationType.Desktop) {
			// Uncomment to add the images in the /images directory to the game asset's one
			FileHandle[] assetsFolder = Gdx.files.internal("../../_assets").list();
			for (FileHandle folder : assetsFolder) {
				if (folder.isDirectory() && !folder.name().startsWith("_")) {
//					TexturePacker.process("../../_assets/" + folder.name(), folder.name(), folder.name() + "Pack");
				}
			}
		}

		logger = new Logger(NAME, Application.LOG_DEBUG);

		Gdx.app.setLogLevel(Application.LOG_DEBUG);

		stringBuilder = new StringBuilder();

		batch = new SpriteBatch();

		resolver = new InternalFileHandleResolver();
		assetManager = new AssetManager(resolver);
		assetManager.setLogger(new Logger("AssetManager", Application.LOG_DEBUG));

		Texture.setAssetManager(assetManager);

		assetManager.setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(resolver));
		assetManager.setLoader(TiledMap.class, new TmxMapLoader(resolver));

		assetManager.load("freetype/5x5_square.ttf", FreeTypeFontGenerator.class);
		assetManager.load("freetype/PressStart2P-Regular.ttf", FreeTypeFontGenerator.class);
		assetManager.finishLoading();

		titleGen = assetManager.get("freetype/5x5_square.ttf", FreeTypeFontGenerator.class);
		title2Gen = assetManager.get("freetype/5x5_square.ttf", FreeTypeFontGenerator.class);
		commonGen = assetManager.get("freetype/5x5_square.ttf", FreeTypeFontGenerator.class);
		numberGen = assetManager.get("freetype/PressStart2P-Regular.ttf", FreeTypeFontGenerator.class);

		mainMenuScreen = new MainMenuScreen(this);
		optionScreen = new OptionScreen(this);
		loadingScreen = new LoadingScreen(this);
		gameScreen = new GameScreen(this);

		loadingScreen.setNextScreen(gameScreen);
		setScreen(loadingScreen);
	}

	@Override
	public void render() {
		super.render();
		if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) && (Gdx.input.isKeyPressed(Input.Keys.Q) || Gdx.input.isKeyPressed(Input.Keys.W)))
			Gdx.app.exit();
		else if (!RELEASE && Gdx.input.isKeyPressed(Input.Keys.ALT_LEFT) && Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) && Gdx.input.isKeyJustPressed(Input.Keys.D)) {
			DEVMODE = !DEVMODE;
		} else if (DEVMODE && Gdx.input.isKeyPressed(Input.Keys.ESCAPE) && screen != mainMenuScreen) {
			loadingScreen.setFadeWhenLoaded(true);
			loadingScreen.setNextScreen(mainMenuScreen);
			setScreen(loadingScreen);
		}
	}

	@Override
	public void dispose() {
		mainMenuScreen.dispose();
		loadingScreen.dispose();
		optionScreen.dispose();
		gameScreen.dispose();
		assetManager.dispose();
		batch.dispose();
	}

	@Override
	public void resume() {
		super.resume();
		if (Gdx.app.getType() == Application.ApplicationType.Android) {
			if (!screen.equals(loadingScreen))
				loadingScreen.setNextScreen((AbstractScreen) screen);
			setScreen(loadingScreen);
		}
	}

	public static void log(String message) {
		logger.info(message);
	}

	public static void debug(String message) {
		logger.debug(message);
	}

	public static double distance(float x1, float y1, float x2, float y2) {
		return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
	}
}
