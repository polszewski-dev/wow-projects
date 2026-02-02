package wow.commons.repository.impl.parser.item;

import wow.commons.model.config.TimeRestriction;
import wow.commons.model.item.ItemSet;
import wow.commons.model.item.ItemSetBonus;
import wow.commons.model.item.impl.ItemSetImpl;
import wow.commons.model.profession.ProfessionId;
import wow.commons.repository.spell.SpellRepository;

import java.util.ArrayList;
import java.util.List;

import static wow.commons.model.effect.EffectSource.ItemSetSource;
import static wow.commons.repository.impl.parser.item.ItemBaseExcelColumnNames.*;

/**
 * User: POlszewski
 * Date: 2022-11-22
 */
public class ItemSetSheetParser extends AbstractItemSheetParser {
	private final ExcelColumn colPieces = column(ITEM_SET_PIECES);
	private final ExcelColumn colIBonusReqProfession = column(ITEM_SET_BONUS_REQ_PROFESSION);

	private final ItemSetExcelParser parser;

	public ItemSetSheetParser(String sheetName, SourceParserFactory sourceParserFactory, SpellRepository spellRepository, ItemSetExcelParser parser) {
		super(sheetName, sourceParserFactory, spellRepository);
		this.parser = parser;
	}

	@Override
	protected void readSingleRow() {
		var itemSet = getItemSet();
		parser.addItemSet(itemSet);
	}

	private ItemSet getItemSet() {
		var description = getDescription();
		var timeRestriction = getTimeRestriction();
		var characterRestriction = getRestriction();
		var pieces = colPieces.getList(x -> x);
		var requiredProfession = colIBonusReqProfession.getEnum(ProfessionId::parse, null);

		var itemSet = new ItemSetImpl(description, timeRestriction, characterRestriction, pieces, requiredProfession);
		var itemSetBonuses = getItemSetBonuses(itemSet, timeRestriction);

		itemSet.setItemSetBonuses(itemSetBonuses);

		return itemSet;
	}

	private List<ItemSetBonus> getItemSetBonuses(ItemSet itemSet, TimeRestriction timeRestriction) {
		List<ItemSetBonus> itemSetBonuses = new ArrayList<>();

		for (int bonusIdx = 1; bonusIdx <= ITEM_SET_MAX_BONUSES; ++bonusIdx) {
			ItemSetBonus itemSetBonus = getItemSetBonus(bonusIdx, itemSet, timeRestriction);
			if (itemSetBonus != null) {
				itemSetBonuses.add(itemSetBonus);
			}
		}

		return itemSetBonuses;
	}

	private ItemSetBonus getItemSetBonus(int bonusIdx, ItemSet itemSet, TimeRestriction timeRestriction) {
		int numPieces = column(itemSetBonusNumPieces(bonusIdx)).getInteger(0);

		if (numPieces == 0) {
			return null;
		}

		var prefix = itemSetBonusStatPrefix(bonusIdx);
		var bonusEffect = readItemEffect(prefix, 1, timeRestriction, new ItemSetSource(itemSet, numPieces));

		return new ItemSetBonus(numPieces, bonusEffect);
	}
}
