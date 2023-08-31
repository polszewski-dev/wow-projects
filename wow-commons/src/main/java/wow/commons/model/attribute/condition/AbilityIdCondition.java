package wow.commons.model.attribute.condition;

import wow.commons.model.spell.AbilityId;
import wow.commons.util.EnumUtil;

import java.util.Map;
import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2022-11-11
 */
public record AbilityIdCondition(AbilityId abilityId) implements AttributeCondition {
	public AbilityIdCondition {
		Objects.requireNonNull(abilityId);
	}

	public static AbilityIdCondition of(AbilityId abilityId) {
		return CACHE.get(abilityId);
	}

	@Override
	public boolean test(AttributeConditionArgs args) {
		return args.getAbilityId() == abilityId;
	}

	@Override
	public String toString() {
		return abilityId.toString();
	}

	private static final Map<AbilityId, AbilityIdCondition> CACHE = EnumUtil.cache(
			AbilityId.class, AbilityId.values(), AbilityIdCondition::new);
}
