package wow.simulator.model.effect.impl;

import wow.commons.model.Duration;
import wow.commons.model.effect.Effect;
import wow.commons.model.effect.component.PeriodicComponent;
import wow.commons.model.spell.Spell;
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
			Spell sourceAbility,
			Duration duration,
			Duration tickInterval,
			int numStacks,
			int numCharges
	) {
		super(owner, target, effect, sourceAbility, duration, numStacks, numCharges);
		this.tickInterval = tickInterval;
	}

	@Override
	protected void doSetUp() {
		scheduleNextTick();
	}

	private void tick() {
		++tickNo;

		var periodicComponent = effect.getPeriodicComponent();

		if (periodicComponent != null) {
			execPeriodicComponent(periodicComponent);
		}

		// todo fire tick event!!!

		scheduleNextTick();
	}

	private void execPeriodicComponent(PeriodicComponent periodicComponent) {
		switch (periodicComponent.type()) {
			case DAMAGE ->
					resolutionContext.dealPeriodicDamage(tickNo, numStacks);
			case MANA_GAIN ->
					resolutionContext.periodicManaGain(numStacks);
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
