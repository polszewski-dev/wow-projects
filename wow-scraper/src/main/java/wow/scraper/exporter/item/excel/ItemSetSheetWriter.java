package wow.scraper.exporter.item.excel;

import wow.commons.model.item.ItemSetBonus;

import java.util.List;

import static wow.commons.repository.impl.parser.excel.CommonColumnNames.NAME;
import static wow.commons.repository.impl.parser.excel.CommonColumnNames.REQ_CLASS;
import static wow.commons.repository.impl.parser.item.ItemBaseExcelColumnNames.*;
import static wow.scraper.util.CommonAssertions.assertSizeNoLargerThan;

/**
 * User: POlszewski
 * Date: 2023-05-18
 */
public class ItemSetSheetWriter extends ItemBaseSheetWriter<SavedSets.SetInfo> {
	public ItemSetSheetWriter(ItemExcelBuilder builder) {
		super(builder);
	}

	@Override
	public void writeHeader() {
		setHeader(NAME, 30);
		writeTimeRestrictionHeader();
		setHeader(REQ_CLASS);
		setHeader(ITEM_SET_BONUS_REQ_PROFESSION);
		setHeader(ITEM_SET_BONUS_REQ_PROFESSION_LEVEL);

		for (int bonusIdx = 1; bonusIdx <= ITEM_SET_MAX_BONUSES; ++bonusIdx) {
			setHeader(itemSetBonusNumPieces(bonusIdx));
			writeEffectHeader(itemSetBonusStatPrefix(bonusIdx), 1);
		}
	}

	@Override
	public void writeRow(SavedSets.SetInfo setInfo) {
		setValue(setInfo.getItemSetName());
		writeTimeRestriction(setInfo.getTimeRestriction());
		setValue(setInfo.getItemSetRequiredClass());
		setValue(setInfo.getItemSetBonusRequiredProfession());
		setValue(setInfo.getItemSetBonusRequiredProfessionLevel());
		writeBonuses(setInfo.getItemSetBonuses());
	}

	private void writeBonuses(List<ItemSetBonus> bonuses) {
		assertSizeNoLargerThan("set bonuses", bonuses, ITEM_SET_MAX_BONUSES);

		for (int i = 0; i < ITEM_SET_MAX_BONUSES; ++i) {
			if (i < bonuses.size()) {
				var bonus = bonuses.get(i);
				setValue(bonus.numPieces());
				writeEffect(bonus.bonusEffect());
			} else {
				fillRemainingEmptyCols(3);
			}
		}
	}
}
