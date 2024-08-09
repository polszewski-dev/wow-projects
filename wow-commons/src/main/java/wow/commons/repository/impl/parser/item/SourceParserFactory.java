package wow.commons.repository.impl.parser.item;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.commons.model.pve.PhaseId;
import wow.commons.repository.ItemRepository;
import wow.commons.repository.pve.FactionRepository;
import wow.commons.repository.pve.NpcRepository;
import wow.commons.repository.pve.ZoneRepository;

/**
 * User: POlszewski
 * Date: 05.09.2024
 */
@AllArgsConstructor
@Component
public class SourceParserFactory {
	private final ZoneRepository zoneRepository;
	private final NpcRepository npcRepository;
	private final FactionRepository factionRepository;

	public SourceParser create(PhaseId phaseId, ItemRepository itemRepository) {
		return new SourceParser(phaseId, zoneRepository, npcRepository, factionRepository, itemRepository);
	}
}
