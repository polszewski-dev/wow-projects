package wow.commons.model.config;

import wow.commons.model.categorization.PveRole;
import wow.commons.model.character.CharacterClassId;
import wow.commons.model.character.ExclusiveFaction;
import wow.commons.model.character.PetType;
import wow.commons.model.character.RaceId;
import wow.commons.model.profession.ProfessionId;
import wow.commons.model.profession.ProfessionSpecializationId;
import wow.commons.model.pve.Side;
import wow.commons.model.spell.AbilityId;
import wow.commons.model.talent.TalentId;

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

	boolean hasAbility(AbilityId abilityId);

	boolean hasTalent(TalentId talentId);

	boolean hasTalent(TalentId talentId, int rank);
}
