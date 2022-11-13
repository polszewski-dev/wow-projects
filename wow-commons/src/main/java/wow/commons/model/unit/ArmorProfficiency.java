package wow.commons.model.unit;

import lombok.AllArgsConstructor;
import wow.commons.model.categorization.ArmorSubType;

/**
 * User: POlszewski
 * Date: 2021-03-14
 */
@AllArgsConstructor
public enum ArmorProfficiency {
	CLOTH(ArmorSubType.CLOTH),
	LEATHER(ArmorSubType.LEATHER),
	MAIL(ArmorSubType.MAIL),
	PLATE(ArmorSubType.PLATE);

	private final ArmorSubType armorSubType;

	public boolean matches(ArmorSubType armorSubType) {
		return this.armorSubType == armorSubType;
	}

	public static boolean matches(CharacterClass characterClass, ArmorSubType armorSubType) {
		return characterClass.getArmorProfficiencies().stream().anyMatch(x -> x.matches(armorSubType));
	}
}
