package wow.commons.model.categorization;

/**
 * User: POlszewski
 * Date: 2021-03-05
 */
public interface ItemSubType {
	static ItemSubType parse(String value) {
		if (value == null) {
			return null;
		}
		ItemSubType result = tryParse(value);
		if (result != null) {
			return result;
		}
		throw new IllegalArgumentException(value);
	}

	static ItemSubType tryParse(String value) {
		if (value == null) {
			return null;
		}
		ItemSubType result = ArmorSubType.tryParse(value);
		if (result != null) {
			return result;
		}
		result = WeaponSubType.tryParse(value);
		if (result != null) {
			return result;
		}
		return ProjectileSubType.tryParse(value);
	}

	static String name(ItemSubType itemSubType) {
		if (itemSubType == null) {
			return null;
		}
		return ((Enum<?>)itemSubType).name();
	}

	static ItemSubType valueOf(String value) {
		if (value == null) {
			return null;
		}
		try {
			return ArmorSubType.valueOf(value);
		} catch (IllegalArgumentException e) {
			return WeaponSubType.valueOf(value);
		}
	}
}
