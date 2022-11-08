package wow.commons.model.unit;

import wow.commons.util.EnumUtil;

/**
 * User: POlszewski
 * Date: 2020-09-30
 */
public enum PetType {
	IMP,
	VOIDWALKER,
	SUCCUBUS,
	FELHUNTER,
	FELGUARD,
	;

	public static PetType parse(String value) {
		return EnumUtil.parse(value, values());
	}
}
