package wow.character.model.character;

import lombok.AllArgsConstructor;
import lombok.Getter;
import wow.character.model.Copyable;
import wow.commons.model.spell.Ability;
import wow.commons.model.spell.AbilityId;

import java.util.*;

/**
 * User: POlszewski
 * Date: 2023-05-11
 */
@AllArgsConstructor
@Getter
public class Spellbook implements Copyable<Spellbook> {
	private final Map<AbilityId, List<Ability>> abilityById = new HashMap<>();

	@Override
	public Spellbook copy() {
		Spellbook copy = new Spellbook();

		for (var entry : this.abilityById.entrySet()) {
			copy.abilityById.put(entry.getKey(), new ArrayList<>(entry.getValue()));
		}

		return copy;
	}

	public void reset() {
		this.abilityById.clear();
	}

	public void addAbility(Ability ability) {
		if (getAbility(ability.getAbilityId(), ability.getRank()).isPresent()) {
			return;
		}
		abilityById.computeIfAbsent(ability.getAbilityId(), x -> new ArrayList<>()).add(ability);
	}

	public void addAbilities(List<Ability> abilities) {
		abilities.forEach(this::addAbility);
	}

	public Optional<Ability> getAbility(AbilityId abilityId) {
		return abilityById.getOrDefault(abilityId, List.of()).stream()
				.max(Comparator.comparingInt(Ability::getRank));
	}

	public Optional<Ability> getAbility(AbilityId abilityId, int rank) {
		return abilityById.getOrDefault(abilityId, List.of()).stream()
				.filter(x -> x.getRank() == rank)
				.findAny();
	}

	public Optional<Ability> getAbility(String abilityName) {
		return getAbility(AbilityId.of(abilityName));
	}

	public Optional<Ability> getAbility(String abilityName, int rank) {
		return getAbility(AbilityId.of(abilityName), rank);
	}
}
