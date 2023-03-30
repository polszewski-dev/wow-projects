package wow.commons.model.attributes.condition;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import wow.commons.model.attributes.AttributeCondition;
import wow.commons.model.professions.ProfessionSpecializationId;
import wow.commons.util.EnumUtil;

import java.util.Map;

/**
 * User: POlszewski
 * Date: 2023-03-25
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
public class ProfessionSpecCondition implements AttributeCondition {
	@NonNull
	private final ProfessionSpecializationId specializationId;

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
