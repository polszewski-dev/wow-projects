package wow.simulator.model.context;

import wow.commons.model.spell.Ability;
import wow.simulator.model.unit.Unit;

/**
 * User: POlszewski
 * Date: 2023-11-04
 */
public abstract class Context {
	protected final Unit caster;
	protected final Ability ability;

	protected Context(Unit caster, Ability ability) {
		this.caster = caster;
		this.ability = ability;
	}

	protected abstract Conversions getConversions();

	protected void decreaseHealth(Unit target, int amount, boolean critRoll) {
		int actualDamage = target.decreaseHealth(amount, critRoll, ability);

		getConversions().performDamageDoneConversion(actualDamage);
	}

	protected void increaseMana(Unit target, int amount) {
		target.increaseMana(amount, false, ability);
	}
}
