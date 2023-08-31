package wow.simulator.model.unit;

import lombok.Getter;
import wow.character.model.character.Character;
import wow.character.model.equipment.EquippableItem;
import wow.commons.model.attribute.Attributes;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.spell.Spell;
import wow.commons.model.spell.SpellId;

import java.util.Optional;

/**
 * User: POlszewski
 * Date: 2023-08-07
 */
@Getter
public class Player extends Unit {
	private final Character character;

	public Player(String name, Character character) {
		super(name);
		this.character = character;
		this.resources.setHealth(1_000_000_000, 1_000_000_000);
	}

	public void equip(EquippableItem item) {
		character.equip(item);
	}

	public void equip(EquippableItem item, ItemSlot slot) {
		character.equip(item, slot);
	}

	@Override
	public Attributes getStats() {
		return character.getStats();
	}

	@Override
	public Optional<Spell> getSpell(SpellId spellId) {
		return character.getSpellbook().getSpell(spellId);
	}
}
