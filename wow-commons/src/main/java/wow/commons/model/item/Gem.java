package wow.commons.model.item;

import wow.commons.model.categorization.PveRoleClassified;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2021-03-06
 */
public interface Gem extends AbstractItem, PveRoleClassified {
	GemColor getColor();

	List<MetaEnabler> getMetaEnablers();

	default String getShorterName() {
		if (getColor() == GemColor.META) {
			return getName();
		}
		return getAttributes().toString();
	}

	default boolean isMetaConditionTrue(int numRed, int numYellow, int numBlue) {
		if (getMetaEnablers() == null) {
			return true;
		}

		return getMetaEnablers().stream().allMatch(metaEnabler -> metaEnabler.isMetaConditionTrue(numRed, numYellow, numBlue));
	}
}
