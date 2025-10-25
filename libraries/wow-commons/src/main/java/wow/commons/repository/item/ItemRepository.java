package wow.commons.repository.item;

import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.item.Item;
import wow.commons.model.item.ItemId;
import wow.commons.model.pve.PhaseId;

import java.util.List;
import java.util.Optional;

/**
 * User: POlszewski
 * Date: 2021-03-02
 */
public interface ItemRepository {
	Optional<Item> getItem(ItemId itemId, PhaseId phaseId);

	Optional<Item> getItem(String name, PhaseId phaseId);

	List<Item> getItemsBySlot(ItemSlot itemSlot, PhaseId phaseId);
}
