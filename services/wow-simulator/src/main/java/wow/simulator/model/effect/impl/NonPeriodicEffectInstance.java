package wow.simulator.model.effect.impl;

import wow.commons.model.Duration;
import wow.commons.model.effect.Effect;
import wow.commons.model.spell.Ability;
import wow.simulator.model.unit.Unit;

/**
 * User: POlszewski
 * Date: 2023-11-01
 */
public class NonPeriodicEffectInstance extends EffectInstanceImpl {
	public NonPeriodicEffectInstance(
			Unit owner,
			Unit target,
			Effect effect,
			Ability sourceAbility,
			Duration duration,
			int numStacks,
			int numCharges
	) {
		super(owner, target, effect, sourceAbility, duration, numStacks, numCharges);
	}

	@Override
	protected void doSetUp() {
		fromNowAfter(duration, () -> {});
	}

	@Override
	public Duration getTickInterval() {
		return null;
	}
}
