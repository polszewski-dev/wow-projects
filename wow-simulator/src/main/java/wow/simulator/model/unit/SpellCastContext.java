package wow.simulator.model.unit;

import wow.character.model.snapshot.Snapshot;
import wow.commons.model.Duration;
import wow.commons.model.spells.Cost;
import wow.commons.model.spells.Spell;

/**
 * User: POlszewski
 * Date: 2023-08-13
 */
public record SpellCastContext(
		Unit caster,
		Unit target,
		Snapshot snapshot,
		Cost cost,
		Cost costUnreduced
) {
	public Spell spell() {
		return snapshot.getSpell();
	}

	public Duration getGcd() {
		return Duration.seconds(snapshot.getGcd());
	}

	public Duration getCastTime() {
		return Duration.seconds(snapshot.getCastTime());
	}

	public boolean isInstantCast() {
		return snapshot.isInstantCast();
	}

	public SpellConversions getConversions() {
		return new SpellConversions(this);
	}

	public void paySpellCost() {
		caster.paySpellCost(this);

		getConversions().performPaidCostConversion();
	}

	public void decreaseHealth(int amount, boolean critRoll) {
		int actualDamage = target.decreaseHealth(amount, critRoll, spell());

		getConversions().performDamageDoneConversion(actualDamage);
	}
}
