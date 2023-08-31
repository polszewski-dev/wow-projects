package wow.commons.model.attribute.condition;

import wow.commons.model.spell.AbilityId;

import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2023-10-10
 */
public record OwnerIsChannelingCondition(AbilityId abilityId) implements AttributeCondition {
	public OwnerIsChannelingCondition {
		Objects.requireNonNull(abilityId);
	}

	public static OwnerIsChannelingCondition of(AbilityId abilityId) {
		return new OwnerIsChannelingCondition(abilityId);
	}

	public static OwnerIsChannelingCondition tryParse(String value) {
		if (value != null && value.startsWith(PREFIX)) {
			return of(AbilityId.parse(value.replace(PREFIX, "")));
		}
		return null;
	}

	@Override
	public boolean test(AttributeConditionArgs args) {
		return args.isOwnerChannelig(abilityId);
	}

	@Override
	public String toString() {
		return PREFIX + abilityId;
	}

	private static final String PREFIX = "OwnerIsChanneling:";
}
