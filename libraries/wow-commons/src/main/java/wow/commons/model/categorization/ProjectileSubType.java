package wow.commons.model.categorization;

import lombok.AllArgsConstructor;
import wow.commons.util.EnumUtil;

/**
 * User: POlszewski
 * Date: 2021-03-17
 */
@AllArgsConstructor
public enum ProjectileSubType implements ItemSubType {
	ARROW("Arrow"),
	BULLET("Bullet");

	private final String key;

	public static ProjectileSubType tryParse(String value) {
		return EnumUtil.tryParse(value, values(), x -> x.key);
	}

	@Override
	public String toString() {
		return key;
	}
}
