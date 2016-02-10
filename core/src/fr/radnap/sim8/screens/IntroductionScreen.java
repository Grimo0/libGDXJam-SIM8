package fr.radnap.sim8.screens;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import fr.radnap.sim8.SIM8;

/**
 * @author Radnap
 */
public class IntroductionScreen extends LoadingScreen {

	public IntroductionScreen(SIM8 game) {
		super(game);
		setFadeWhenLoaded(false);
		setNextScreen(game.gameScreen);

		Label introduction = new Label(
				" In a long raging war against Heredians, six\n" +
				"humans illustrate themselves and are now\n" +
				"heading home to share their knowledge of\n" +
				"the enemy.\n" +
				" To save insufficient resources, they are\n" +
				"now cryogenised and your role is to ensure\n" +
				"they arrive safe and sound at destination.", clickLabel.getStyle());
		stage.addActor(introduction);
		introduction.setAlignment(Align.bottom, Align.left);
//		introduction.setWidth(1000f);
		introduction.setX((stage.getWidth() - introduction.getWidth()) / 2f);
		introduction.setY(500f);
	}
}
