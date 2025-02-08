package wow.commons.model.attribute;

import wow.commons.model.talent.TalentTree;

/**
 * User: POlszewski
 * Date: 2025-02-08
 */
public interface AttributeScalingParams {
	int getLevel();

	int getNumberOfEffectsOnTarget(TalentTree tree);
}
