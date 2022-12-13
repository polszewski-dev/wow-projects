package wow.commons.model.attributes.condition;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import wow.commons.model.attributes.AttributeCondition;
import wow.commons.model.spells.SpellSchool;
import wow.commons.util.EnumUtil;

import java.util.Map;

/**
 * User: POlszewski
 * Date: 2022-11-11
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
public class SpellSchoolCondition implements AttributeCondition {
	@NonNull
	private final SpellSchool spellSchool;

	public static SpellSchoolCondition of(SpellSchool spellSchool) {
		return CACHE.get(spellSchool);
	}

	@Override
	public String getConditionString() {
		return "school: " + spellSchool;
	}

	@Override
	public String toString() {
		return spellSchool.toString();
	}

	private static final Map<SpellSchool, SpellSchoolCondition> CACHE = EnumUtil.cache(
			SpellSchool.class, SpellSchool.values(), SpellSchoolCondition::new);
}
