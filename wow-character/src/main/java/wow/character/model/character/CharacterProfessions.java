package wow.character.model.character;

import wow.character.model.Copyable;
import wow.commons.model.attributes.AttributeCondition;
import wow.commons.model.professions.ProfessionId;
import wow.commons.model.professions.ProfessionSpecializationId;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * User: POlszewski
 * Date: 2022-11-30
 */
public class CharacterProfessions implements Copyable<CharacterProfessions> {
	private static final int MAX_PROFESSIONS = 2;

	private final List<CharacterProfession> professions = new ArrayList<>();

	@Override
	public CharacterProfessions copy() {
		CharacterProfessions copy = new CharacterProfessions();
		copy.setProfessions(professions);
		return copy;
	}

	public void setProfessions(List<CharacterProfession> professions) {
		assertProfessionsAreCorrect(professions);
		this.professions.clear();
		this.professions.addAll(professions);
	}

	public void addProfession(Profession profession, ProfessionSpecialization specialization) {
		var newList = new ArrayList<>(professions);
		newList.add(new CharacterProfession(profession, specialization));
		setProfessions(newList);
	}

	public void reset() {
		professions.clear();
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

	private static void assertProfessionsAreCorrect(List<CharacterProfession> professions) {
		if (professions.size() > MAX_PROFESSIONS) {
			throw new IllegalArgumentException("At most %s professions allowed".formatted(MAX_PROFESSIONS));
		}
		if (professions.stream().map(CharacterProfession::getProfessionId).distinct().count() != professions.size()) {
			throw new IllegalArgumentException("Can't have 2 identical professions");
		}
	}
}
