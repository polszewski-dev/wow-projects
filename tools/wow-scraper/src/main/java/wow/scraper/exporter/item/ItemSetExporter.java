package wow.scraper.exporter.item;

import wow.scraper.exporter.ExcelExporter;
import wow.scraper.exporter.item.excel.ItemSetExcelBuilder;
import wow.scraper.exporter.item.excel.SavedSets;

/**
 * User: POlszewski
 * Date: 2023-05-19
 */
public class ItemSetExporter extends ExcelExporter<ItemSetExcelBuilder> {
	public static final SavedSets SAVED_SETS = new SavedSets();

	@Override
	protected void prepareData() {
		// data is already prepared by item exporter
	}

	@Override
	protected void exportPreparedData(ItemSetExcelBuilder builder) {
		builder.addItemSetHeader();
		SAVED_SETS.forEach(builder::add);
	}
}
