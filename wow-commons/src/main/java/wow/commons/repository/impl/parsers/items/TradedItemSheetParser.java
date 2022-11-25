package wow.commons.repository.impl.parsers.items;

import wow.commons.model.config.Description;
import wow.commons.model.config.Restriction;
import wow.commons.model.item.BasicItemInfo;
import wow.commons.model.item.TradedItem;
import wow.commons.repository.PVERepository;
import wow.commons.repository.impl.ItemDataRepositoryImpl;

/**
 * User: POlszewski
 * Date: 2022-11-22
 */
public class TradedItemSheetParser extends AbstractItemSheetParser {
	public TradedItemSheetParser(String sheetName, PVERepository pveRepository, ItemDataRepositoryImpl itemDataRepository) {
		super(sheetName, pveRepository, itemDataRepository);
	}

	@Override
	protected void readSingleRow() {
		TradedItem tradedItem = getTradedItem();
		itemDataRepository.addTradedItem(tradedItem);
	}

	private TradedItem getTradedItem() {
		var id = getId();

		Description description = getDescription();
		Restriction restriction = getRestriction();
		BasicItemInfo basicItemInfo = getBasicItemInfo();

		return new TradedItem(id, description, restriction, basicItemInfo);
	}
}
