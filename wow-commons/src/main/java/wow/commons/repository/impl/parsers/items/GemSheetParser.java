package wow.commons.repository.impl.parsers.items;

import wow.commons.model.config.CharacterRestriction;
import wow.commons.model.config.Description;
import wow.commons.model.config.TimeRestriction;
import wow.commons.model.item.BasicItemInfo;
import wow.commons.model.item.Gem;
import wow.commons.model.item.GemColor;
import wow.commons.model.item.MetaEnabler;
import wow.commons.repository.PveRepository;
import wow.commons.repository.impl.ItemDataRepositoryImpl;

import static wow.commons.repository.impl.parsers.items.ItemBaseExcelColumnNames.*;

/**
 * User: POlszewski
 * Date: 2022-11-22
 */
public class GemSheetParser extends AbstractItemSheetParser {
	private final ExcelColumn colColor = column(GEM_COLOR);
	private final ExcelColumn colMetaEnablers = column(GEM_META_ENABLERS);

	public GemSheetParser(String sheetName, PveRepository pveRepository, ItemDataRepositoryImpl itemDataRepository) {
		super(sheetName, pveRepository, itemDataRepository);
	}

	@Override
	protected void readSingleRow() {
		Gem gem = getGem();
		itemDataRepository.addGem(gem);
	}

	private Gem getGem() {
		var id = getId();
		var color = colColor.getEnum(GemColor::valueOf);
		var metaEnablers = colMetaEnablers.getList(MetaEnabler::valueOf);
		var stats = readAttributes(GEM_MAX_STATS);

		Description description = getDescription();
		CharacterRestriction characterRestriction = getRestriction();
		TimeRestriction timeRestriction = getTimeRestriction();
		BasicItemInfo basicItemInfo = getBasicItemInfo();

		return new Gem(id, description, timeRestriction, characterRestriction, stats, basicItemInfo, color, metaEnablers);
	}
}
