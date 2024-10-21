package wow.commons.model.character;

import lombok.AllArgsConstructor;
import wow.commons.util.EnumUtil;

/**
 * User: POlszewski
 * Date: 2020-09-30
 */
@AllArgsConstructor
public enum PetType {
	IMP("Imp"),
	VOIDWALKER("Voidwalker"),
	SUCCUBUS("Succubus"),
	INCUBUS("Incubus"),
	FELHUNTER("Felhunter"),
	FELGUARD("Felguard"),
	ENSLAVED("Enslaved");

	private final String name;

	public static PetType parse(String value) {
		return EnumUtil.parse(value, values(), x -> x.name);
	}

	public static PetType tryParse(String value) {
		return EnumUtil.tryParse(value, values(), x -> x.name);
	}

	@Override
	public String toString() {
		return name;
	}
}
