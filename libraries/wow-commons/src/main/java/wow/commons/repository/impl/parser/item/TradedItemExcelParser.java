package wow.commons.repository.impl.parser.item;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import polszewski.excel.reader.templates.ExcelParser;
import polszewski.excel.reader.templates.ExcelSheetParser;
import wow.commons.model.item.TradedItem;
import wow.commons.model.item.TradedItemId;
import wow.commons.model.pve.PhaseId;
import wow.commons.repository.pve.FactionRepository;
import wow.commons.repository.pve.NpcRepository;
import wow.commons.repository.pve.ZoneRepository;
import wow.commons.repository.spell.SpellRepository;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static wow.commons.repository.impl.parser.item.ItemBaseExcelSheetNames.TRADE;

/**
 * User: POlszewski
 * Date: 2022-10-30
 */
@Component
@Scope("prototype")
@RequiredArgsConstructor
public class TradedItemExcelParser extends ExcelParser {
	@Value("${traded.items.xls.file.path}")
	private final String xlsFilePath;

	private final ZoneRepository zoneRepository;
	private final NpcRepository npcRepository;
	private final FactionRepository factionRepository;
	private final SpellRepository spellRepository;

	@Getter
	private final List<TradedItem> tradedItems = new ArrayList<>();

	@Override
	protected InputStream getExcelInputStream() {
		return fromResourcePath(xlsFilePath);
	}

	@Override
	protected Stream<ExcelSheetParser> getSheetParsers() {
		var sourceParserFactory = new SourceParserFactory(zoneRepository, npcRepository, factionRepository, this::getTradedItem);
		return Stream.of(
				new TradedItemSheetParser(TRADE, sourceParserFactory, spellRepository, this)
		);
	}

	void addTradedItem(TradedItem tradedItem) {
		tradedItems.add(tradedItem);
	}

	private Optional<TradedItem> getTradedItem(TradedItemId tradedItemId, PhaseId phaseId) {
		return Optional.empty();
	}
}
