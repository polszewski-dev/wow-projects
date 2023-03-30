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
public enum ProfessionSpecializationId {
	GNOMISH_ENGINEER("Gnomish Engineer", ProfessionId.ENGINEERING),
	GOBLIN_ENGINEER("Goblin Engineer", ProfessionId.ENGINEERING),
	MASTER_SWORDSMITH("Master Swordsmith", ProfessionId.BLACKSMITHING),
	MOONCLOTH_TAILORING("Mooncloth Tailoring", ProfessionId.TAILORING),
	SHADOWEAVE_TAILORING("Shadoweave Tailoring", ProfessionId.TAILORING),
	SPELLFIRE_TAILORING("Spellfire Tailoring", ProfessionId.TAILORING);

	private final String key;
	private final ProfessionId professionId;

	public static ProfessionSpecializationId parse(String value) {
		return EnumUtil.parse(value, values(), x -> x.key);
	}

	public static ProfessionSpecializationId tryParse(String value) {
		return EnumUtil.tryParse(value, values(), x -> x.key);
	}

	@Override
	public String toString() {
		return key;
	}
}
