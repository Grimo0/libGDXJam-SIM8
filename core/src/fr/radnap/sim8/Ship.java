package fr.radnap.sim8;

/**
 * @author Radnap
 */
public interface Ship {
	void takeDamages(int damage);
	int getLaserDamages();
	int getRocketDamages();
}
