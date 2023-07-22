package wow.commons.model.attributes.condition;

import wow.commons.model.professions.ProfessionSpecializationId;
import wow.commons.util.EnumUtil;

import java.util.Map;
import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2023-03-25
 */
public record ProfessionSpecCondition(ProfessionSpecializationId specializationId) implements AttributeCondition {
	public ProfessionSpecCondition {
		Objects.requireNonNull(specializationId);
	}

	public static ProfessionSpecCondition of(ProfessionSpecializationId spec) {
		return CACHE.get(spec);
	}

	@Override
	public String getConditionString() {
		return "spec: " + specializationId;
	}

	@Override
	public String toString() {
		return specializationId.toString();
	}

	private static final Map<ProfessionSpecializationId, ProfessionSpecCondition> CACHE = EnumUtil.cache(
			ProfessionSpecializationId.class, ProfessionSpecializationId.values(), ProfessionSpecCondition::new);
}
