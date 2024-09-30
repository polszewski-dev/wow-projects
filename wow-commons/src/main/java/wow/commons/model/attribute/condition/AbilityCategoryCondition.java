package wow.commons.model.attribute.condition;

import wow.commons.model.spell.AbilityCategory;
import wow.commons.util.EnumUtil;

import java.util.Map;
import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2023-09-19
 */
public record AbilityCategoryCondition(AbilityCategory abilityCategory) implements AttributeCondition {
	public AbilityCategoryCondition {
		Objects.requireNonNull(abilityCategory);
	}

	public static AbilityCategoryCondition of(AbilityCategory abilityCategory) {
		return CACHE.get(abilityCategory);
	}

	@Override
	public String toString() {
		return abilityCategory.toString();
	}

	private static final Map<AbilityCategory, AbilityCategoryCondition> CACHE = EnumUtil.cache(
			AbilityCategory.class, AbilityCategory.values(), AbilityCategoryCondition::new);
}
