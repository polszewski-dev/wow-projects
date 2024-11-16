package wow.commons.model.effect;

import wow.commons.model.config.Description;
import wow.commons.model.spell.Ability;
import wow.commons.model.spell.AbilityId;

/**
 * User: POlszewski
 * Date: 2024-11-14
 */
public record AbilitySource(Ability ability) implements EffectSource {
	public AbilityId getAbilityId() {
		return ability.getAbilityId();
	}

	@Override
	public int getPriority() {
		return 0;
	}

	@Override
	public Description getDescription() {
		return ability.getDescription();
	}
}
