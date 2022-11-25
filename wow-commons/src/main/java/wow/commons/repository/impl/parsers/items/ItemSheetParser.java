package wow.commons.repository.impl.parsers.items;

import wow.commons.model.attributes.Attributes;
import wow.commons.model.categorization.ItemCategory;
import wow.commons.model.categorization.ItemSubType;
import wow.commons.model.categorization.ItemType;
import wow.commons.model.config.Description;
import wow.commons.model.config.Restriction;
import wow.commons.model.item.BasicItemInfo;
import wow.commons.model.item.Item;
import wow.commons.model.item.ItemSocketSpecification;
import wow.commons.model.item.SocketType;
import wow.commons.repository.PVERepository;
import wow.commons.repository.impl.ItemDataRepositoryImpl;
import wow.commons.repository.impl.parsers.gems.SocketBonusParser;
import wow.commons.repository.impl.parsers.stats.StatParser;
import wow.commons.repository.impl.parsers.stats.StatPatternRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static wow.commons.repository.impl.parsers.items.ItemBaseExcelColumnNames.*;

/**
 * User: POlszewski
 * Date: 2022-11-22
 */
public class ItemSheetParser extends AbstractItemSheetParser {
	private final ExcelColumn colSocketTypes = column(ITEM_SOCKET_TYPES);
	private final ExcelColumn colSocketBonus = column(ITEM_SOCKET_BONUS);
	private final ExcelColumn colItemSet = column(ITEM_ITEM_SET);
	private final ExcelColumn colStat = column(ITEM_STAT);

	private static final int MAX_ITEM_STATS = 10;

	private final Map<String, List<Item>> setPiecesByName;
	
	public ItemSheetParser(String sheetName, PVERepository pveRepository, ItemDataRepositoryImpl itemDataRepository, Map<String, List<Item>> setPiecesByName) {
		super(sheetName, pveRepository, itemDataRepository);
		this.setPiecesByName = setPiecesByName;
	}

	@Override
	protected void readSingleRow() {
		Item item = getItem();
		itemDataRepository.addItem(item);
	}

	private Item getItem() {
		var id = getId();
		var socketTypes = colSocketTypes.getList(SocketType::valueOf);
		var socketBonus = colSocketBonus.getString(null);
		var itemSetName = colItemSet.getString(null);
		var stats = getStats();

		Description description = getDescription();
		Restriction restriction = getRestriction();
		BasicItemInfo basicItemInfo = getBasicItemInfo();
		ItemSocketSpecification socketSpecification = getSocketSpecification(socketTypes, socketBonus);

		Item item = new Item(id, description, restriction, stats, basicItemInfo, socketSpecification, null);

		validateItem(item);

		if (itemSetName != null) {
			setPiecesByName.computeIfAbsent(itemSetName, x -> new ArrayList<>()).add(item);
		}

		return item;
	}

	private Attributes getStats() {
		StatParser parser = StatPatternRepository.getInstance().getItemStatParser();

		for (int i = 1; i <= MAX_ITEM_STATS; ++i) {
			String line = colStat.multi(i).getString(null);
			if (line != null && !parser.tryParse(line)) {
				throw new IllegalArgumentException("Can't parse: " + line);
			}
		}

		return parser.getParsedStats();
	}

	private static ItemSocketSpecification getSocketSpecification(List<SocketType> socketTypes, String socketBonus) {
		if (socketTypes.isEmpty()) {
			if (socketBonus != null) {
				throw new IllegalArgumentException();
			}
			return ItemSocketSpecification.EMPTY;
		}

		if (socketBonus == null) {
			throw new IllegalArgumentException();
		}

		Attributes socketBonusAttributes = SocketBonusParser.tryParseSocketBonus(socketBonus);

		if (socketBonusAttributes == null) {
			throw new IllegalArgumentException("Invalid socket bonus: " + socketBonus);
		}

		return new ItemSocketSpecification(socketTypes, socketBonusAttributes);
	}

	private static void validateItem(Item item) {
		ItemType itemType = item.getItemType();
		ItemSubType itemSubType = item.getItemSubType();

		if (itemType == null) {
			return;// checked after all items are read
		}
		if (itemType.getCategory() == ItemCategory.ARMOR && itemSubType == null) {
			throw new IllegalArgumentException(item.getName());
		}
		if (itemType.getCategory() == ItemCategory.WEAPON && itemSubType == null) {
			throw new IllegalArgumentException(item.getName());
		}
		if (itemType.getCategory() == ItemCategory.PROJECTILE && itemSubType == null) {
			throw new IllegalArgumentException(item.getName());
		}
		if ((itemType.getCategory() != ItemCategory.ARMOR && itemType.getCategory() != ItemCategory.WEAPON && itemType.getCategory() != ItemCategory.PROJECTILE) && itemSubType != null) {
			throw new IllegalArgumentException(item.getName());
		}
	}
}
