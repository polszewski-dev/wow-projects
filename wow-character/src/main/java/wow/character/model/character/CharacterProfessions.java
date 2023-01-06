package wow.character.model.character;

import wow.commons.model.professions.Profession;
import wow.commons.model.professions.ProfessionSpecialization;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2022-11-30
 */
public class CharacterProfessions {
	private final List<CharacterProfession> professions;

	public static final CharacterProfessions EMPTY = new CharacterProfessions(List.of());

	private CharacterProfessions(List<CharacterProfession> professions) {
		this.professions = professions;
		if (professions.size() > 2) {
			throw new IllegalArgumentException("At most 2 professions allowed");
		}
		if (professions.stream().map(CharacterProfession::getProfession).distinct().count() != professions.size()) {
			throw new IllegalArgumentException("Can't have 2 identical professions");
		}
	}

	public static CharacterProfessions of(List<CharacterProfession> professions) {
		return new CharacterProfessions(professions);
	}

	public List<CharacterProfession> getList() {
		return professions;
	}

	public boolean hasProfession(Profession profession) {
		return professions.stream().anyMatch(x -> x.getProfession() == profession);
	}

	public boolean hasProfessionSpecialization(ProfessionSpecialization specialization) {
		return professions.stream().anyMatch(x -> x.getSpecialization() == specialization);
	}
}
