package wow.commons.model.attributes.condition;

import wow.commons.model.attributes.AttributeCondition;
import wow.commons.model.talents.TalentTree;
import wow.commons.util.EnumUtil;

import java.util.Map;
import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2022-11-11
 */
public record TalentTreeCondition(TalentTree talentTree) implements AttributeCondition {
	public TalentTreeCondition {
		Objects.requireNonNull(talentTree);
	}

	public static TalentTreeCondition of(TalentTree talentTree) {
		return CACHE.get(talentTree);
	}

	@Override
	public String getConditionString() {
		return "tree: " + talentTree;
	}

	@Override
	public String toString() {
		return talentTree.toString();
	}

	private static final Map<TalentTree, TalentTreeCondition> CACHE = EnumUtil.cache(
			TalentTree.class, TalentTree.values(), TalentTreeCondition::new);
}
