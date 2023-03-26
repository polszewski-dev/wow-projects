package wow.commons.repository.impl.parsers.items;

import wow.commons.model.attributes.AttributeCondition;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.config.CharacterRestriction;
import wow.commons.model.config.TimeRestriction;
import wow.commons.model.item.Item;
import wow.commons.model.item.ItemSet;
import wow.commons.model.item.ItemSetBonus;
import wow.commons.model.professions.Profession;
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
		var itemSetBonuses = getItemSetBonuses();

		TimeRestriction timeRestriction = getTimeRestriction();
		CharacterRestriction characterRestriction = getRestriction();
		List<Item> pieces = parser.getPieces(name, timeRestriction.getUniqueVersion());

		ItemSet itemSet = new ItemSet(name, null, timeRestriction, characterRestriction, itemSetBonuses, pieces);

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
		AttributeCondition professionCondition = AttributeCondition.of(colIBonusReqProfession.getEnum(Profession::parse, null));

		attributes = AttributesBuilder.attachCondition(attributes, professionCondition);

		return new ItemSetBonus(numPieces, description, attributes);
	}
}
