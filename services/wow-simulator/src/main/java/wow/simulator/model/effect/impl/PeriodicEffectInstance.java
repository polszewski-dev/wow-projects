package wow.simulator.model.effect.impl;

import wow.commons.model.Duration;
import wow.commons.model.effect.Effect;
import wow.commons.model.effect.EffectSource;
import wow.commons.model.effect.component.PeriodicComponent;
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
			EffectSource effectSource
	) {
		super(owner, target, effect, duration, numStacks, numCharges, effectSource);
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
