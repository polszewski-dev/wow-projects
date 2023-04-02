package wow.character.model.character;

import lombok.AllArgsConstructor;
import wow.commons.model.categorization.ArmorSubType;
import wow.commons.util.EnumUtil;

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

	public static ArmorProfficiency parse(String value) {
		return EnumUtil.parse(value, values());
	}

	public boolean matches(ArmorSubType armorSubType) {
		return this.armorSubType == armorSubType;
	}
}
