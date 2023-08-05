package wow.commons.model.config;

import wow.commons.model.categorization.PveRole;
import wow.commons.model.character.CharacterClassId;
import wow.commons.model.character.ExclusiveFaction;
import wow.commons.model.character.PetType;
import wow.commons.model.character.RaceId;
import wow.commons.model.professions.ProfessionId;
import wow.commons.model.professions.ProfessionSpecializationId;
import wow.commons.model.pve.Side;
import wow.commons.model.spells.SpellId;
import wow.commons.model.talents.TalentId;

/**
 * User: POlszewski
 * Date: 2022-12-20
 */
public interface CharacterInfo {
	int getLevel();

	CharacterClassId getCharacterClassId();

	RaceId getRaceId();

	Side getSide();

	PveRole getRole();

	boolean hasProfession(ProfessionId professionId);

	boolean hasProfession(ProfessionId professionId, int level);

	boolean hasProfessionSpecialization(ProfessionSpecializationId specializationId);

	boolean hasActivePet(PetType petType);

	boolean hasExclusiveFaction(ExclusiveFaction exclusiveFaction);

	boolean hasSpell(SpellId spellId);

	boolean hasTalent(TalentId talentId);
}
