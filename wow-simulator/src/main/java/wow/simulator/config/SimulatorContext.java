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
public interface SimulatorContext {
	CharacterService getCharacterService();

	ItemService getItemService();

	SpellService getSpellService();

	CharacterCalculationService getCharacterCalculationService();

	ItemRepository getItemRepository();
}
