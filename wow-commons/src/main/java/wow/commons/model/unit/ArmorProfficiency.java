package wow.commons.model.unit;

import wow.commons.model.categorization.ArmorSubType;

/**
 * User: POlszewski
 * Date: 2021-03-14
 */
public enum ArmorProfficiency {
	Cloth(ArmorSubType.Cloth),
	Leather(ArmorSubType.Leather),
	Mail(ArmorSubType.Mail),
	Plate(ArmorSubType.Plate),
	;

	private final ArmorSubType armorSubType;

	ArmorProfficiency(ArmorSubType armorSubType) {
		if (armorSubType == null) {
			throw new NullPointerException();
		}
		this.armorSubType = armorSubType;
	}

	public boolean matches(ArmorSubType armorSubType) {
		return this.armorSubType == armorSubType;
	}

	public static boolean matches(CharacterClass characterClass, ArmorSubType armorSubType) {
		return characterClass.getArmorProfficiencies().stream().anyMatch(x -> x.matches(armorSubType));
	}
}