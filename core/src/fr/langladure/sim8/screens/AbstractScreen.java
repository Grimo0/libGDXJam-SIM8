package fr.langladure.sim8.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import fr.langladure.sim8.SIM8;

/**
 * Handle the camera creation and contain the stage variable. Handle the resize of the window and
 * reset the screen at the beginning of each render.<br/>
 * The unit size is the centimeter.
 *
 * @author Radnap
 */
public abstract class AbstractScreen implements Screen {

	/** In world unit */
	protected float SCREEN_WIDTH;
	/** In world unit */
	protected float SCREEN_HEIGHT;

	final public SIM8 game;

	public OrthographicCamera camera;
	public FillViewport viewport;


	public AbstractScreen(final SIM8 game) {
		this.game = game;

		SCREEN_WIDTH = 256f;
		SCREEN_HEIGHT = 192f;

		if (SCREEN_WIDTH / SCREEN_HEIGHT > (float) Gdx.graphics.getWidth() / Gdx.graphics.getHeight()) {
			SCREEN_WIDTH = Gdx.graphics.getHeight();
			SCREEN_HEIGHT = Gdx.graphics.getHeight() * SCREEN_WIDTH / SCREEN_HEIGHT;
		} else {
			SCREEN_HEIGHT = Gdx.graphics.getWidth() * SCREEN_HEIGHT / SCREEN_WIDTH;
			SCREEN_WIDTH = Gdx.graphics.getWidth();
		}

		camera = new OrthographicCamera();
		viewport = new FillViewport(SCREEN_WIDTH, SCREEN_HEIGHT, camera);
		viewport.apply(true);
	}

	/**
	 * @param screenWidth the new width in pixels
	 * @param screenHeight the new height in pixels
	 */
	public void updateViewPort(int screenWidth, int screenHeight) {
		viewport.update(screenWidth, screenHeight, true);
		/*if (viewport.getWorldWidth() / viewport.getWorldHeight() > (float) screenWidth / screenHeight) {
			SCREEN_WIDTH = viewport.getWorldHeight() * screenWidth / screenHeight;
			SCREEN_HEIGHT = viewport.getWorldHeight();
		} else {
			SCREEN_WIDTH = viewport.getWorldWidth();
			SCREEN_HEIGHT = viewport.getWorldWidth() * screenHeight / screenWidth;
		}*/
		SCREEN_WIDTH = viewport.getWorldWidth();
		SCREEN_HEIGHT = viewport.getWorldHeight();
	}

	public void loadAssets() {

	}

	/**
	 * Has to be called before first showing.
	 * Initialize the screen if it hasn't been. Only the first call do something.
	 */
	public void create() {

	}

	@Override
	public void show() {
		if (!SIM8.RELEASE) {
			SIM8.stringBuilder.setLength(0);
			SIM8.debug(SIM8.stringBuilder.append("### Showing ")
					.append(((Object) this).getClass().getSimpleName())
					.append(" ###").toString());
		}
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0.02f, 0.02f, 0.02f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
	}

	@Override
	public void resize(int width, int height) {
		updateViewPort(width, height);
	}

	@Override
	public void hide() {

	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void dispose() {

	}
}
