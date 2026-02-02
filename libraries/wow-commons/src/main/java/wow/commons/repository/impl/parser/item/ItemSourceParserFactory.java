package wow.commons.repository.impl.parser.item;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.commons.model.pve.PhaseId;
import wow.commons.repository.item.TradedItemRepository;
import wow.commons.repository.pve.FactionRepository;
import wow.commons.repository.pve.NpcRepository;
import wow.commons.repository.pve.ZoneRepository;

/**
 * User: POlszewski
 * Date: 05.09.2024
 */
@AllArgsConstructor
@Component
public class ItemSourceParserFactory {
	private final ZoneRepository zoneRepository;
	private final NpcRepository npcRepository;
	private final FactionRepository factionRepository;
	private final TradedItemRepository tradedItemRepository;

	public ItemSourceParser create(PhaseId phaseId) {
		return new ItemSourceParser(phaseId, zoneRepository, npcRepository, factionRepository, tradedItemRepository);
	}
}
