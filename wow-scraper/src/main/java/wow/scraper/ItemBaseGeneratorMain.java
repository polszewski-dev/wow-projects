package wow.scraper;

import lombok.extern.slf4j.Slf4j;
import wow.commons.model.pve.GameVersionId;
import wow.scraper.excel.ItemBaseExcelBuilder;
import wow.scraper.model.*;
import wow.scraper.parsers.WowheadSourceParser;
import wow.scraper.parsers.tooltip.AbstractTooltipParser;
import wow.scraper.parsers.tooltip.GemTooltipParser;
import wow.scraper.parsers.tooltip.ItemTooltipParser;
import wow.scraper.parsers.tooltip.TradedItemParser;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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
		T create(JsonItemDetailsAndTooltip itemDetailsAndTooltip, GameVersionId gameVersion);
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
			for (GameVersionId gameVersion : GameVersionId.values()) {
				getItemDetailRepository()
						.getDetail(gameVersion, category, itemId)
						.ifPresent(value -> export(value, gameVersion, parserCreator, parsedDataExporter));
			}
		}
	}

	private <T extends AbstractTooltipParser> void export(
			JsonItemDetailsAndTooltip itemDetailsAndTooltip,
			GameVersionId gameVersion,
			ParserCreator<T> parserCreator,
			ParsedDataExporter<T> parsedDataExporter
	) {
		var parser = parserCreator.create(itemDetailsAndTooltip, gameVersion);

		parser.parse();
		fixMissingRequiredLevel(parser);
		parsedDataExporter.export(parser);

		log.info("Added {} {} [{}]", parser.getItemId(), parser.getName(), gameVersion);
	}

	private void fixMissingRequiredLevel(AbstractTooltipParser parser) {
		if (parser.getRequiredLevel() != null) {
			return;
		}

		for (WowheadQuestInfo questInfo : getQuestInfos(parser)) {
			if (questInfo.getRequiredLevel() != null) {
				parser.setRequiredLevel(questInfo.getRequiredLevel());
			}
			if (questInfo.getRequiredSide() != null) {
				parser.setRequiredSide(questInfo.getRequiredSide());
			}
		}
	}

	private List<WowheadQuestInfo> getQuestInfos(AbstractTooltipParser parser) {
		return parser.getItemDetailsAndTooltip().getDetails().getSourcesOf(WowheadSource.QUEST).stream()
				.map(JsonSourceMore::getTi)
				.filter(Objects::nonNull)
				.map(this::getWowheadQuestInfo)
				.flatMap(Optional::stream)
				.toList();
	}

	private Optional<WowheadQuestInfo> getWowheadQuestInfo(Integer questId) {
		try {
			return getQuestInfoRepository().getQuestInfo(getGameVersion(), questId);
		} catch (IOException e) {
			log.error("Error accessing quest info: " + questId, e);
			return Optional.empty();
		}
	}

	private List<Integer> getItemIds(WowheadItemCategory category) {
		return Stream.of(GameVersionId.values())
				.map(gameVersion -> getItemDetailRepository().getItemIds(gameVersion, category))
				.flatMap(Collection::stream)
				.distinct()
				.sorted()
				.toList();
	}
}
