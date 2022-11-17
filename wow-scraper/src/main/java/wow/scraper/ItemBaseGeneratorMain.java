package wow.scraper;

import lombok.extern.slf4j.Slf4j;
import wow.scraper.excel.ItemBaseExcelBuilder;
import wow.scraper.model.JsonItemDetailsAndTooltip;
import wow.scraper.model.WowheadItemCategory;
import wow.scraper.parsers.*;

import java.io.IOException;
import java.util.List;

/**
 * User: POlszewski
 * Date: 2022-10-29
 */
@Slf4j
public class ItemBaseGeneratorMain extends ScraperTool {
	public static void main(String[] args) throws Exception {
		new ItemBaseGeneratorMain().run();
	}

	@Override
	protected void run() throws Exception {
		ItemBaseExcelBuilder builder = new ItemBaseExcelBuilder();
		builder.start();

		builder.addItemHeader();
		addEquipment(builder);

		builder.addGemHeader();
		addGems(builder);

		builder.addItemstartingQuestHeader();
		addItemsStartingQuest(builder);

		builder.addTokenHeader();
		addTokens(builder);

		String itemFilePath = "scraper/item_base.xls";

		builder.finish(itemFilePath);

		log.info("Saved to {}", itemFilePath);

		AbstractTooltipParser.reportUnmatchedLines(log);
	}

	private void addEquipment(ItemBaseExcelBuilder builder) throws IOException {
		for (WowheadItemCategory category : WowheadItemCategory.equipment()) {
			export(
					category,
					(itemId, tooltip) -> new ItemTooltipParser(itemId, tooltip, getGameVersion()),
					builder::add
			);
		}
	}

	private void addGems(ItemBaseExcelBuilder builder) throws IOException {
		export(
				WowheadItemCategory.GEMS,
				(itemId, tooltip) -> new GemTooltipParser(itemId, tooltip, getGameVersion()),
				builder::add
		);
	}

	private void addItemsStartingQuest(ItemBaseExcelBuilder builder) throws IOException {
		export(
				WowheadItemCategory.QUEST,
				(itemId, tooltip) -> new ItemStartingQuestTooltipParser(itemId, tooltip, getGameVersion()),
				builder::add
		);
	}

	private void addTokens(ItemBaseExcelBuilder builder) throws IOException {
		export(
				WowheadItemCategory.TOKENS,
				(itemId, tooltip) -> new TokenTooltipParser(itemId, tooltip, getGameVersion()),
				builder::add
		);
	}

	private interface ParserCreator<T extends AbstractTooltipParser> {
		T create(int itemId, String tooltip);
	}

	private interface ParsedDataExporter<T extends AbstractTooltipParser> {
		void export(T parser, JsonItemDetailsAndTooltip itemDetailsAndTooltip);
	}

	private <T extends AbstractTooltipParser> void export(WowheadItemCategory category, ParserCreator<T> parserCreator, ParsedDataExporter<T> parsedDataExporter) throws IOException {
		List<Integer> itemIds = getItemDetailRepository().getItemIds(getGameVersion(), category);

		for (Integer itemId : itemIds) {
			var itemDetailsAndTooltip = getItemDetailRepository().getDetail(getGameVersion(), category, itemId).orElseThrow();
			var parser = parserCreator.create(itemId, itemDetailsAndTooltip.getHtmlTooltip());

			parser.parse();
			parsedDataExporter.export(parser, itemDetailsAndTooltip);

			log.info("Added {} {}", parser.getItemId(), parser.getName());
		}
	}
}
