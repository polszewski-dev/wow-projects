package wow.commons.repository.impl.parser.item;

import wow.commons.model.item.TradedItem;
import wow.commons.model.item.impl.TradedItemImpl;
import wow.commons.repository.PveRepository;
import wow.commons.repository.impl.ItemRepositoryImpl;

/**
 * User: POlszewski
 * Date: 2022-11-22
 */
public class TradedItemSheetParser extends AbstractItemSheetParser {
	public TradedItemSheetParser(String sheetName, PveRepository pveRepository, ItemRepositoryImpl itemRepository) {
		super(sheetName, pveRepository, itemRepository);
	}

	@Override
	protected void readSingleRow() {
		TradedItem tradedItem = getTradedItem();
		itemRepository.addTradedItem(tradedItem);
	}

	private TradedItem getTradedItem() {
		var id = getId();

		var description = getDescription();
		var timeRestriction = getTimeRestriction();
		var characterRestriction = getRestriction();
		var basicItemInfo = getBasicItemInfo();

		return new TradedItemImpl(id, description, timeRestriction, characterRestriction, basicItemInfo);
	}
}
