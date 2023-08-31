package wow.commons.model.attribute.condition;

import wow.commons.model.categorization.WeaponSubType;
import wow.commons.util.EnumUtil;

import java.util.Map;
import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2023-10-13
 */
public record WeaponTypeCondition(WeaponSubType weaponType) implements AttributeCondition {
	public WeaponTypeCondition {
		Objects.requireNonNull(weaponType);
	}

	public static WeaponTypeCondition of(WeaponSubType weaponType) {
		return CACHE.get(weaponType);
	}

	@Override
	public boolean test(AttributeConditionArgs args) {
		return args.getWeaponType() == weaponType;
	}

	@Override
	public String toString() {
		return weaponType.toString();
	}

	private static final Map<WeaponSubType, WeaponTypeCondition> CACHE = EnumUtil.cache(
			WeaponSubType.class, WeaponSubType.values(), WeaponTypeCondition::new);
}
