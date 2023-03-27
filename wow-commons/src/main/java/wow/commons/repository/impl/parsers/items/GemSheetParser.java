package wow.commons.repository.impl.parsers.items;

import wow.commons.model.item.Gem;
import wow.commons.model.item.GemColor;
import wow.commons.model.item.MetaEnabler;
import wow.commons.model.item.impl.GemImpl;
import wow.commons.repository.PveRepository;
import wow.commons.repository.impl.ItemRepositoryImpl;

import static wow.commons.repository.impl.parsers.items.ItemBaseExcelColumnNames.*;

/**
 * User: POlszewski
 * Date: 2022-11-22
 */
public class GemSheetParser extends AbstractItemSheetParser {
	private final ExcelColumn colColor = column(GEM_COLOR);
	private final ExcelColumn colMetaEnablers = column(GEM_META_ENABLERS);

	public GemSheetParser(String sheetName, PveRepository pveRepository, ItemRepositoryImpl itemRepository) {
		super(sheetName, pveRepository, itemRepository);
	}

	@Override
	protected void readSingleRow() {
		Gem gem = getGem();
		itemRepository.addGem(gem);
	}

	private Gem getGem() {
		var id = getId();
		var color = colColor.getEnum(GemColor::valueOf);
		var metaEnablers = colMetaEnablers.getList(MetaEnabler::valueOf);
		var stats = readAttributes(GEM_MAX_STATS);

		var description = getDescription();
		var timeRestriction = getTimeRestriction();
		var characterRestriction = getRestriction();
		var basicItemInfo = getBasicItemInfo();

		return new GemImpl(id, description, timeRestriction, characterRestriction, stats, basicItemInfo, color, metaEnablers);
	}
}
