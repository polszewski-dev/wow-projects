package wow.commons.model.categorization;

import wow.commons.util.EnumUtil;

/**
 * User: POlszewski
 * Date: 2021-03-17
 */
public enum ProjectileSubType implements ItemSubType {
	ARROW("Arrow"),
	BULLET("Bullet");

	private final String key;

	ProjectileSubType(String key) {
		this.key = key;
	}

	public static ProjectileSubType tryParse(String value) {
		return EnumUtil.tryParse(value, values(), x -> x.key);
	}
}
