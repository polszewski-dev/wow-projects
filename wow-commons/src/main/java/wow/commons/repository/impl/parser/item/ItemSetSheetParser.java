package wow.commons.repository.impl.parser.item;

import wow.commons.model.config.TimeRestriction;
import wow.commons.model.item.Item;
import wow.commons.model.item.ItemSet;
import wow.commons.model.item.ItemSetBonus;
import wow.commons.model.item.ItemSetSource;
import wow.commons.model.item.impl.ItemImpl;
import wow.commons.model.item.impl.ItemSetImpl;
import wow.commons.model.profession.ProfessionId;
import wow.commons.repository.PveRepository;
import wow.commons.repository.SpellRepository;
import wow.commons.repository.impl.ItemRepositoryImpl;

import java.util.ArrayList;
import java.util.List;

import static wow.commons.repository.impl.parser.item.ItemBaseExcelColumnNames.*;

/**
 * User: POlszewski
 * Date: 2022-11-22
 */
public class ItemSetSheetParser extends AbstractItemSheetParser {
	private final ExcelColumn colIBonusReqProfession = column(ITEM_SET_BONUS_REQ_PROFESSION);

	private final ItemBaseExcelParser parser;

	public ItemSetSheetParser(String sheetName, PveRepository pveRepository, SpellRepository spellRepository, ItemRepositoryImpl itemRepository, ItemBaseExcelParser parser) {
		super(sheetName, pveRepository, spellRepository, itemRepository);
		this.parser = parser;
	}

	@Override
	protected void readSingleRow() {
		ItemSet itemSet = getItemSet();
		itemRepository.addItemSet(itemSet);
	}

	private ItemSet getItemSet() {
		var name = colName.getString();

		var timeRestriction = getTimeRestriction();
		var characterRestriction = getRestriction();
		var pieces = parser.getPieces(name, timeRestriction.getUniqueVersion());
		var requiredProfession = colIBonusReqProfession.getEnum(ProfessionId::parse, null);

		var itemSet = new ItemSetImpl(name, timeRestriction, characterRestriction, pieces, requiredProfession);
		var itemSetBonuses = getItemSetBonuses(itemSet, timeRestriction);

		itemSet.setItemSetBonuses(itemSetBonuses);

		for (Item item : itemSet.getPieces()) {
			((ItemImpl)item).setItemSet(itemSet);
		}

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
