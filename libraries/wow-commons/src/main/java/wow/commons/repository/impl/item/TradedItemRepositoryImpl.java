package wow.commons.repository.impl.item;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import wow.commons.model.item.TradedItem;
import wow.commons.model.pve.PhaseId;
import wow.commons.repository.impl.parser.item.SourceParserFactory;
import wow.commons.repository.impl.parser.item.TradedItemExcelParser;
import wow.commons.repository.item.TradedItemRepository;
import wow.commons.repository.pve.FactionRepository;
import wow.commons.repository.pve.NpcRepository;
import wow.commons.repository.pve.ZoneRepository;
import wow.commons.repository.spell.SpellRepository;
import wow.commons.util.PhaseMap;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Optional;

import static wow.commons.util.PhaseMap.putForEveryPhase;

/**
 * User: POlszewski
 * Date: 2021-03-02
 */
@Component
@RequiredArgsConstructor
public class TradedItemRepositoryImpl implements TradedItemRepository {
	private final ZoneRepository zoneRepository;
	private final NpcRepository npcRepository;
	private final FactionRepository factionRepository;
	private final SpellRepository spellRepository;

	private final PhaseMap<Integer, TradedItem> tradedItemById = new PhaseMap<>();

	@Value("${traded.items.xls.file.path}")
	private String xlsFilePath;

	@Override
	public Optional<TradedItem> getTradedItem(int tradedItemId, PhaseId phaseId) {
		return tradedItemById.getOptional(phaseId, tradedItemId);
	}

	@PostConstruct
	public void init() throws IOException {
		var parserFactory = new SourceParserFactory(zoneRepository, npcRepository, factionRepository, this);
		var parser = new TradedItemExcelParser(xlsFilePath, parserFactory, this, spellRepository);
		parser.readFromXls();
	}

	public void addTradedItem(TradedItem tradedItem) {
		putForEveryPhase(tradedItemById, tradedItem.getId(), tradedItem);
	}
}
