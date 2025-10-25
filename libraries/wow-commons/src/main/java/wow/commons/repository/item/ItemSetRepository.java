package wow.commons.repository.item;

import wow.commons.model.item.ItemSet;
import wow.commons.model.pve.PhaseId;

import java.util.Optional;

/**
 * User: POlszewski
 * Date: 2025-10-25
 */
public interface ItemSetRepository {
	Optional<ItemSet> getItemSet(String name, PhaseId phaseId);
}
