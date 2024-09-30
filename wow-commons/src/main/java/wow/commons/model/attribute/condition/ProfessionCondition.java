package wow.commons.model.attribute.condition;

import wow.commons.model.profession.ProfessionId;
import wow.commons.util.EnumUtil;

import java.util.Map;
import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2023-03-25
 */
public record ProfessionCondition(ProfessionId professionId) implements AttributeCondition {
	public ProfessionCondition {
		Objects.requireNonNull(professionId);
	}

	public static ProfessionCondition of(ProfessionId professionId) {
		return CACHE.get(professionId);
	}

	@Override
	public String toString() {
		return professionId.toString();
	}

	private static final Map<ProfessionId, ProfessionCondition> CACHE = EnumUtil.cache(
			ProfessionId.class, ProfessionId.values(), ProfessionCondition::new);
}
