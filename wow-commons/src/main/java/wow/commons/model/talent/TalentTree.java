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
	RACIAL("Racial"),
	MISC("Misc"),

	AFFLICTION("Affliction"),
	DEMONOLOGY("Demonology"),
	DESTRUCTION("Destruction"),

	DISCIPLINE("Discipline"),
	HOLY("HolyTree"),
	SHADOW("ShadowTree"),

	ARCANE("ArcaneTree"),
	FIRE("FireTree"),
	FROST("FrostTree");

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
