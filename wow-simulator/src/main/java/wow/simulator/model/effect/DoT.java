package wow.simulator.model.effect;

import wow.simulator.model.rng.RngStrategies;
import wow.simulator.model.unit.SpellCastContext;

/**
 * User: POlszewski
 * Date: 2023-08-17
 */
public class DoT extends PeriodicEffect {
	public DoT(SpellCastContext context) {
		super(context);
	}

	@Override
	protected double getTotalAmount() {
		return context.snapshot().getDotDamage(RngStrategies.DOT);
	}

	@Override
	protected void onTick(int tickAmount) {
		context.decreaseHealth(tickAmount, false);
	}
}
