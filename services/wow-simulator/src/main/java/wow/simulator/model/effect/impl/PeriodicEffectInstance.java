package wow.simulator.model.effect.impl;

import wow.commons.model.Duration;
import wow.commons.model.effect.Effect;
import wow.commons.model.effect.EffectSource;
import wow.commons.model.effect.component.PeriodicComponent;
import wow.commons.model.spell.Spell;
import wow.simulator.model.context.Context;
import wow.simulator.model.unit.Unit;

/**
 * User: POlszewski
 * Date: 2023-11-01
 */
public class PeriodicEffectInstance extends EffectInstanceImpl {
	private final Duration tickInterval;
	private int tickNo;

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
	}

	@Override
	protected void doSetUp() {
		scheduleNextTick();
	}

	private void tick() {
		++tickNo;

		if (effect.hasPeriodicComponent()) {
			execPeriodicComponent(effect.getPeriodicComponent());
		}

		scheduleNextTick();
	}

	private void execPeriodicComponent(PeriodicComponent periodicComponent) {
		switch (periodicComponent.type()) {
			case DAMAGE ->
					effectUpdateContext.dealPeriodicDamage(tickNo, numStacks);
			case MANA_GAIN ->
					effectUpdateContext.periodicManaGain(numStacks);
			default ->
					throw new UnsupportedOperationException();
		}
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
