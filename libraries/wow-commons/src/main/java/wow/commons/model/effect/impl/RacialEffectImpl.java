package wow.commons.model.effect.impl;

import lombok.Getter;
import lombok.Setter;
import wow.commons.model.config.CharacterRestriction;
import wow.commons.model.effect.RacialEffect;
import wow.commons.model.spell.AbilityId;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2024-11-18
 */
@Getter
@Setter
public class RacialEffectImpl extends EffectImpl implements RacialEffect {
	private CharacterRestriction characterRestriction;

	public RacialEffectImpl(List<AbilityId> augmentedAbilities) {
		super(augmentedAbilities);
	}
}
