package wow.simulator.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Component;
import wow.character.service.CharacterCalculationService;
import wow.character.service.CharacterService;
import wow.character.service.ItemService;
import wow.commons.repository.item.EnchantRepository;
import wow.commons.repository.item.GemRepository;
import wow.commons.repository.item.ItemRepository;

/**
 * User: POlszewski
 * Date: 2023-08-08
 */
@Component
@AllArgsConstructor
@Getter
public class SimulatorContextImpl implements SimulatorContext {
	private final CharacterService characterService;
	private final ItemService itemService;
	private final ItemRepository itemRepository;
	private final EnchantRepository enchantRepository;
	private final GemRepository gemRepository;
	private final CharacterCalculationService characterCalculationService;
}
