package wow.commons.repository.spell;

import wow.commons.model.buff.Buff;
import wow.commons.model.buff.BuffId;
import wow.commons.model.pve.PhaseId;

import java.util.List;
import java.util.Optional;

/**
 * User: POlszewski
 * Date: 2020-09-28
 */
public interface BuffRepository {
	List<Buff> getAvailableBuffs(PhaseId phaseId);

	Optional<Buff> getBuff(BuffId buffId, int rank, PhaseId phaseId);
}
