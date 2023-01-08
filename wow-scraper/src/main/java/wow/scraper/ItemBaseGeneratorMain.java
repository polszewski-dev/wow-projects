package wow.scraper;

import lombok.extern.slf4j.Slf4j;
import wow.commons.model.pve.GameVersion;
import wow.scraper.excel.ItemBaseExcelBuilder;
import wow.scraper.model.JsonItemDetailsAndTooltip;
import wow.scraper.model.WowheadItemCategory;
import wow.scraper.parsers.WowheadSourceParser;
import wow.scraper.parsers.tooltip.AbstractTooltipParser;
import wow.scraper.parsers.tooltip.GemTooltipParser;
import wow.scraper.parsers.tooltip.ItemTooltipParser;
import wow.scraper.parsers.tooltip.TradedItemParser;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

		ItemBaseExcelBuilder builder = new ItemBaseExcelBuilder(getScraperConfig());
		builder.start();

		builder.addTradedItemHeader();
		addTokens(builder);
		addItemsStartingQuest(builder);

		builder.addItemHeader();
		addEquipment(builder);

		builder.addGemHeader();
		addGems(builder);

		String itemFilePath = getScraperConfig().getDirectoryPath() + "/item_base.xls";

		builder.finish(itemFilePath);

		log.info("Saved to {}", itemFilePath);

		AbstractTooltipParser.reportUnmatchedLines(log);
	}

	private void addEquipment(ItemBaseExcelBuilder builder) throws IOException {
		for (WowheadItemCategory category : WowheadItemCategory.equipment()) {
			addItems(builder, category);
		}
	}

	private void addItems(ItemBaseExcelBuilder builder, WowheadItemCategory category) throws IOException {
		export(
				category,
				(itemDetailsAndTooltip, gameVersion) -> new ItemTooltipParser(itemDetailsAndTooltip, gameVersion, getStatPatternRepository()),
				builder::add
		);
	}

	private void addGems(ItemBaseExcelBuilder builder) throws IOException {
		export(
				WowheadItemCategory.GEMS,
				(itemDetailsAndTooltip, gameVersion) -> new GemTooltipParser(itemDetailsAndTooltip, gameVersion, getStatPatternRepository()),
				builder::add
		);
	}

	private void addItemsStartingQuest(ItemBaseExcelBuilder builder) throws IOException {
		export(
				WowheadItemCategory.QUEST,
				(itemDetailsAndTooltip, gameVersion) -> new TradedItemParser(itemDetailsAndTooltip, gameVersion, getStatPatternRepository()),
				builder::add
		);
	}

	private void addTokens(ItemBaseExcelBuilder builder) throws IOException {
		export(
				WowheadItemCategory.TOKENS,
				(itemDetailsAndTooltip, gameVersion) -> new TradedItemParser(itemDetailsAndTooltip, gameVersion, getStatPatternRepository()),
				builder::add
		);
	}

	private interface ParserCreator<T extends AbstractTooltipParser> {
		T create(JsonItemDetailsAndTooltip itemDetailsAndTooltip, GameVersion gameVersion);
	}

	private interface ParsedDataExporter<T extends AbstractTooltipParser> {
		void export(T parser);
	}

	private <T extends AbstractTooltipParser> void export(
			WowheadItemCategory category,
			ParserCreator<T> parserCreator,
			ParsedDataExporter<T> parsedDataExporter
	) throws IOException {
		for (Integer itemId : getItemIds(category)) {
			for (GameVersion gameVersion : GameVersion.values()) {
				getItemDetailRepository()
						.getDetail(gameVersion, category, itemId)
						.ifPresent(value -> export(value, gameVersion, parserCreator, parsedDataExporter));
			}
		}
	}

	private <T extends AbstractTooltipParser> void export(
			JsonItemDetailsAndTooltip itemDetailsAndTooltip,
			GameVersion gameVersion,
			ParserCreator<T> parserCreator,
			ParsedDataExporter<T> parsedDataExporter
	) {
		var parser = parserCreator.create(itemDetailsAndTooltip, gameVersion);

		parser.parse();
		parsedDataExporter.export(parser);

		log.info("Added {} {} [{}]", parser.getItemId(), parser.getName(), gameVersion);
	}

	private List<Integer> getItemIds(WowheadItemCategory category) {
		return Stream.of(GameVersion.values())
				.map(gameVersion -> getItemDetailRepository().getItemIds(gameVersion, category))
				.flatMap(Collection::stream)
				.distinct()
				.sorted()
				.collect(Collectors.toList());
	}
}
