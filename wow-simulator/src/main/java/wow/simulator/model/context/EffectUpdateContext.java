package wow.simulator.model.context;

import wow.simulator.model.effect.UnitEffect;
import wow.simulator.model.unit.Unit;

/**
 * User: POlszewski
 * Date: 2023-11-04
 */
public class EffectUpdateContext extends Context {
	private final UnitEffect effect;
	private final Unit target;

	private double roundingReminder;

	public EffectUpdateContext(Unit caster, UnitEffect effect) {
		super(caster, effect.getSourceAbility());
		this.effect = effect;
		this.target = effect.getTarget();
	}

	public void dealPeriodicDamage(int tickNo, int numStacks) {
		var snapshot = caster.getPeriodicSpellDamageSnapshot(ability, target);

		var tickDamage = numStacks * snapshot.getTickDamage(tickNo) + roundingReminder;
		var roundedTickDamage = (int) tickDamage;

		roundingReminder = tickDamage - roundedTickDamage;

		decreaseHealth(target, roundedTickDamage, false);
	}

	@Override
	protected Conversions getConversions() {
		return new EffectUpdateConversions(caster, effect);
	}
}
