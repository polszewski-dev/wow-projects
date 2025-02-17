package wow.simulator.model.effect.impl;

import wow.commons.model.Duration;
import wow.commons.model.effect.Effect;
import wow.commons.model.effect.EffectSource;
import wow.commons.model.spell.Spell;
import wow.simulator.model.context.Context;
import wow.simulator.model.unit.Unit;

import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2023-11-01
 */
public class PeriodicEffectInstance extends EffectInstanceImpl {
	private final Duration tickInterval;
	protected int tickNo;

	public PeriodicEffectInstance(
			Unit owner,
			Unit target,
			Effect effect,
			Duration duration,
			Duration tickInterval,
			int numStacks,
			int numCharges,
			EffectSource effectSource,
			Spell sourceSpell,
			Context parentContext
	) {
		super(owner, target, effect, duration, numStacks, numCharges, effectSource, sourceSpell, parentContext);
		this.tickInterval = tickInterval;
		Objects.requireNonNull(effect.getPeriodicComponent());
		Objects.requireNonNull(tickInterval);
	}

	@Override
	protected void doSetUp() {
		scheduleNextTick();
	}

	private void tick() {
		++tickNo;
		effectUpdateContext.periodicComponentAction(effect.getPeriodicComponent(), tickNo, numStacks);
		scheduleNextTick();
	}

	private void scheduleNextTick() {
		if (tickInterval != null && getRemainingDuration().isPositive()) {
			fromNowAfter(tickInterval, this::tick);
		}
	}

	@Override
	public Duration getTickInterval() {
		return tickInterval;
	}
}
