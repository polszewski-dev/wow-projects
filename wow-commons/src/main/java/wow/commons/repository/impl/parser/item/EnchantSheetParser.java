package wow.commons.repository.impl.parser.item;

import wow.commons.model.categorization.ItemRarity;
import wow.commons.model.categorization.ItemSubType;
import wow.commons.model.categorization.ItemType;
import wow.commons.model.item.Enchant;
import wow.commons.model.item.impl.EnchantImpl;
import wow.commons.repository.impl.ItemRepositoryImpl;
import wow.commons.repository.impl.parser.excel.WowExcelSheetParser;

/**
 * User: POlszewski
 * Date: 2022-11-22
 */
public class EnchantSheetParser extends WowExcelSheetParser {
	private final ExcelColumn colId = column("id");
	private final ExcelColumn colName = column("name");
	private final ExcelColumn colItemTypes = column("item_types");
	private final ExcelColumn colItemSubTypes = column("item_subtypes");
	private final ExcelColumn colRarity = column("rarity");
	private final ExcelColumn colEnchantId = column("enchant_id");

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
		var itemTypes = colItemTypes.getList(ItemType::valueOf);
		var itemSubTypes = colItemSubTypes.getList(ItemSubType::valueOf);
		var rarity = colRarity.getEnum(ItemRarity::parse);
		var enchantId = colEnchantId.getInteger();

		var description = getDescription();
		var timeRestriction = getTimeRestriction();
		var characterRestriction = getRestriction();
		var attributes = readAttributes();
		var pveRoles = getPveRoles();

		var enchant = new EnchantImpl(
				id, description, timeRestriction, characterRestriction, itemTypes, itemSubTypes, rarity, pveRoles, enchantId
		);

		enchant.setAttributes(attributes);
		return enchant;
	}
}
