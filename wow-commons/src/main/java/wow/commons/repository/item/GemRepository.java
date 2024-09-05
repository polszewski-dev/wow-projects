package wow.commons.repository.item;

import wow.commons.model.item.Gem;
import wow.commons.model.item.SocketType;
import wow.commons.model.pve.PhaseId;

import java.util.List;
import java.util.Optional;

/**
 * User: POlszewski
 * Date: 2021-03-02
 */
public interface GemRepository {
	Optional<Gem> getGem(int gemId, PhaseId phaseId);

	Optional<Gem> getGem(String name, PhaseId phaseId);

	List<Gem> getGems(SocketType socketType, PhaseId phaseId);
}
