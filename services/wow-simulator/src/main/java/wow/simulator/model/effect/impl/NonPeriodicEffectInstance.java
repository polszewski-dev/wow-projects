package wow.simulator.model.effect.impl;

import lombok.Getter;
import lombok.Setter;
import wow.commons.model.AnyDuration;
import wow.commons.model.effect.Effect;
import wow.commons.model.effect.EffectSource;
import wow.commons.model.spell.Spell;
import wow.simulator.model.context.Context;
import wow.simulator.model.unit.Unit;

/**
 * User: POlszewski
 * Date: 2023-11-01
 */
public class NonPeriodicEffectInstance extends EffectInstanceImpl {
	@Getter
	@Setter
	private boolean hidden;

	public NonPeriodicEffectInstance(
			Unit owner,
			Unit target,
			Effect effect,
			AnyDuration duration,
			int numStacks,
			int numCharges,
			EffectSource effectSource,
			Spell sourceSpell,
			Context parentContext
	) {
		super(owner, target, effect, duration, numStacks, numCharges, effectSource, sourceSpell, parentContext);
	}

	@Override
	protected void doSetUp() {
		fromNowAfter(duration, () -> {});
	}
}
