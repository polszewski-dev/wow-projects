package wow.commons.repository.impl.parsers.items;

import wow.commons.model.item.Item;
import wow.commons.repository.PVERepository;
import wow.commons.repository.impl.ItemDataRepositoryImpl;
import wow.commons.util.ExcelParser;
import wow.commons.util.ExcelSheetParser;

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
	protected Stream<ExcelSheetParser> getSheetParsers() {
		return Stream.of(
				new TradedItemSheetParser(TRADE, pveRepository, itemDataRepository),
				new ItemSheetParser(ITEM, pveRepository, itemDataRepository, setPiecesByName),
				new GemSheetParser(GEM, pveRepository, itemDataRepository),
				new ItemSetSheetParser(SET, itemDataRepository, setPiecesByName)
		);
	}
}
