package wow.commons.model.spell;

import lombok.AllArgsConstructor;
import wow.commons.util.EnumUtil;

/**
 * User: POlszewski
 * Date: 2023-10-05
 */
@AllArgsConstructor
public enum Reagent {
	SOUL_SHARD("Soul Shard"),
	INFERNAL_STONE("Infernal Stone"),
	DEMONIC_FIGURINE("Demonic Figurine"),
	LIGHT_FEATHER("Light Feather"),
	HOLY_CANDLE("Holy Candle"),
	SACRED_CANDLE("Sacred Candle");

	private final String name;

	public static Reagent parse(String value) {
		return EnumUtil.parse(value, values(), x -> x.name);
	}

	@Override
	public String toString() {
		return name;
	}
}
