package wow.commons.model.professions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import wow.commons.util.EnumUtil;

/**
 * User: POlszewski
 * Date: 2022-10-31
 */
@AllArgsConstructor
@Getter
public enum ProfessionSpecialization {
	GNOMISH_ENGINEER("Gnomish Engineer", Profession.ENGINEERING),
	GOBLIN_ENGINEER("Goblin Engineer", Profession.ENGINEERING),
	MASTER_SWORDSMITH("Master Swordsmith", Profession.BLACKSMITHING),
	MOONCLOTH_TAILORING("Mooncloth Tailoring", Profession.TAILORING),
	SHADOWEAVE_TAILORING("Shadoweave Tailoring", Profession.TAILORING),
	SPELLFIRE_TAILORING("Spellfire Tailoring", Profession.TAILORING);

	private final String key;
	private final Profession profession;

	public static ProfessionSpecialization parse(String value) {
		return EnumUtil.parse(value, values(), x -> x.key);
	}

	public static ProfessionSpecialization tryParse(String value) {
		return EnumUtil.tryParse(value, values(), x -> x.key);
	}

	@Override
	public String toString() {
		return key;
	}
}
