package wow.commons.model.spell;

import wow.commons.model.effect.EffectSource;

/**
 * User: POlszewski
 * Date: 2023-10-24
 */
public interface ActivatedAbility extends Ability {
	@Override
	default SpellType getType() {
		return SpellType.ACTIVATED_ABILITY;
	}

	EffectSource getSource();

	@Override
	default int getRank() {
		return 0;
	}

	@Override
	default Cost getCost() {
		return null;
	}
}
