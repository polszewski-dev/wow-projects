package wow.commons.repository.impl.parsers.items;

import wow.commons.model.attributes.Attributes;
import wow.commons.model.attributes.condition.AttributeCondition;
import wow.commons.model.item.Item;
import wow.commons.model.item.ItemSet;
import wow.commons.model.item.ItemSetBonus;
import wow.commons.model.item.ItemSetSource;
import wow.commons.model.item.impl.ItemImpl;
import wow.commons.model.item.impl.ItemSetImpl;
import wow.commons.model.professions.ProfessionId;
import wow.commons.repository.impl.ItemRepositoryImpl;
import wow.commons.repository.impl.parsers.excel.WowExcelSheetParser;
import wow.commons.util.AttributesBuilder;

import java.util.ArrayList;
import java.util.List;

import static wow.commons.repository.impl.parsers.items.ItemBaseExcelColumnNames.*;

/**
 * User: POlszewski
 * Date: 2022-11-22
 */
public class ItemSetSheetParser extends WowExcelSheetParser {
	private final ExcelColumn colName = column(ITEM_SET_NAME);
	private final ExcelColumn colIBonusReqProfession = column(ITEM_SET_BONUS_REQ_PROFESSION);

	private final ItemRepositoryImpl itemRepository;
	private final ItemBaseExcelParser parser;

	public ItemSetSheetParser(String sheetName, ItemRepositoryImpl itemRepository, ItemBaseExcelParser parser) {
		super(sheetName);
		this.itemRepository = itemRepository;
		this.parser = parser;
	}

	@Override
	protected ExcelColumn getColumnIndicatingOptionalRow() {
		return colName;
	}

	@Override
	protected void readSingleRow() {
		ItemSet itemSet = getItemSet();
		itemRepository.addItemSet(itemSet);
	}

	private ItemSet getItemSet() {
		var name = colName.getString();

		var timeRestriction = getTimeRestriction();
		var characterRestriction = getRestriction();
		var pieces = parser.getPieces(name, timeRestriction.getUniqueVersion());

		var itemSet = new ItemSetImpl(name, null, timeRestriction, characterRestriction, pieces);
		var itemSetBonuses = getItemSetBonuses(itemSet);

		itemSet.setItemSetBonuses(itemSetBonuses);

		for (Item item : itemSet.getPieces()) {
			((ItemImpl)item).setItemSet(itemSet);
		}

		return itemSet;
	}

	private List<ItemSetBonus> getItemSetBonuses(ItemSet itemSet) {
		List<ItemSetBonus> itemSetBonuses = new ArrayList<>();

		for (int bonusIdx = 1; bonusIdx <= ITEM_SET_MAX_BONUSES; ++bonusIdx) {
			ItemSetBonus itemSetBonus = getItemSetBonus(bonusIdx, itemSet);
			if (itemSetBonus != null) {
				itemSetBonuses.add(itemSetBonus);
			}
		}

		return itemSetBonuses;
	}

	private ItemSetBonus getItemSetBonus(int bonusIdx, ItemSet itemSet) {
		int numPieces = column(itemSetBonusNumPieces(bonusIdx)).getInteger(0);

		if (numPieces == 0) {
			return null;
		}

		String description = column(itemSetBonusDescription(bonusIdx)).getString();
		Attributes attributes = readAttributes(itemSetBonusStatPrefix(bonusIdx));
		AttributeCondition professionCondition = AttributeCondition.of(colIBonusReqProfession.getEnum(ProfessionId::parse, null));

		attributes = AttributesBuilder.attachCondition(attributes, professionCondition);
		attributes = AttributesBuilder.attachSource(attributes, new ItemSetSource(itemSet, numPieces));

		return new ItemSetBonus(numPieces, description, attributes);
	}
}
