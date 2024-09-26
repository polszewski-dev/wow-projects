package wow.character.model.character;

import wow.character.model.Copyable;
import wow.commons.model.profession.Profession;
import wow.commons.model.profession.ProfessionId;
import wow.commons.model.profession.ProfessionSpecialization;
import wow.commons.model.profession.ProfessionSpecializationId;

import java.util.ArrayList;
import java.util.List;

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

	public void addProfession(Profession profession, ProfessionSpecialization specialization, int level) {
		var newList = new ArrayList<>(professions);
		newList.add(new CharacterProfession(profession, specialization, level));
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

	public boolean hasProfession(ProfessionId professionId, int level) {
		return professions.stream().anyMatch(x -> x.getProfessionId() == professionId && x.getLevel() >= level);
	}

	public boolean hasProfessionSpecialization(ProfessionSpecializationId specializationId) {
		return professions.stream().anyMatch(x -> x.getSpecializationId() == specializationId);
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
