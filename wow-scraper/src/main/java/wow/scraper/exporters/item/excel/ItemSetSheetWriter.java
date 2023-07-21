package wow.scraper.exporters.item.excel;

import static wow.commons.repository.impl.parsers.excel.CommonColumnNames.REQ_CLASS;
import static wow.commons.repository.impl.parsers.excel.CommonColumnNames.REQ_VERSION;
import static wow.commons.repository.impl.parsers.items.ItemBaseExcelColumnNames.*;

/**
 * User: POlszewski
 * Date: 2023-05-18
 */
public class ItemSetSheetWriter extends ItemBaseSheetWriter<SavedSets.SetInfo> {
	public ItemSetSheetWriter(ItemBaseExcelBuilder builder) {
		super(builder);
	}

	@Override
	public void writeHeader() {
		setHeader(ITEM_SET_NAME, 30);
		setHeader(REQ_VERSION);
		setHeader(REQ_CLASS);
		setHeader(ITEM_SET_BONUS_REQ_PROFESSION);
		setHeader(ITEM_SET_BONUS_REQ_PROFESSION_LEVEL);

		for (int bonusIdx = 1; bonusIdx <= ITEM_SET_MAX_BONUSES; ++bonusIdx) {
			setHeader(itemSetBonusNumPieces(bonusIdx));
			setHeader(itemSetBonusDescription(bonusIdx));
			writeAttributeHeader(itemSetBonusStatPrefix(bonusIdx), ITEM_SET_BONUS_MAX_STATS);
		}
	}

	@Override
	public void writeRow(SavedSets.SetInfo setInfo) {
		setValue(setInfo.getItemSetName());
		setValue(setInfo.getGameVersion());
		setValue(setInfo.getItemSetRequiredClass());
		setValue(setInfo.getItemSetBonusRequiredProfession());
		setValue(setInfo.getItemSetBonusRequiredProfessionLevel());

		setList(setInfo.getItemSetBonuses(), ITEM_SET_MAX_BONUSES, x -> {
			setValue(x.numPieces());
			setValue(x.description());
			writeAttributes(x.bonusStats(), ITEM_SET_BONUS_MAX_STATS);
		}, 2 + 2 * ITEM_SET_BONUS_MAX_STATS);
	}
}
