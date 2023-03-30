package wow.character.model.character;

import wow.commons.model.attributes.AttributeCondition;
import wow.commons.model.professions.ProfessionId;
import wow.commons.model.professions.ProfessionSpecializationId;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
		if (professions.stream().map(CharacterProfession::getProfessionId).distinct().count() != professions.size()) {
			throw new IllegalArgumentException("Can't have 2 identical professions");
		}
	}

	public static CharacterProfessions of(List<CharacterProfession> professions) {
		return new CharacterProfessions(professions);
	}

	public List<CharacterProfession> getList() {
		return professions;
	}

	public boolean hasProfession(ProfessionId professionId) {
		return professions.stream().anyMatch(x -> x.getProfessionId() == professionId);
	}

	public boolean hasProfessionSpecialization(ProfessionSpecializationId specializationId) {
		return professions.stream().anyMatch(x -> x.getSpecializationId() == specializationId);
	}

	public Set<AttributeCondition> getConditions() {
		var result = new HashSet<AttributeCondition>();

		for (CharacterProfession profession : professions) {
			result.add(AttributeCondition.of(profession.getProfessionId()));
			result.add(AttributeCondition.of(profession.getSpecializationId()));
		}

		result.remove(AttributeCondition.EMPTY);
		return result;
	}
}
