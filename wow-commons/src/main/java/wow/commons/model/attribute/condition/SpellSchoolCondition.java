package wow.commons.model.attribute.condition;

import wow.commons.model.spell.SpellSchool;
import wow.commons.util.EnumUtil;

import java.util.Map;
import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2022-11-11
 */
public record SpellSchoolCondition(SpellSchool spellSchool) implements AttributeCondition {
	public SpellSchoolCondition {
		Objects.requireNonNull(spellSchool);
	}

	public static SpellSchoolCondition of(SpellSchool spellSchool) {
		return CACHE.get(spellSchool);
	}

	@Override
	public boolean test(AttributeConditionArgs args) {
		return args.getSpellSchool() == spellSchool;
	}

	@Override
	public String toString() {
		return spellSchool.toString();
	}

	private static final Map<SpellSchool, SpellSchoolCondition> CACHE = EnumUtil.cache(
			SpellSchool.class, SpellSchool.values(), SpellSchoolCondition::new);
}
