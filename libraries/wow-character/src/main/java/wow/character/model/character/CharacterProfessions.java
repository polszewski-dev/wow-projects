package wow.character.model.character;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import wow.character.model.Copyable;
import wow.commons.model.profession.Profession;
import wow.commons.model.profession.ProfessionId;
import wow.commons.model.profession.ProfessionSpecializationId;
import wow.commons.model.pve.Phase;

import java.util.*;
import java.util.function.Function;

/**
 * User: POlszewski
 * Date: 2022-11-30
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class CharacterProfessions implements Copyable<CharacterProfessions> {
	private static final int MAX_PROFESSIONS = 2;

	private final Map<ProfessionId, Profession> availableProfessionsById;
	private final Phase phase;
	private final int characterLevel;
	private final List<CharacterProfession> professions = Arrays.asList(new CharacterProfession[MAX_PROFESSIONS]);
	private int position = 0;

	public CharacterProfessions(List<Profession> availableProfessions, Phase phase, int characterLevel) {
		this.availableProfessionsById = new EnumMap<>(ProfessionId.class);
		this.phase = phase;
		this.characterLevel = characterLevel;

		for (var profession : availableProfessions) {
			this.availableProfessionsById.put(profession.getProfessionId(), profession);
		}
	}

	public boolean hasProfession(ProfessionId professionId) {
		return professions.stream()
				.filter(Objects::nonNull)
				.anyMatch(x -> x.professionId() == professionId);
	}

	public boolean hasProfession(ProfessionId professionId, int level) {
		return professions.stream()
				.filter(Objects::nonNull)
				.anyMatch(x -> x.professionId() == professionId && x.level() >= level);
	}

	public boolean hasProfessionSpecialization(ProfessionSpecializationId specializationId) {
		return professions.stream()
				.filter(Objects::nonNull)
				.anyMatch(x -> x.specializationId() == specializationId);
	}

	public List<CharacterProfession> getList() {
		return new ArrayList<>(professions);
	}

	public <T> List<T> getList(Function<CharacterProfession, T> mapper) {
		return professions.stream()
				.map(x -> x != null ? mapper.apply(x) : null)
				.toList();
	}

	public void reset() {
		for (int i = 0; i < MAX_PROFESSIONS; ++i) {
			professions.set(i, null);
		}
		this.position = 0;
	}

	public void add(ProfessionId professionId, ProfessionSpecializationId specializationId, int level) {
		var characterProfession = getCharacterProfession(professionId, specializationId, level);

		add(characterProfession);
	}

	public void add(ProfessionId professionId, int level) {
		add(professionId, null, level);
	}

	public void addMaxLevel(ProfessionId professionId, ProfessionSpecializationId specializationId) {
		add(professionId, specializationId, getMaxLevel(professionId));
	}

	public void addMaxLevel(ProfessionId professionId) {
		addMaxLevel(professionId, null);
	}

	private void add(CharacterProfession characterProfession) {
		if (position == MAX_PROFESSIONS) {
			throw new IllegalArgumentException("There are already %s professions".formatted(MAX_PROFESSIONS));
		}

		if (characterProfession != null && hasProfession(characterProfession.professionId())) {
			throw new IllegalArgumentException("Can't have 2 identical professions");
		}

		professions.set(position++, characterProfession);
	}

	public void set(List<ProfIdSpecIdLevel> professions) {
		var characterProfessions = professions.stream()
				.map(this::getCharacterProfession)
				.toList();

		setProfessions(characterProfessions);
	}

	public void setMaxLevels(List<ProfIdSpecId> professions) {
		var characterProfessions = professions.stream()
				.map(this::getCharacterProfessionMaxLevel)
				.toList();

		setProfessions(characterProfessions);
	}

	public void setMaxLevel(int index, ProfIdSpecId profession) {
		if (profession == null) {
			professions.set(index, null);
			return;
		}

		var otherIndex = index == 0 ? 1 : 0;
		var otherProfession = professions.get(otherIndex);

		if (otherProfession != null && otherProfession.professionId() == profession.professionId()) {
			professions.set(otherIndex, null);
		}

		professions.set(index, getCharacterProfessionMaxLevel(profession));
	}

	private CharacterProfession getCharacterProfession(ProfessionId professionId, ProfessionSpecializationId specializationId, int level) {
		var profession = availableProfessionsById.get(professionId);
		var specialization = specializationId != null
				? profession.getSpecialization(specializationId).orElseThrow()
				: null;

		if (level < 1 || level > getMaxLevel(profession)) {
			throw new IllegalArgumentException("Incorrect profession level: " + level);
		}

		return new CharacterProfession(profession, specialization, level);
	}

	private CharacterProfession getCharacterProfession(ProfIdSpecIdLevel profession) {
		if (profession == null) {
			return null;
		}
		return getCharacterProfession(
				profession.professionId(),
				profession.specializationId(),
				profession.level()
		);
	}

	private CharacterProfession getCharacterProfessionMaxLevel(ProfIdSpecId profession) {
		if (profession == null) {
			return null;
		}
		return getCharacterProfession(
				profession.professionId(),
				profession.specializationId(),
				getMaxLevel(profession.professionId())
		);
	}

	private int getMaxLevel(ProfessionId professionId) {
		var profession = availableProfessionsById.get(professionId);

		return getMaxLevel(profession);
	}

	private int getMaxLevel(Profession profession) {
		return phase.getMaxProfessionLevel(profession, characterLevel);
	}

	private void setProfessions(List<CharacterProfession> professions) {
		reset();

		for (var profession : professions) {
			add(profession);
		}
	}

	@Override
	public CharacterProfessions copy() {
		var copy = new CharacterProfessions(availableProfessionsById, phase, characterLevel);
		copy.position = position;
		copy.setProfessions(professions);
		return copy;
	}
}
