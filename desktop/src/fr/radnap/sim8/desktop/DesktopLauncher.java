package fr.radnap.sim8.desktop;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import fr.radnap.sim8.SIM8;

public class DesktopLauncher {
	public static void main(String[] arg) {
		System.setProperty("user.name", "EnglishWords"); // to avoid an LWJGLException

		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = SIM8.NAME + " " + SIM8.VERSION;
		cfg.height = 720;
		cfg.width = 1280;
		cfg.resizable = false;
		cfg.addIcon("ic_launcher.png", Files.FileType.Internal);

		new LwjglApplication(new SIM8(), cfg);

//        Gdx.input.setCursorCatched(true);
	}
}
