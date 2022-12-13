package wow.commons.model.attributes.condition;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import wow.commons.model.attributes.AttributeCondition;
import wow.commons.model.talents.TalentTree;
import wow.commons.util.EnumUtil;

import java.util.Map;

/**
 * User: POlszewski
 * Date: 2022-11-11
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
public class TalentTreeCondition implements AttributeCondition {
	@NonNull
	private final TalentTree talentTree;

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
