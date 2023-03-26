package wow.commons.model.attributes.condition;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import wow.commons.model.attributes.AttributeCondition;
import wow.commons.model.professions.ProfessionSpecialization;
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
	private final ProfessionSpecialization specialization;

	public static ProfessionSpecCondition of(ProfessionSpecialization spec) {
		return CACHE.get(spec);
	}

	@Override
	public String getConditionString() {
		return "spec: " + specialization;
	}

	@Override
	public String toString() {
		return specialization.toString();
	}

	private static final Map<ProfessionSpecialization, ProfessionSpecCondition> CACHE = EnumUtil.cache(
			ProfessionSpecialization.class, ProfessionSpecialization.values(), ProfessionSpecCondition::new);
}
