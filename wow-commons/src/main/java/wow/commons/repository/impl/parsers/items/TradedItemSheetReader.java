package wow.commons.repository.impl.parsers.items;

import wow.commons.model.categorization.Binding;
import wow.commons.model.categorization.ItemRarity;
import wow.commons.model.categorization.ItemType;
import wow.commons.model.config.Description;
import wow.commons.model.config.Restriction;
import wow.commons.model.item.BasicItemInfo;
import wow.commons.model.item.TradedItem;
import wow.commons.model.pve.Phase;
import wow.commons.model.sources.Source;
import wow.commons.model.unit.CharacterClass;
import wow.commons.repository.PVERepository;
import wow.commons.repository.impl.ItemDataRepositoryImpl;
import wow.commons.util.ExcelSheetReader;
import wow.commons.util.SourceParser;

import java.util.Set;

import static wow.commons.repository.impl.parsers.items.ItemBaseExcelColumnNames.*;

/**
 * User: POlszewski
 * Date: 2022-11-22
 */
public class TradedItemSheetReader extends ExcelSheetReader {
	private final ExcelColumn colItemId = column(TRADE_ITEM_ID);
	private final ExcelColumn colItemName = column(TRADE_ITEM_NAME);
	private final ExcelColumn colItemType = column(TRADE_ITEM_TYPE);
	private final ExcelColumn colItemRarity = column(TRADE_ITEM_RARITY);
	private final ExcelColumn colItemItemLevel = column(TRADE_ITEM_ITEM_LEVEL);
	private final ExcelColumn colItemReqLevel = column(TRADE_ITEM_REQ_LEVEL);
	private final ExcelColumn colItemBinding = column(TRADE_ITEM_BINDING);
	private final ExcelColumn colItemPhase = column(TRADE_ITEM_PHASE);
	private final ExcelColumn colItemClassRestriction = column(TRADE_ITEM_CLASS_RESTRICTION);
	private final ExcelColumn colItemSource = column(TRADE_ITEM_SOURCE);

	private final PVERepository pveRepository;
	private final ItemDataRepositoryImpl itemDataRepository;

	public TradedItemSheetReader(String sheetName, PVERepository pveRepository, ItemDataRepositoryImpl itemDataRepository) {
		super(sheetName);
		this.pveRepository = pveRepository;
		this.itemDataRepository = itemDataRepository;
	}

	@Override
	protected ExcelColumn getColumnIndicatingOptionalRow() {
		return colItemName;
	}

	@Override
	protected void readSingleRow() {
		TradedItem tradedItem = getTradedItem();
		itemDataRepository.addTradedItem(tradedItem);
	}

	private TradedItem getTradedItem() {
		var id = colItemId.getInteger();
		var name = colItemName.getString();
		var type = colItemType.getEnum(ItemType::valueOf);
		var rarity = colItemRarity.getEnum(ItemRarity::valueOf);
		var itemLevel = colItemItemLevel.getInteger(0);
		var requiredLevel = colItemReqLevel.getInteger(0);
		var binding = colItemBinding.getEnum(Binding::valueOf, null);
		var phase = colItemPhase.getEnum(Phase::valueOf);
		var classRestriction = colItemClassRestriction.getList(CharacterClass::valueOf);
		var source = colItemSource.getString();

		Description description = new Description(name, null, null);
		Restriction restriction = Restriction.builder()
				.requiredLevel(requiredLevel)
				.phase(phase)
				.classRestriction(classRestriction)
				.build();
		Set<Source> sources = new SourceParser(pveRepository, itemDataRepository).parse(source);
		BasicItemInfo basicItemInfo = new BasicItemInfo(rarity, binding, false, itemLevel, sources);

		return new TradedItem(id, description, restriction, type, basicItemInfo);
	}
}
