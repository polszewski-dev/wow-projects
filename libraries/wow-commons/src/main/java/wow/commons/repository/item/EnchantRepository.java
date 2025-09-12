package wow.commons.repository.item;

import wow.commons.model.categorization.ItemSubType;
import wow.commons.model.categorization.ItemType;
import wow.commons.model.item.Enchant;
import wow.commons.model.item.EnchantId;
import wow.commons.model.pve.PhaseId;

import java.util.List;
import java.util.Optional;

/**
 * User: POlszewski
 * Date: 2021-03-02
 */
public interface EnchantRepository {
	Optional<Enchant> getEnchant(EnchantId enchantId, PhaseId phaseId);

	Optional<Enchant> getEnchant(String name, PhaseId phaseId);

	List<Enchant> getEnchants(ItemType itemType, ItemSubType itemSubType, PhaseId phaseId);
}
