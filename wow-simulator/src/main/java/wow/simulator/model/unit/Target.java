package wow.simulator.model.unit;

import wow.character.model.character.Enemy;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.spells.Spell;
import wow.commons.model.spells.SpellId;

import java.util.Optional;

/**
 * User: POlszewski
 * Date: 2023-08-07
 */
public class Target extends Unit {
	private final Enemy enemy;

	public Target(String name, Enemy enemy) {
		super(name);
		this.enemy = enemy;
		this.resources.setHealth(1_000_000_000, 1_000_000_000);
	}

	@Override
	public Attributes getStats() {
		return Attributes.EMPTY;
	}

	@Override
	public Optional<Spell> getSpell(SpellId spellId) {
		return Optional.empty();
	}
}
