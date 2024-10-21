package wow.commons.model.attribute.condition;

import wow.commons.model.character.DruidFormType;
import wow.commons.util.EnumUtil;

import java.util.Map;
import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2023-09-29
 */
public record DruidFormCondition(DruidFormType type) implements AttributeCondition {
	public DruidFormCondition {
		Objects.requireNonNull(type);
	}

	public static DruidFormCondition of(DruidFormType type) {
		return CACHE.get(type);
	}

	@Override
	public String toString() {
		return type.toString();
	}

	private static final Map<DruidFormType, DruidFormCondition> CACHE = EnumUtil.cache(
			DruidFormType.class, DruidFormType.values(), DruidFormCondition::new);
}
