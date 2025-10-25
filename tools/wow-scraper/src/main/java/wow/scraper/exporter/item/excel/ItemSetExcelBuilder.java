package wow.scraper.exporter.item.excel;

import lombok.Getter;
import wow.scraper.config.ScraperConfig;
import wow.scraper.config.ScraperDatafixes;
import wow.scraper.exporter.excel.WowExcelBuilder;

import static wow.commons.repository.impl.parser.item.ItemBaseExcelSheetNames.SET;

/**
 * User: POlszewski
 * Date: 2022-10-30
 */
@Getter
public class ItemSetExcelBuilder extends WowExcelBuilder {
	private final SavedSets savedSets = new SavedSets();

	private final ItemSetSheetWriter itemSetSheetWriter;

	public ItemSetExcelBuilder(ScraperConfig config, ScraperDatafixes datafixes) {
		super(config, datafixes);
		this.itemSetSheetWriter = new ItemSetSheetWriter(this);
	}

	public void addItemSetHeader() {
		writeHeader(SET, itemSetSheetWriter, 1, 1);
	}

	public void add(SavedSets.SetInfo setInfo) {
		writeRow(setInfo, itemSetSheetWriter);
	}
}
