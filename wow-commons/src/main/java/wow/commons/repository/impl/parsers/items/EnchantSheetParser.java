package wow.commons.repository.impl.parsers.items;

import wow.commons.model.categorization.ItemRarity;
import wow.commons.model.categorization.ItemType;
import wow.commons.model.item.Enchant;
import wow.commons.model.item.impl.EnchantImpl;
import wow.commons.repository.impl.ItemRepositoryImpl;
import wow.commons.repository.impl.parsers.excel.WowExcelSheetParser;

/**
 * User: POlszewski
 * Date: 2022-11-22
 */
public class EnchantSheetParser extends WowExcelSheetParser {
	private final ExcelColumn colId = column("id");
	private final ExcelColumn colName = column("name");
	private final ExcelColumn colItemTypes = column("item_types");
	private final ExcelColumn colRarity = column("rarity");

	private final ItemRepositoryImpl itemRepository;

	public EnchantSheetParser(String sheetName, ItemRepositoryImpl itemRepository) {
		super(sheetName);
		this.itemRepository = itemRepository;
	}

	@Override
	protected ExcelColumn getColumnIndicatingOptionalRow() {
		return colName;
	}

	@Override
	protected void readSingleRow() {
		Enchant enchant = getEnchant();
		itemRepository.addEnchant(enchant);
	}

	private Enchant getEnchant() {
		var id = colId.getInteger();
		var itemTypes = colItemTypes.getList(x -> ItemType.parse(x.trim()));
		var rarity = colRarity.getEnum(ItemRarity::parse);

		var description = getDescription();
		var timeRestriction = getTimeRestriction();
		var characterRestriction = getRestriction();
		var attributes = readAttributes(2);

		return new EnchantImpl(id, description, timeRestriction, characterRestriction, attributes, itemTypes, rarity);
	}
}
