package wow.commons.model.profession;

import lombok.AllArgsConstructor;
import wow.commons.util.EnumUtil;

/**
 * User: POlszewski
 * Date: 2022-10-31
 */
@AllArgsConstructor
public enum ProfessionSpecializationId {
	ELIXIR_MASTER("Elixir Master"),
	POTION_MASTER("Potion Master"),
	TRANSMUTATION_MASTER("Transmutation Master"),
	SPELLFIRE_TAILORING("Spellfire Tailoring"),
	SHADOWEAVE_TAILORING("Shadoweave Tailoring"),
	MOONCLOTH_TAILORING("Mooncloth Tailoring"),
	DRAGONSCALE_LEATHERWORKING("Dragonscale Leatherworking"),
	ELEMENTAL_LEATHERWORKING("Elemental Leatherworking"),
	TRIBAL_LEATHERWORKING("Tribal Leatherworking"),
	ARMORSMITH("Armorsmith"),
	WEAPONSMITH("Weaponsmith"),
	MASTER_SWORDSMITH("Master Swordsmith"),
	MASTER_AXESMITH("Master Axesmith"),
	MASTER_HAMMERSMITH("Master Hammersmith"),
	GOBLIN_ENGINEER("Goblin Engineer"),
	GNOMISH_ENGINEER("Gnomish Engineer");

	private final String name;

	public static ProfessionSpecializationId parse(String value) {
		return EnumUtil.parse(value, values(), x -> x.name);
	}

	public static ProfessionSpecializationId tryParse(String value) {
		return EnumUtil.tryParse(value, values(), x -> x.name);
	}

	@Override
	public String toString() {
		return name;
	}
}
