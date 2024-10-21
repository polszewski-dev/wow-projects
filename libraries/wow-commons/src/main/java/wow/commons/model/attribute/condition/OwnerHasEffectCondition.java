package wow.commons.model.attribute.condition;

import wow.commons.model.spell.AbilityId;

import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2023-09-26
 */
public record OwnerHasEffectCondition(AbilityId abilityId) implements AttributeCondition {
	public OwnerHasEffectCondition {
		Objects.requireNonNull(abilityId);
	}

	public static OwnerHasEffectCondition of(AbilityId effectId) {
		return new OwnerHasEffectCondition(effectId);
	}

	public static OwnerHasEffectCondition tryParse(String value) {
		if (value != null && value.startsWith(PREFIX)) {
			return of(AbilityId.parse(value.replace(PREFIX, "").trim()));
		}
		return null;
	}

	@Override
	public String toString() {
		return PREFIX + abilityId;
	}

	private static final String PREFIX = "OwnerHas:";
}
