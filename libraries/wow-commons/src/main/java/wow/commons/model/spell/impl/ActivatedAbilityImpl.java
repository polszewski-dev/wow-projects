package wow.commons.model.spell.impl;

import lombok.Getter;
import wow.commons.model.effect.EffectSource;
import wow.commons.model.spell.ActivatedAbility;

/**
 * User: POlszewski
 * Date: 2023-10-24
 */
@Getter
public class ActivatedAbilityImpl extends AbilityImpl implements ActivatedAbility {
	private EffectSource source;

	public void attachSource(EffectSource source) {
		this.source = source;
	}
}
