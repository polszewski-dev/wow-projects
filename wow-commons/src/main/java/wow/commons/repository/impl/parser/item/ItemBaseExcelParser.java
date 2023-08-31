package wow.commons.repository.impl.parser.item;

import lombok.AllArgsConstructor;
import polszewski.excel.reader.templates.ExcelParser;
import polszewski.excel.reader.templates.ExcelSheetParser;
import wow.commons.model.item.Item;
import wow.commons.model.pve.GameVersionId;
import wow.commons.repository.PveRepository;
import wow.commons.repository.SpellRepository;
import wow.commons.repository.impl.ItemRepositoryImpl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static wow.commons.repository.impl.parser.item.ItemBaseExcelSheetNames.*;

/**
 * User: POlszewski
 * Date: 2022-10-30
 */
@AllArgsConstructor
public class ItemBaseExcelParser extends ExcelParser {
	private final String xlsFilePath;
	private final ItemRepositoryImpl itemRepository;
	private final PveRepository pveRepository;
	private final SpellRepository spellRepository;

	private final Map<String, List<Item>> setPiecesByName = new HashMap<>();

	@Override
	protected InputStream getExcelInputStream() {
		return fromResourcePath(xlsFilePath);
	}

	@Override
	protected Stream<ExcelSheetParser> getSheetParsers() {
		return Stream.of(
				new TradedItemSheetParser(TRADE, pveRepository, spellRepository, itemRepository),
				new ItemSheetParser(ITEM, pveRepository, spellRepository, itemRepository, this),
				new EnchantSheetParser(ENCHANT, pveRepository, spellRepository, itemRepository),
				new GemSheetParser(GEM, pveRepository, spellRepository, itemRepository),
				new ItemSetSheetParser(SET, pveRepository, spellRepository, itemRepository, this)
		);
	}

	void addItemSetPiece(String itemSetName, Item item) {
		String key = getSetKey(itemSetName, item.getTimeRestriction().getUniqueVersion());
		setPiecesByName.computeIfAbsent(key, x -> new ArrayList<>()).add(item);
	}

	List<Item> getPieces(String name, GameVersionId version) {
		return setPiecesByName.getOrDefault(getSetKey(name, version), List.of());
	}

	private static String getSetKey(String setName, GameVersionId version) {
		return setName + "#" + version;
	}
}
