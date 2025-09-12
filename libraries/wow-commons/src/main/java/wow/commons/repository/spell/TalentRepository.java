package wow.commons.repository.spell;

import wow.commons.model.character.CharacterClassId;
import wow.commons.model.pve.PhaseId;
import wow.commons.model.talent.Talent;
import wow.commons.model.talent.TalentId;

import java.util.List;
import java.util.Optional;

/**
 * User: POlszewski
 * Date: 2020-09-28
 */
public interface TalentRepository {
	List<Talent> getAvailableTalents(CharacterClassId characterClassId, PhaseId phaseId);

	Optional<Talent> getTalent(int talentId, PhaseId phaseId);

	Optional<Talent> getTalent(CharacterClassId characterClassId, TalentId talentId, int rank, PhaseId phaseId);

	Optional<Talent> getTalent(CharacterClassId characterClassId, int talentCalculatorPosition, int rank, PhaseId phaseId);
}
