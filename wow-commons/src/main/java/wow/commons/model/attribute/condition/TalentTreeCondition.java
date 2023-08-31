package wow.commons.model.attribute.condition;

import wow.commons.model.talent.TalentTree;
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
	public boolean test(AttributeConditionArgs args) {
		return args.getTalentTree() == talentTree;
	}

	@Override
	public String toString() {
		return talentTree.toString();
	}

	private static final Map<TalentTree, TalentTreeCondition> CACHE = EnumUtil.cache(
			TalentTree.class, TalentTree.values(), TalentTreeCondition::new);
}
