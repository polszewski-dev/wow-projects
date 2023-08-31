package wow.commons.model.attribute.condition;

import wow.commons.model.effect.EffectCategory;
import wow.commons.util.EnumUtil;

import java.util.Map;
import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2023-10-15
 */
public record EffectCategoryCondition(EffectCategory type) implements AttributeCondition {
	public EffectCategoryCondition {
		Objects.requireNonNull(type);
	}

	public static EffectCategoryCondition of(EffectCategory type) {
		return CACHE.get(type);
	}

	@Override
	public boolean test(AttributeConditionArgs args) {
		return args.getEffectCategory() == type;
	}

	@Override
	public String toString() {
		return type.toString();
	}

	private static final Map<EffectCategory, EffectCategoryCondition> CACHE = EnumUtil.cache(
			EffectCategory.class, EffectCategory.values(), EffectCategoryCondition::new);
}
