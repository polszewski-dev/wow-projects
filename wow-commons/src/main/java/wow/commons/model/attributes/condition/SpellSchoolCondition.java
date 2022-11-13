package wow.commons.model.attributes.condition;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import wow.commons.model.attributes.AttributeCondition;
import wow.commons.model.spells.SpellSchool;

/**
 * User: POlszewski
 * Date: 2022-11-11
 */
@AllArgsConstructor
@EqualsAndHashCode
public class SpellSchoolCondition implements AttributeCondition {
	@NonNull
	private final SpellSchool spellSchool;

	@Override
	public String toString() {
		return "school: " + spellSchool;
	}
}
