package wow.commons.model.unit;

import lombok.Getter;
import wow.commons.model.professions.Profession;
import wow.commons.model.professions.ProfessionSpecialization;
import wow.commons.model.pve.Side;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2022-10-31
 */
@Getter
public class CharacterInfo {
	private final CharacterClass characterClass;
	private final Race race;
	private final int level;
	private final List<CharacterProfession> professions;

	public CharacterInfo(CharacterClass characterClass, Race race, int level, List<CharacterProfession> professions) {
		this.characterClass = characterClass;
		this.race = race;
		this.level = level;
		this.professions = professions;
		if (professions.size() > 2) {
			throw new IllegalArgumentException("At most 2 professions allowed");
		}
		if (professions.stream().map(CharacterProfession::getProfession).distinct().count() != professions.size()) {
			throw new IllegalArgumentException("Can't have 2 identical professions");
		}
	}

	public Side getSide() {
		return race.getSide();
	}

	public boolean hasProfession(Profession profession) {
		return professions.stream().anyMatch(x -> x.getProfession() == profession);//todo sprawdzic level!!!
	}

	public boolean hasProfessionSpecialization(ProfessionSpecialization specialization) {
		return professions.stream().anyMatch(x -> x.getSpecialization() == specialization);
	}

	public int getProfessionLevel(Profession profession) {
		return professions.stream()
				.filter(x -> x.getProfession() == profession)
				.mapToInt(CharacterProfession::getLevel)
				.findFirst()
				.orElse(0);
	}
}
