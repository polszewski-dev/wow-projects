package wow.commons.repository.impl.parser.item;

import lombok.AllArgsConstructor;
import polszewski.excel.reader.templates.ExcelParser;
import polszewski.excel.reader.templates.ExcelSheetParser;
import wow.commons.model.item.Item;
import wow.commons.model.pve.GameVersionId;
import wow.commons.repository.impl.item.ItemRepositoryImpl;
import wow.commons.repository.spell.SpellRepository;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static wow.commons.repository.impl.parser.item.ItemBaseExcelSheetNames.ITEM;
import static wow.commons.repository.impl.parser.item.ItemBaseExcelSheetNames.SET;

/**
 * User: POlszewski
 * Date: 2022-10-30
 */
@AllArgsConstructor
public class ItemExcelParser extends ExcelParser {
	private final String xlsFilePath;
	private final SourceParserFactory sourceParserFactory;
	private final ItemRepositoryImpl itemRepository;
	private final SpellRepository spellRepository;

	private final Map<String, List<Item>> setPiecesByName = new HashMap<>();

	@Override
	protected InputStream getExcelInputStream() {
		return fromResourcePath(xlsFilePath);
	}

	@Override
	protected Stream<ExcelSheetParser> getSheetParsers() {
		return Stream.of(
				new ItemSheetParser(ITEM, sourceParserFactory, spellRepository, itemRepository, this),
				new ItemSetSheetParser(SET, sourceParserFactory, spellRepository, itemRepository, this)
		);
	}

	void addItemSetPiece(String itemSetName, Item item) {
		String key = getSetKey(itemSetName, item.getTimeRestriction().getGameVersionId());
		setPiecesByName.computeIfAbsent(key, x -> new ArrayList<>()).add(item);
	}

	List<Item> getPieces(String name, GameVersionId version) {
		return setPiecesByName.getOrDefault(getSetKey(name, version), List.of());
	}

	private static String getSetKey(String setName, GameVersionId version) {
		return setName + "#" + version;
	}
}
