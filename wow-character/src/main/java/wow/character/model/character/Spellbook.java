package wow.character.model.character;

import lombok.AllArgsConstructor;
import lombok.Getter;
import wow.character.model.Copyable;
import wow.commons.model.spell.Spell;
import wow.commons.model.spell.SpellId;

import java.util.*;

/**
 * User: POlszewski
 * Date: 2023-05-11
 */
@AllArgsConstructor
@Getter
public class Spellbook implements Copyable<Spellbook> {
	private final Map<SpellId, List<Spell>> spellById = new EnumMap<>(SpellId.class);

	@Override
	public Spellbook copy() {
		Spellbook copy = new Spellbook();

		for (Map.Entry<SpellId, List<Spell>> entry : this.spellById.entrySet()) {
			copy.spellById.put(entry.getKey(), new ArrayList<>(entry.getValue()));
		}

		return copy;
	}

	public void reset() {
		this.spellById.clear();
	}

	public void addSpell(Spell spell) {
		if (getSpell(spell.getSpellId(), spell.getRank()).isPresent()) {
			return;
		}
		spellById.computeIfAbsent(spell.getSpellId(), x -> new ArrayList<>()).add(spell);
	}

	public void addSpells(List<Spell> spells) {
		for (Spell spell : spells) {
			addSpell(spell);
		}
	}

	public Optional<Spell> getSpell(SpellId spellId) {
		return spellById.getOrDefault(spellId, List.of()).stream()
				.max(Comparator.comparingInt(Spell::getRank));
	}

	public Optional<Spell> getSpell(SpellId spellId, int rank) {
		return spellById.getOrDefault(spellId, List.of()).stream()
				.filter(x -> x.getRank() == rank)
				.findAny();
	}
}
