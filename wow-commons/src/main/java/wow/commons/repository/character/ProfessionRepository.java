package wow.commons.repository.character;

import wow.commons.model.profession.Profession;
import wow.commons.model.profession.ProfessionId;
import wow.commons.model.profession.ProfessionProficiency;
import wow.commons.model.profession.ProfessionProficiencyId;
import wow.commons.model.pve.GameVersionId;

import java.util.Optional;

/**
 * User: POlszewski
 * Date: 27.09.2024
 */
public interface ProfessionRepository {
	Optional<Profession> getProfession(ProfessionId professionId, GameVersionId gameVersionId);

	Optional<ProfessionProficiency> getProficiency(ProfessionProficiencyId professionProficiencyId, GameVersionId gameVersionId);
}
