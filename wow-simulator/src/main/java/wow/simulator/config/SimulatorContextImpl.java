package wow.simulator.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Component;
import wow.character.service.CharacterCalculationService;
import wow.character.service.CharacterService;
import wow.character.service.ItemService;
import wow.character.service.SpellService;
import wow.commons.repository.ItemRepository;

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
	private final SpellService spellService;
	private final ItemRepository itemRepository;
	private final CharacterCalculationService characterCalculationService;
}
