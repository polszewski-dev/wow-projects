package wow.commons.model.attributes.condition;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import wow.commons.model.attributes.AttributeCondition;
import wow.commons.model.professions.Profession;
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
	private final Profession profession;

	public static ProfessionCondition of(Profession profession) {
		return CACHE.get(profession);
	}

	@Override
	public String getConditionString() {
		return "prof: " + profession;
	}

	@Override
	public String toString() {
		return profession.toString();
	}

	private static final Map<Profession, ProfessionCondition> CACHE = EnumUtil.cache(
			Profession.class, Profession.values(), ProfessionCondition::new);
}
