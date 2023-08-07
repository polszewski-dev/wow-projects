package wow.simulator.config;

import wow.character.service.CharacterCalculationService;
import wow.character.service.CharacterService;
import wow.character.service.ItemService;
import wow.character.service.SpellService;
import wow.commons.repository.ItemRepository;

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
	default ItemService getItemService() {
		return getSimulatorContext().getItemService();
	}

	@Override
	default SpellService getSpellService() {
		return getSimulatorContext().getSpellService();
	}

	@Override
	default CharacterCalculationService getCharacterCalculationService() {
		return getSimulatorContext().getCharacterCalculationService();
	}

	@Override
	default ItemRepository getItemRepository() {
		return getSimulatorContext().getItemRepository();
	}
}
