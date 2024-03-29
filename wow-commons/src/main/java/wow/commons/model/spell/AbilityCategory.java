package wow.commons.model.spell;

import lombok.AllArgsConstructor;
import wow.commons.util.EnumUtil;

/**
 * User: POlszewski
 * Date: 2023-09-19
 */
@AllArgsConstructor
public enum AbilityCategory {
	BLESSINGS("Blessings"),
	CURSES("Curses"),
	DAMAGING_SHOTS("DamagingShots"),
	FINISHING_MOVES("FinishingMoves"),
	INTERRUPTS("Interrupts"),
	JUDGEMENTS("Judgements"),
	MANA_GEMS("ManaGems"),
	POISONS("Poisons"),
	SILENCES("Silences"),
	SHAPESHIFTS("Shapeshifts"),
	TOTEMS("Totems"),

	HEALING_POTIONS("HealingPotions"),
	MANA_POTIONS("ManaPotions");

	private final String name;

	public static AbilityCategory parse(String value) {
		return EnumUtil.parse(value, values(), x -> x.name);
	}

	public static AbilityCategory tryParse(String value) {
		return EnumUtil.tryParse(value, values(), x -> x.name);
	}

	@Override
	public String toString() {
		return name;
	}
}
