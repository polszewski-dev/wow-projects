package wow.commons.model.categorization;

/**
 * User: POlszewski
 * Date: 2021-03-05
 */
public interface ItemSubType {
	static ItemSubType tryParse(String line) {
		ItemSubType result = ArmorSubType.tryParse(line);
		if (result != null) {
			return result;
		}
		result = WeaponSubType.tryParse(line);
		if (result != null) {
			return result;
		}
		return ProjectileSubType.tryParse(line);
	}
}
