package wow.commons.repository.impl.parsers.items;

import wow.commons.model.attributes.Attributes;
import wow.commons.model.config.Restriction;
import wow.commons.model.item.Item;
import wow.commons.model.item.ItemSet;
import wow.commons.model.item.ItemSetBonus;
import wow.commons.model.professions.Profession;
import wow.commons.repository.impl.ItemDataRepositoryImpl;
import wow.commons.repository.impl.parsers.excel.WowExcelSheetParser;
import wow.commons.repository.impl.parsers.stats.StatParser;
import wow.commons.repository.impl.parsers.stats.StatPatternRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static wow.commons.repository.impl.parsers.items.ItemBaseExcelColumnNames.*;

/**
 * User: POlszewski
 * Date: 2022-11-22
 */
public class ItemSetSheetParser extends WowExcelSheetParser {
	private final ExcelColumn colName = column(ITEM_SET_NAME);
	private final ExcelColumn colReqProf = column(ITEM_SET_REQ_PROF);
	private final ExcelColumn colReqProfLvl = column(ITEM_SET_REQ_PROF_LVL);

	private static final int MAX_ITEM_SET_BONUSES = 6;

	private final ItemDataRepositoryImpl itemDataRepository;
	private final Map<String, List<Item>> setPiecesByName;

	public ItemSetSheetParser(String sheetName, ItemDataRepositoryImpl itemDataRepository, Map<String, List<Item>> setPiecesByName) {
		super(sheetName);
		this.itemDataRepository = itemDataRepository;
		this.setPiecesByName = setPiecesByName;
	}

	@Override
	protected ExcelColumn getColumnIndicatingOptionalRow() {
		return colName;
	}

	@Override
	protected void readSingleRow() {
		ItemSet itemSet = getItemSet();
		itemDataRepository.addItemSet(itemSet);
	}

	private ItemSet getItemSet() {
		var name = colName.getString();
		var itemSetBonuses = getItemSetBonuses();
		var requiredProfession = colReqProf.getEnum(Profession::valueOf, null);
		var requiredProfessionLevel = colReqProfLvl.getInteger(0);

		Restriction restriction = Restriction.builder()
				.requiredProfession(requiredProfession)
				.requiredProfessionLevel(requiredProfessionLevel)
				.build();

		ItemSet itemSet = new ItemSet(name, null, restriction, itemSetBonuses, setPiecesByName.getOrDefault(name, List.of()));

		for (Item item : itemSet.getPieces()) {
			item.setItemSet(itemSet);
		}

		return itemSet;
	}

	private List<ItemSetBonus> getItemSetBonuses() {
		List<ItemSetBonus> itemSetBonuses = new ArrayList<>();

		for (int i = 1; i <= MAX_ITEM_SET_BONUSES; i++) {
			int numPieces = column("bonus" + i + "_p").getInteger(-1);
			String description = column("bonus" + i + "_d").getString(null);

			if (description != null) {
				ItemSetBonus itemSetBonus = getItemSetBonus(numPieces, description);
				itemSetBonuses.add(itemSetBonus);
			}
		}

		return itemSetBonuses;
	}

	private static ItemSetBonus getItemSetBonus(int numPieces, String description) {
		StatParser statParser = StatPatternRepository.getInstance().getItemStatParser();
		if (statParser.tryParse(description)) {
			Attributes bonusStats = statParser.getParsedStats();
			return new ItemSetBonus(numPieces, description, bonusStats);
		}
		throw new IllegalArgumentException("Missing bonus: " + description);
	}
}
