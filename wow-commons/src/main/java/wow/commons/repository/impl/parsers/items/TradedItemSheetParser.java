package wow.commons.repository.impl.parsers.items;

import wow.commons.model.config.CharacterRestriction;
import wow.commons.model.config.Description;
import wow.commons.model.config.TimeRestriction;
import wow.commons.model.item.BasicItemInfo;
import wow.commons.model.item.TradedItem;
import wow.commons.repository.PveRepository;
import wow.commons.repository.impl.ItemDataRepositoryImpl;

/**
 * User: POlszewski
 * Date: 2022-11-22
 */
public class TradedItemSheetParser extends AbstractItemSheetParser {
	public TradedItemSheetParser(String sheetName, PveRepository pveRepository, ItemDataRepositoryImpl itemDataRepository) {
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
		TimeRestriction timeRestriction = getTimeRestriction();
		CharacterRestriction characterRestriction = getRestriction();
		BasicItemInfo basicItemInfo = getBasicItemInfo();

		return new TradedItem(id, description, timeRestriction, characterRestriction, basicItemInfo);
	}
}
