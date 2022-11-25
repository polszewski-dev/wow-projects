package wow.commons.repository.impl.parsers.items;

import wow.commons.model.categorization.Binding;
import wow.commons.model.categorization.ItemRarity;
import wow.commons.model.categorization.ItemSubType;
import wow.commons.model.categorization.ItemType;
import wow.commons.model.item.BasicItemInfo;
import wow.commons.model.sources.Source;
import wow.commons.repository.PVERepository;
import wow.commons.repository.impl.ItemDataRepositoryImpl;
import wow.commons.repository.impl.parsers.excel.WowExcelSheetParser;
import wow.commons.util.SourceParser;

import java.util.Set;

import static wow.commons.repository.impl.parsers.items.ItemBaseExcelColumnNames.*;

/**
 * User: POlszewski
 * Date: 2022-11-24
 */
public abstract class AbstractItemSheetParser extends WowExcelSheetParser {
	protected final PVERepository pveRepository;
	protected final ItemDataRepositoryImpl itemDataRepository;

	protected AbstractItemSheetParser(String sheetName, PVERepository pveRepository, ItemDataRepositoryImpl itemDataRepository) {
		super(sheetName);
		this.pveRepository = pveRepository;
		this.itemDataRepository = itemDataRepository;
	}

	private final ExcelColumn colId = column(ID);

	protected int getId() {
		return colId.getInteger();
	}

	private final ExcelColumn colItemType = column(ITEM_TYPE);
	private final ExcelColumn colItemSubtype = column(ITEM_SUBTYPE);
	private final ExcelColumn colRarity = column(RARITY);
	private final ExcelColumn colBinding = column(BINDING);
	private final ExcelColumn colUnique = column(UNIQUE);
	private final ExcelColumn colItemLevel = column(ITEM_LEVEL);
	private final ExcelColumn colSource = column(SOURCE);

	protected BasicItemInfo getBasicItemInfo() {
		var itemType = colItemType.getEnum(ItemType::valueOf);
		var itemSubType = colItemSubtype.getEnum(ItemSubType::tryParse, null);
		var rarity = colRarity.getEnum(ItemRarity::valueOf);
		var binding = colBinding.getEnum(Binding::valueOf, Binding.BINDS_ON_EQUIP);
		var unique = colUnique.getBoolean();
		var itemLevel = colItemLevel.getInteger();
		var source = colSource.getString();
		Set<Source> sources = new SourceParser(pveRepository, itemDataRepository).parse(source);

		return new BasicItemInfo(itemType, itemSubType, rarity, binding, unique, itemLevel, sources);
	}
}
