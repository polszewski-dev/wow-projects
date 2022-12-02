package wow.scraper;

import lombok.extern.slf4j.Slf4j;
import wow.scraper.excel.ItemBaseExcelBuilder;
import wow.scraper.model.JsonItemDetailsAndTooltip;
import wow.scraper.model.WowheadItemCategory;
import wow.scraper.parsers.WowheadSourceParser;
import wow.scraper.parsers.tooltip.AbstractTooltipParser;
import wow.scraper.parsers.tooltip.GemTooltipParser;
import wow.scraper.parsers.tooltip.ItemTooltipParser;
import wow.scraper.parsers.tooltip.TradedItemParser;

import java.io.IOException;
import java.util.List;

/**
 * User: POlszewski
 * Date: 2022-10-29
 */
@Slf4j
public class ItemBaseGeneratorMain extends ScraperTool {
	public static void main(String[] args) throws IOException {
		new ItemBaseGeneratorMain().run();
	}

	@Override
	protected void run() throws IOException {
		WowheadSourceParser.configure(getScraperConfig());

		ItemBaseExcelBuilder builder = new ItemBaseExcelBuilder();
		builder.start();

		builder.addTradedItemHeader();
		addTokens(builder);
		addItemsStartingQuest(builder);

		builder.addItemHeader();
		addEquipment(builder);

		builder.addGemHeader();
		addGems(builder);

		String itemFilePath = "scraper/item_base.xls";

		builder.finish(itemFilePath);

		log.info("Saved to {}", itemFilePath);

		AbstractTooltipParser.reportUnmatchedLines(log);
	}

	private void addEquipment(ItemBaseExcelBuilder builder) throws IOException {
		for (WowheadItemCategory category : WowheadItemCategory.equipment()) {
			export(
					category,
					itemDetailsAndTooltip -> new ItemTooltipParser(itemDetailsAndTooltip, getGameVersion(), getStatPatternRepository()),
					builder::add
			);
		}
	}

	private void addGems(ItemBaseExcelBuilder builder) throws IOException {
		export(
				WowheadItemCategory.GEMS,
				itemDetailsAndTooltip -> new GemTooltipParser(itemDetailsAndTooltip, getGameVersion(), getStatPatternRepository()),
				builder::add
		);
	}

	private void addItemsStartingQuest(ItemBaseExcelBuilder builder) throws IOException {
		export(
				WowheadItemCategory.QUEST,
				itemDetailsAndTooltip -> new TradedItemParser(itemDetailsAndTooltip, getGameVersion(), getStatPatternRepository()),
				builder::add
		);
	}

	private void addTokens(ItemBaseExcelBuilder builder) throws IOException {
		export(
				WowheadItemCategory.TOKENS,
				itemDetailsAndTooltip -> new TradedItemParser(itemDetailsAndTooltip, getGameVersion(), getStatPatternRepository()),
				builder::add
		);
	}

	private interface ParserCreator<T extends AbstractTooltipParser> {
		T create(JsonItemDetailsAndTooltip itemDetailsAndTooltip);
	}

	private interface ParsedDataExporter<T extends AbstractTooltipParser> {
		void export(T parser);
	}

	private <T extends AbstractTooltipParser> void export(WowheadItemCategory category, ParserCreator<T> parserCreator, ParsedDataExporter<T> parsedDataExporter) throws IOException {
		List<Integer> itemIds = getItemDetailRepository().getItemIds(getGameVersion(), category);

		for (Integer itemId : itemIds) {
			var itemDetailsAndTooltip = getItemDetailRepository().getDetail(getGameVersion(), category, itemId).orElseThrow();
			var parser = parserCreator.create(itemDetailsAndTooltip);

			parser.parse();
			parsedDataExporter.export(parser);

			log.info("Added {} {}", parser.getItemId(), parser.getName());
		}
	}
}
