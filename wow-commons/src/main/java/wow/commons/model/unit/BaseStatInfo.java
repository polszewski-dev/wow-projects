package wow.commons.model.unit;

import wow.commons.model.Percent;

/**
 * User: POlszewski
 * Date: 2021-03-19
 */
public class BaseStatInfo {
	private final int level;
	private final CharacterClass characterClass;
	private final Race race;
	private final int baseStrength;
	private final int baseAgility;
	private final int baseStamina;
	private final int baseIntellect;
	private final int baseSpirit;
	private final int baseHP;
	private final int baseMana;
	private final Percent baseSpellCritPct;
	private final double intellectPerCritPct;

	public BaseStatInfo(int level, CharacterClass characterClass, Race race, int baseStrength, int baseAgility, int baseStamina, int baseIntellect, int baseSpirit, int baseHP, int baseMana, Percent baseSpellCritPct, double intellectPerCritPct) {
		this.level = level;
		this.characterClass = characterClass;
		this.race = race;
		this.baseStrength = baseStrength;
		this.baseAgility = baseAgility;
		this.baseStamina = baseStamina;
		this.baseIntellect = baseIntellect;
		this.baseSpirit = baseSpirit;
		this.baseHP = baseHP;
		this.baseMana = baseMana;
		this.baseSpellCritPct = baseSpellCritPct;
		this.intellectPerCritPct = intellectPerCritPct;
	}

	public int getLevel() {
		return level;
	}

	public CharacterClass getCharacterClass() {
		return characterClass;
	}

	public Race getRace() {
		return race;
	}

	public int getBaseStrength() {
		return baseStrength;
	}

	public int getBaseAgility() {
		return baseAgility;
	}

	public int getBaseStamina() {
		return baseStamina;
	}

	public int getBaseIntellect() {
		return baseIntellect;
	}

	public int getBaseSpirit() {
		return baseSpirit;
	}

	public int getBaseHP() {
		return baseHP;
	}

	public int getBaseMana() {
		return baseMana;
	}

	public Percent getBaseSpellCritPct() {
		return baseSpellCritPct;
	}

	public double getIntellectPerCritPct() {
		return intellectPerCritPct;
	}

	@Override
	public String toString() {
		return String.format("%s,%s,%s", characterClass, race, level);
	}
}
