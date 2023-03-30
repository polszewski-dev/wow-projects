package wow.commons.model.attributes.condition;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import wow.commons.model.attributes.AttributeCondition;
import wow.commons.model.professions.ProfessionId;
import wow.commons.util.EnumUtil;

import java.util.Map;

/**
 * User: POlszewski
 * Date: 2023-03-25
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
public class ProfessionCondition implements AttributeCondition {
	@NonNull
	private final ProfessionId professionId;

	public static ProfessionCondition of(ProfessionId professionId) {
		return CACHE.get(professionId);
	}

	@Override
	public String getConditionString() {
		return "prof: " + professionId;
	}

	@Override
	public String toString() {
		return professionId.toString();
	}

	private static final Map<ProfessionId, ProfessionCondition> CACHE = EnumUtil.cache(
			ProfessionId.class, ProfessionId.values(), ProfessionCondition::new);
}
