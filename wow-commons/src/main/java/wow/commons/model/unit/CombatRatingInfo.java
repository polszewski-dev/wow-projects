package wow.commons.model.unit;

/**
 * User: POlszewski
 * Date: 2021-03-19
 */
public class CombatRatingInfo {
	private final int level;
	private final double spellCrit;
	private final double spellHit;
	private final double spellHaste;

	public CombatRatingInfo(int level, double spellCrit, double spellHit, double spellHaste) {
		this.level = level;
		this.spellCrit = spellCrit;
		this.spellHit = spellHit;
		this.spellHaste = spellHaste;
	}

	public int getLevel() {
		return level;
	}

	public double getSpellCrit() {
		return spellCrit;
	}

	public double getSpellHit() {
		return spellHit;
	}

	public double getSpellHaste() {
		return spellHaste;
	}

	@Override
	public String toString() {
		return level + "";
	}
}
