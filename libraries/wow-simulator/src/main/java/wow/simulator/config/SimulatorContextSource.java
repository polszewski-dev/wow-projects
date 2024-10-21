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
public interface SimulatorContextSource extends SimulatorContext {
	SimulatorContext getSimulatorContext();

	@Override
	default CharacterService getCharacterService() {
		return getSimulatorContext().getCharacterService();
	}

	@Override
	default CharacterCalculationService getCharacterCalculationService() {
		return getSimulatorContext().getCharacterCalculationService();
	}

	@Override
	default ItemRepository getItemRepository() {
		return getSimulatorContext().getItemRepository();
	}

	@Override
	default EnchantRepository getEnchantRepository() {
		return getSimulatorContext().getEnchantRepository();
	}

	@Override
	default GemRepository getGemRepository() {
		return getSimulatorContext().getGemRepository();
	}
}
