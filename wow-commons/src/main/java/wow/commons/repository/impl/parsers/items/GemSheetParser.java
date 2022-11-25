package wow.commons.repository.impl.parsers.items;

import wow.commons.model.config.Description;
import wow.commons.model.config.Restriction;
import wow.commons.model.item.BasicItemInfo;
import wow.commons.model.item.Gem;
import wow.commons.model.item.GemColor;
import wow.commons.model.item.MetaEnabler;
import wow.commons.repository.PVERepository;
import wow.commons.repository.impl.ItemDataRepositoryImpl;
import wow.commons.repository.impl.parsers.gems.GemStatsParser;

import static wow.commons.repository.impl.parsers.items.ItemBaseExcelColumnNames.*;

/**
 * User: POlszewski
 * Date: 2022-11-22
 */
public class GemSheetParser extends AbstractItemSheetParser {
	private final ExcelColumn colColor = column(GEM_COLOR);
	private final ExcelColumn colStats = column(GEM_STATS);
	private final ExcelColumn colMetaEnablers = column(GEM_META_ENABLERS);

	public GemSheetParser(String sheetName, PVERepository pveRepository, ItemDataRepositoryImpl itemDataRepository) {
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
		var stats = GemStatsParser.tryParseStats(colStats.getString());

		Description description = getDescription();
		Restriction restriction = getRestriction();
		BasicItemInfo basicItemInfo = getBasicItemInfo();

		return new Gem(id, description, restriction, stats, basicItemInfo, color, metaEnablers);
	}
}
