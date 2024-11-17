package wow.simulator.model.context;

import wow.commons.model.spell.Spell;
import wow.simulator.model.unit.Unit;

/**
 * User: POlszewski
 * Date: 2023-11-04
 */
public abstract class Context {
	protected final Unit caster;
	protected final Spell spell;

	protected Context(Unit caster, Spell spell) {
		this.caster = caster;
		this.spell = spell;
	}

	protected abstract Conversions getConversions();

	protected void decreaseHealth(Unit target, int amount, boolean directDamage, boolean critRoll) {
		int actualDamage = target.decreaseHealth(amount, critRoll, spell);

		getConversions().performDamageDoneConversion(actualDamage);

		EventContext.fireSpellDamageEvent(caster, target, spell, directDamage, critRoll);
	}

	protected void increaseMana(Unit target, int amount) {
		target.increaseMana(amount, false, spell);
	}
}
