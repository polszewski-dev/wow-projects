package wow.commons.model.attributes.condition;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import wow.commons.model.attributes.AttributeCondition;
import wow.commons.model.talents.TalentTree;

/**
 * User: POlszewski
 * Date: 2022-11-11
 */
@AllArgsConstructor
@EqualsAndHashCode
public class TalentTreeCondition implements AttributeCondition {
	@NonNull
	private final TalentTree talentTree;

	@Override
	public String getConditionString() {
		return "tree: " + talentTree;
	}

	@Override
	public String toString() {
		return talentTree.toString();
	}
}
