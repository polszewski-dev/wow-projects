package wow.commons.model.attribute.condition;

import wow.commons.model.spell.AbilityId;

import java.util.HashMap;
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
		return CACHE.computeIfAbsent(
				abilityId,
				AbilityIdCondition::new
		);
	}

	@Override
	public String toString() {
		return abilityId.toString();
	}

	private static final Map<AbilityId, AbilityIdCondition> CACHE = new HashMap<>();
}
