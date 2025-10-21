package wow.commons.model.talent;

import lombok.AllArgsConstructor;
import lombok.Getter;
import wow.commons.util.EnumUtil;

/**
 * User: POlszewski
 * Date: 2020-09-19
 */
@AllArgsConstructor
@Getter
public enum TalentTree {
	MISC("Misc"),

	AFFLICTION("Affliction"),
	DEMONOLOGY("Demonology"),
	DESTRUCTION("Destruction"),

	DISCIPLINE("Discipline"),
	HOLY("Holy Tree"),
	SHADOW("Shadow Tree"),

	ARCANE("Arcane Tree"),
	FIRE("Fire Tree"),
	FROST("Frost Tree"),

	BALANCE("Balance"),
	FERAL_COMBAT("Feral Combat"),
	RESTORATION("Restoration"),

	ELEMENTAL("Elemental Tree"),
	ENHANCEMENT("Enhancement"),

	PROTECTION("Protection"),
	RETRIBUTION("Retribution");

	private final String name;

	public static TalentTree parse(String value) {
		return EnumUtil.parse(value, values(), x -> x.name);
	}

	public static TalentTree tryParse(String value) {
		return EnumUtil.tryParse(value, values(), x -> x.name);
	}

	@Override
	public String toString() {
		return name;
	}
}
