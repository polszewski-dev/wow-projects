package wow.simulator.model.context;

import wow.simulator.model.effect.EffectInstance;
import wow.simulator.model.unit.Unit;

/**
 * User: POlszewski
 * Date: 2023-11-04
 */
public class EffectUpdateContext extends Context {
	private final EffectInstance effect;
	private final Unit target;

	private double roundingReminder;

	public EffectUpdateContext(Unit caster, EffectInstance effect, Context parentContext) {
		super(caster, effect.getSourceSpell(), parentContext);
		this.effect = effect;
		this.target = effect.getTarget();
	}

	public void dealPeriodicDamage(int tickNo, int numStacks) {
		var snapshot = caster.getPeriodicSpellDamageSnapshot(spell, target);

		var tickDamage = numStacks * snapshot.getTickDamage(tickNo) + roundingReminder;
		var roundedTickDamage = (int) tickDamage;

		roundingReminder = tickDamage - roundedTickDamage;

		decreaseHealth(target, roundedTickDamage, false, false);
	}

	public void periodicManaGain(int numStacks) {
		var amount = numStacks * effect.getPeriodicComponent().amount();
		increaseMana(target, amount);
	}

	@Override
	protected Conversions getConversions() {
		return new EffectUpdateConversions(caster, effect);
	}
}
