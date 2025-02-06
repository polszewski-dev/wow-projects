package wow.simulator.model.context;

import wow.character.model.snapshot.PeriodicSpellDamageSnapshot;
import wow.commons.model.effect.component.ComponentType;
import wow.simulator.model.effect.EffectInstance;
import wow.simulator.model.unit.Unit;
import wow.simulator.util.RoundingReminder;

/**
 * User: POlszewski
 * Date: 2023-11-04
 */
public class EffectUpdateContext extends Context {
	private final EffectInstance effect;
	private final Unit target;
	private final PeriodicSpellDamageSnapshot spellDamageSnapshot;

	private final RoundingReminder roundingReminder = new RoundingReminder();

	public EffectUpdateContext(Unit caster, EffectInstance effect, Context parentContext) {
		super(caster, effect.getSourceSpell(), parentContext);
		this.effect = effect;
		this.target = effect.getTarget();
		this.spellDamageSnapshot = getSpellDamageSnapshot();
	}

	public void dealPeriodicDamage(int tickNo, int numStacks) {
		var tickDamage = numStacks * spellDamageSnapshot.getTickDamage(tickNo);
		var roundedTickDamage = roundingReminder.roundValue(tickDamage);

		decreaseHealth(target, roundedTickDamage, false, false);
	}

	public void periodicManaGain(int numStacks) {
		var amount = numStacks * effect.getPeriodicComponent().amount();
		increaseMana(target, amount);
	}

	private PeriodicSpellDamageSnapshot getSpellDamageSnapshot() {
		if (effect.hasPeriodicComponent(ComponentType.DAMAGE)) {
			return caster.getPeriodicSpellDamageSnapshot(spell, target);
		}
		return null;
	}
}
