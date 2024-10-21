package wow.minmax.util;

import wow.commons.model.effect.Effect;
import wow.commons.model.spell.ActivatedAbility;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2023-11-19
 */
public interface NonModifierHandler {
	void handleNonModifier(Effect effect, int stackCount);

	void handleActivatedAbilities(List<ActivatedAbility> activatedAbilities);
}
