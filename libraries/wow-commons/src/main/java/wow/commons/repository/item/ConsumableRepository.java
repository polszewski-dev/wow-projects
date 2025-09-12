package wow.commons.repository.item;

import wow.commons.model.item.Consumable;
import wow.commons.model.item.ConsumableId;
import wow.commons.model.pve.PhaseId;

import java.util.List;
import java.util.Optional;

/**
 * User: POlszewski
 * Date: 2021-03-02
 */
public interface ConsumableRepository {
	Optional<Consumable> getConsumable(ConsumableId consumableId, PhaseId phaseId);

	Optional<Consumable> getConsumable(String name, PhaseId phaseId);

	List<Consumable> getAvailableConsumables(PhaseId phaseId);
}
