package wow.simulator.config;

import wow.character.service.CharacterCalculationService;
import wow.character.service.CharacterService;
import wow.commons.repository.item.EnchantRepository;
import wow.commons.repository.item.GemRepository;
import wow.commons.repository.item.ItemRepository;

/**
 * User: POlszewski
 * Date: 2023-08-08
 */
public interface SimulatorContext {
	CharacterService getCharacterService();

	CharacterCalculationService getCharacterCalculationService();

	ItemRepository getItemRepository();

	EnchantRepository getEnchantRepository();

	GemRepository getGemRepository();
}
