package wow.commons.model.spell;

import lombok.AllArgsConstructor;
import wow.commons.util.EnumUtil;

/**
 * User: POlszewski
 * Date: 2023-10-05
 */
@AllArgsConstructor
public enum Reagent {
	ANKH("Ankh"),
	ARCANE_POWDER("Arcane Powder"),
	ARCANE_POWDER_X_2("Arcane Powder (2)"),
	ASHWOOD_SEED("Ashwood Seed"),
	DEMONIC_FIGURINE("Demonic Figurine"),
	FISH_OIL("Fish Oil"),
	FLINTWEED_SEED("Flintweed Seed"),
	HOLY_CANDLE("Holy Candle"),
	HORNBEAM_SEED("Hornbeam Seed"),
	INFERNAL_STONE("Infernal Stone"),
	IRONWOOD_SEED("Ironwood Seed"),
	LIGHT_FEATHER("Light Feather"),
	MAPLE_SEED("Maple Seed"),
	RUNE_OF_PORTALS("Rune of Portals"),
	RUNE_OF_TELEPORTATION("Rune of Teleportation"),
	SACRED_CANDLE("Sacred Candle"),
	SHINY_FISH_SCALES("Shiny Fish Scales"),
	SOUL_SHARD("Soul Shard"),
	STRANGLETHORN_SEED("Stranglethorn Seed"),
	SYMBOL_OF_DIVINITY("Symbol of Divinity"),
	SYMBOL_OF_KINGS("Symbol of Kings"),
	WILD_BERRIES("Wild Berries"),
	WILD_QUILLVINE("Wild Quillvine"),
	WILD_THORNROOT("Wild Thornroot"),

	;

	private final String name;

	public static Reagent parse(String value) {
		return EnumUtil.parse(value, values(), x -> x.name);
	}

	@Override
	public String toString() {
		return name;
	}
}
