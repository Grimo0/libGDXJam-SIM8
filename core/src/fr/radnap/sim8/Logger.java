package fr.radnap.sim8;

import com.badlogic.gdx.Gdx;

import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * @author Radnap
 */
public class Logger extends com.badlogic.gdx.utils.Logger {

	public Writer debugLogWriter;
	public SimpleDateFormat dateFmt;
	public StringBuilder stringBuilder;


	public Logger(String tag) {
		this(tag, ERROR);
	}

	public Logger(String tag, int level) {
		super(tag, level);

		debugLogWriter = Gdx.files.local("debug_"+tag+".log").writer(false, "utf-8");
		dateFmt = new SimpleDateFormat("MM/dd HH:mm:ss", Locale.FRANCE);
		stringBuilder = new StringBuilder();
	}


	public static void log(String message) {
		Gdx.app.log(SIM8.class.getSimpleName(), message);
	}


	public void write(String message) {
		try {
			stringBuilder.setLength(0);
			stringBuilder.append("[")
					.append(dateFmt.format(Calendar.getInstance().getTime()))
					.append("] ")
					.append(message)
					.append("\n");
			debugLogWriter.write(stringBuilder.toString());
			debugLogWriter.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void write(String message, Throwable exception) {
		try {
			stringBuilder.setLength(0);
			stringBuilder.append("[")
					.append(dateFmt.format(Calendar.getInstance().getTime()))
					.append("] ")
					.append(message)
					.append("\n")
					.append(exception.toString());
			debugLogWriter.write(stringBuilder.toString());
			debugLogWriter.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void debug(String message) {
		super.debug(message);
		write(message);
	}

	@Override
	public void debug(String message, Exception exception) {
		super.debug(message, exception);
		write(message, exception);
	}

	@Override
	public void info(String message) {
		super.info(message);
		write(message);
	}

	@Override
	public void info(String message, Exception exception) {
		super.info(message, exception);
		write(message, exception);
	}

	@Override
	public void error(String message) {
		super.error(message);
		write(message);
	}

	@Override
	public void error(String message, Throwable exception) {
		super.error(message, exception);
		write(message, exception);
	}

}
