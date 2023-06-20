package wow.scraper.exporters.item;

import wow.commons.model.pve.GameVersionId;
import wow.scraper.model.JsonItemDetails;
import wow.scraper.parsers.tooltip.GemTooltipParser;

import java.io.IOException;

import static wow.scraper.model.WowheadItemCategory.GEMS;

/**
 * User: POlszewski
 * Date: 2023-05-19
 */
public class GemItemExporter extends AbstractItemExporter<GemTooltipParser> {
	@Override
	public void exportAll() throws IOException {
		builder.addGemHeader();
		export(GEMS);
	}

	@Override
	protected GemTooltipParser createParser(JsonItemDetails details, GameVersionId gameVersion) {
		return new GemTooltipParser(details, gameVersion, getStatPatternRepository());
	}

	@Override
	protected void exportParsedData(GemTooltipParser parser) {
		builder.add(parser);
	}
}
