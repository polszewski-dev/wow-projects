package wow.commons.model.item;

import wow.commons.model.categorization.PveRoleClassified;
import wow.commons.model.effect.Effect;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2021-03-06
 */
public interface Gem extends AbstractItem<GemId>, PveRoleClassified {
	GemColor getColor();

	List<MetaEnabler> getMetaEnablers();

	List<Effect> getEffects();

	boolean isMetaConditionTrue(int numRed, int numYellow, int numBlue);
}
