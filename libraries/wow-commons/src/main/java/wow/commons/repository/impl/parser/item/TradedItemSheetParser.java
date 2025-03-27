package wow.commons.repository.impl.parser.item;

import wow.commons.model.item.TradedItem;
import wow.commons.model.item.impl.TradedItemImpl;
import wow.commons.repository.spell.SpellRepository;

/**
 * User: POlszewski
 * Date: 2022-11-22
 */
public class TradedItemSheetParser extends AbstractItemSheetParser {
	private final TradedItemExcelParser parser;

	public TradedItemSheetParser(String sheetName, SourceParserFactory sourceParserFactory, SpellRepository spellRepository, TradedItemExcelParser parser) {
		super(sheetName, sourceParserFactory, spellRepository);
		this.parser = parser;
	}

	@Override
	protected void readSingleRow() {
		var tradedItem = getTradedItem();
		parser.addTradedItem(tradedItem);
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
