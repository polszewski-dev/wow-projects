package wow.commons.repository.impl.parsers.items;

import wow.commons.model.item.Item;
import wow.commons.repository.PVERepository;
import wow.commons.repository.impl.ItemDataRepositoryImpl;
import wow.commons.util.ExcelParser;
import wow.commons.util.ExcelSheetReader;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static wow.commons.repository.impl.parsers.items.ItemBaseExcelSheetNames.*;

/**
 * User: POlszewski
 * Date: 2022-10-30
 */
public class ItemBaseExcelParser extends ExcelParser {
	private final ItemDataRepositoryImpl itemDataRepository;
	private final PVERepository pveRepository;

	private final Map<String, List<Item>> setPiecesByName = new HashMap<>();

	public ItemBaseExcelParser(ItemDataRepositoryImpl itemDataRepository, PVERepository pveRepository) {
		this.itemDataRepository = itemDataRepository;
		this.pveRepository = pveRepository;
	}

	@Override
	protected InputStream getExcelInputStream() {
		return fromResourcePath("/xls/item_base.xls");
	}

	@Override
	protected Stream<ExcelSheetReader> getSheetReaders() {
		return Stream.of(
				new TradedItemSheetReader(TRADE, pveRepository, itemDataRepository),
				new ItemSheetReader(ITEM, pveRepository, itemDataRepository, setPiecesByName),
				new GemSheetReader(GEM, pveRepository, itemDataRepository),
				new ItemSetSheetReader(SET, itemDataRepository, setPiecesByName)
		);
	}
}
