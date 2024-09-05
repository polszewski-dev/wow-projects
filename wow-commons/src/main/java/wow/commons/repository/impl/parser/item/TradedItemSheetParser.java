package wow.commons.repository.impl.parser.item;

import wow.commons.model.item.TradedItem;
import wow.commons.model.item.impl.TradedItemImpl;
import wow.commons.repository.SpellRepository;
import wow.commons.repository.impl.item.TradedItemRepositoryImpl;

/**
 * User: POlszewski
 * Date: 2022-11-22
 */
public class TradedItemSheetParser extends AbstractItemSheetParser {
	private final TradedItemRepositoryImpl tradedItemRepository;

	public TradedItemSheetParser(String sheetName, SourceParserFactory sourceParserFactory, SpellRepository spellRepository, TradedItemRepositoryImpl tradedItemRepository) {
		super(sheetName, sourceParserFactory, spellRepository);
		this.tradedItemRepository = tradedItemRepository;
	}

	@Override
	protected void readSingleRow() {
		TradedItem tradedItem = getTradedItem();
		tradedItemRepository.addTradedItem(tradedItem);
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
