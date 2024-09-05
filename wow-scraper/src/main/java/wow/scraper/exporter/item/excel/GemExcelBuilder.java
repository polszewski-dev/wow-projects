package wow.scraper.exporter.item.excel;

import lombok.Getter;
import wow.scraper.config.ScraperConfig;
import wow.scraper.exporter.excel.WowExcelBuilder;
import wow.scraper.parser.tooltip.GemTooltipParser;

import static wow.commons.repository.impl.parser.item.ItemBaseExcelSheetNames.GEM;

/**
 * User: POlszewski
 * Date: 2022-10-30
 */
@Getter
public class GemExcelBuilder extends WowExcelBuilder {
	private final GemSheetWriter gemSheetWriter;

	public GemExcelBuilder(ScraperConfig config) {
		super(config);
		this.gemSheetWriter = new GemSheetWriter(this);
	}

	public void addGemHeader() {
		writeHeader(GEM, gemSheetWriter, 2, 1);
	}

	public void add(GemTooltipParser parser) {
		if (isToBeIgnored(parser.getItemId())) {
			return;
		}
		writeRow(parser, gemSheetWriter);
	}

	private boolean isToBeIgnored(int itemId) {
		return config.getIgnoredItemIds().contains(itemId);
	}
}
