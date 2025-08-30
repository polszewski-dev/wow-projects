package wow.minmax.service;

import wow.commons.model.categorization.ItemSlot;
import wow.minmax.model.*;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2025-08-30
 */
public interface EquipmentOptionsService {
	EquipmentOptions getEquipmentOptions(CharacterId characterId);

	ItemOptions getItemOptions(CharacterId characterId, ItemSlot slot);

	List<EnchantOptions> getEnchantOptions(CharacterId characterId);

	List<GemOptions> getGemOptions(CharacterId characterId);
}
