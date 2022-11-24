package wow.commons.repository.impl.parsers.items;

import wow.commons.model.attributes.Attributes;
import wow.commons.model.categorization.ItemType;
import wow.commons.model.config.Description;
import wow.commons.model.config.Restriction;
import wow.commons.model.item.Enchant;
import wow.commons.model.professions.Profession;
import wow.commons.repository.impl.ItemDataRepositoryImpl;
import wow.commons.repository.impl.parsers.excel.WowExcelSheetParser;
import wow.commons.repository.impl.parsers.stats.PrimitiveAttributeSupplier;
import wow.commons.util.AttributesBuilder;

/**
 * User: POlszewski
 * Date: 2022-11-22
 */
public class EnchantSheetReader extends WowExcelSheetParser {
	private final ExcelColumn colId = column("id");
	private final ExcelColumn colName = column("name");
	private final ExcelColumn colItemTypes = column("item_types");
	private final ExcelColumn colReqProf = column("req_prof");
	private final ExcelColumn colReqProfLvl = column("req_prof_lvl");
	private final ExcelColumn colStat = column("stat");
	private final ExcelColumn colAmount = column("amount");

	private final ItemDataRepositoryImpl itemDataRepository;

	public EnchantSheetReader(String sheetName, ItemDataRepositoryImpl itemDataRepository) {
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
		var name = colName.getString();
		var requiredProfession = colReqProf.getEnum(Profession::parse, null);
		var requiredProfessionLevel = colReqProfLvl.getInteger(0);
		var itemTypes = colItemTypes.getList(x -> ItemType.parse(x.trim()));

		Description description = new Description(name, null, null);
		Restriction restriction = Restriction.builder()
				.requiredProfession(requiredProfession)
				.requiredProfessionLevel(requiredProfessionLevel)
				.build();
		Attributes attributes = readAttributes();

		return new Enchant(id, description, restriction, attributes, itemTypes);
	}

	private Attributes readAttributes() {
		AttributesBuilder builder = new AttributesBuilder();
		int maxAttributes = 2;

		for (int statNo = 1; statNo <= maxAttributes; ++statNo) {
			var attributeStr = colStat.multi(statNo).getString(null);
			if (attributeStr != null) {
				PrimitiveAttributeSupplier attributeParser = PrimitiveAttributeSupplier.fromString(attributeStr);
				int amount = colAmount.multi(statNo).getInteger();
				builder.addAttributeList(attributeParser.getAttributeList(amount));
			}
		}

		return builder.toAttributes();
	}
}
