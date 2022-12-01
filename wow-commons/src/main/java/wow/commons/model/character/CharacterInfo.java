package wow.commons.model.character;

import lombok.AllArgsConstructor;
import lombok.Getter;
import wow.commons.model.professions.Profession;
import wow.commons.model.professions.ProfessionSpecialization;
import wow.commons.model.pve.Phase;
import wow.commons.model.pve.Side;
import wow.commons.model.talents.TalentId;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2022-10-31
 */
@AllArgsConstructor
@Getter
public class CharacterInfo {
	private final CharacterClass characterClass;
	private final Race race;
	private final int level;
	private final Build build;
	private final CharacterProfessions characterProfessions;
	private final Phase phase;

	public Side getSide() {
		return race.getSide();
	}

	public List<CharacterProfession> getProfessions() {
		return characterProfessions.getProfessions();
	}

	public boolean hasProfession(Profession profession) {
		return characterProfessions.hasProfession(profession);
	}

	public boolean hasProfession(Profession profession, int level) {
		return characterProfessions.hasProfession(profession, level);
	}

	public boolean hasProfessionSpecialization(ProfessionSpecialization specialization) {
		return characterProfessions.hasProfessionSpecialization(specialization);
	}

	public boolean hasTalent(TalentId talentId) {
		return build.hasTalent(talentId);
	}

	public CharacterInfo setBuild(Build build) {
		if (build == this.build) {
			return this;
		}
		return new CharacterInfo(
				characterClass,
				race,
				level,
				build,
				characterProfessions,
				phase
		);
	}

	public CharacterInfo setPhase(Phase phase) {
		if (phase == this.phase) {
			return this;
		}
		return new CharacterInfo(
				characterClass,
				race,
				level,
				build,
				characterProfessions,
				phase
		);
	}
}
