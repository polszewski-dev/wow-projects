package wow.commons.model.config;

import wow.commons.model.character.CharacterClass;
import wow.commons.model.character.Race;
import wow.commons.model.professions.Profession;
import wow.commons.model.professions.ProfessionSpecialization;
import wow.commons.model.talents.TalentId;

/**
 * User: POlszewski
 * Date: 2022-12-20
 */
public interface CharacterInfo {
	int getLevel();

	CharacterClass getCharacterClass();

	Race getRace();

	boolean hasProfession(Profession profession, int level);

	boolean hasProfessionSpecialization(ProfessionSpecialization specialization);

	boolean hasTalent(TalentId talentId);
}
