package wow.commons.repository.impl.parsers.items;

import wow.commons.model.attributes.Attributes;
import wow.commons.model.categorization.ItemType;
import wow.commons.model.config.Description;
import wow.commons.model.config.Restriction;
import wow.commons.model.item.Enchant;
import wow.commons.repository.impl.ItemDataRepositoryImpl;
import wow.commons.repository.impl.parsers.excel.WowExcelSheetParser;

/**
 * User: POlszewski
 * Date: 2022-11-22
 */
public class EnchantSheetParser extends WowExcelSheetParser {
	private final ExcelColumn colId = column("id");
	private final ExcelColumn colName = column("name");
	private final ExcelColumn colItemTypes = column("item_types");

	private final ItemDataRepositoryImpl itemDataRepository;

	public EnchantSheetParser(String sheetName, ItemDataRepositoryImpl itemDataRepository) {
		super(sheetName);
		this.itemDataRepository = itemDataRepository;
	}

	@Override
	protected ExcelColumn getColumnIndicatingOptionalRow() {
		return colName;
	}

	@Override
	protected void readSingleRow() {
		Enchant enchant = getEnchant();
		itemDataRepository.addEnchant(enchant);
	}

	private Enchant getEnchant() {
		var id = colId.getInteger();
		var itemTypes = colItemTypes.getList(x -> ItemType.parse(x.trim()));

		Description description = getDescription();
		Restriction restriction = getRestriction();
		Attributes attributes = readAttributes(2);

		return new Enchant(id, description, restriction, attributes, itemTypes);
	}
}
