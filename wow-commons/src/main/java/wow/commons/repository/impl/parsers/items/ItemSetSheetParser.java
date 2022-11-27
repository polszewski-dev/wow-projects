package wow.commons.repository.impl.parsers.items;

import wow.commons.model.attributes.Attributes;
import wow.commons.model.config.Restriction;
import wow.commons.model.item.Item;
import wow.commons.model.item.ItemSet;
import wow.commons.model.item.ItemSetBonus;
import wow.commons.repository.impl.ItemDataRepositoryImpl;
import wow.commons.repository.impl.parsers.excel.WowExcelSheetParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static wow.commons.repository.impl.parsers.items.ItemBaseExcelColumnNames.*;

/**
 * User: POlszewski
 * Date: 2022-11-22
 */
public class ItemSetSheetParser extends WowExcelSheetParser {
	private final ExcelColumn colName = column(ITEM_SET_NAME);

	private final ItemDataRepositoryImpl itemDataRepository;
	private final Map<String, List<Item>> setPiecesByName;

	public ItemSetSheetParser(String sheetName, ItemDataRepositoryImpl itemDataRepository, Map<String, List<Item>> setPiecesByName) {
		super(sheetName);
		this.itemDataRepository = itemDataRepository;
		this.setPiecesByName = setPiecesByName;
	}

	@Override
	protected ExcelColumn getColumnIndicatingOptionalRow() {
		return colName;
	}

	@Override
	protected void readSingleRow() {
		ItemSet itemSet = getItemSet();
		itemDataRepository.addItemSet(itemSet);
	}

	private ItemSet getItemSet() {
		var name = colName.getString();
		var itemSetBonuses = getItemSetBonuses();

		Restriction restriction = getRestriction();
		ItemSet itemSet = new ItemSet(name, null, restriction, itemSetBonuses, setPiecesByName.getOrDefault(name, List.of()));

		for (Item item : itemSet.getPieces()) {
			item.setItemSet(itemSet);
		}

		return itemSet;
	}

	private List<ItemSetBonus> getItemSetBonuses() {
		List<ItemSetBonus> itemSetBonuses = new ArrayList<>();

		for (int bonusIdx = 1; bonusIdx <= ITEM_SET_MAX_BONUSES; ++bonusIdx) {
			ItemSetBonus itemSetBonus = getItemSetBonus(bonusIdx);
			if (itemSetBonus != null) {
				itemSetBonuses.add(itemSetBonus);
			}
		}

		return itemSetBonuses;
	}

	private ItemSetBonus getItemSetBonus(int bonusIdx) {
		int numPieces = column(itemSetBonusNumPieces(bonusIdx)).getInteger(0);

		if (numPieces == 0) {
			return null;
		}

		String description = column(itemSetBonusDescription(bonusIdx)).getString();
		Attributes attributes = readAttributes(itemSetBonusStatPrefix(bonusIdx), ITEM_SET_BONUS_MAX_STATS);

		return new ItemSetBonus(numPieces, description, attributes);
	}
}
