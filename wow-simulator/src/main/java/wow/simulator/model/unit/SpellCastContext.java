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
		Cost cost
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
}
