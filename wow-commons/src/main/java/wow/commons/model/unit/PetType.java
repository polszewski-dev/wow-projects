package wow.commons.model.unit;

import wow.commons.util.EnumUtil;

/**
 * User: POlszewski
 * Date: 2020-09-30
 */
public enum PetType {
	IMP("Imp"),
	VOIDWALKER("Voidwalker"),
	SUCCUBUS("Succubus"),
	FELHUNTER("Felhunter"),
	FELGUARD("Felguard");

	private final String name;

	PetType(String name) {
		this.name = name;
	}

	public static PetType parse(String value) {
		return EnumUtil.parse(value, values(), x -> x.name);
	}

	@Override
	public String toString() {
		return name;
	}
}
