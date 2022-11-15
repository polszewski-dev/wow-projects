package wow.scraper;

import lombok.extern.slf4j.Slf4j;
import wow.commons.model.pve.GameVersion;
import wow.scraper.excel.ItemBaseExcelBuilder;
import wow.scraper.model.WowheadItemCategory;
import wow.scraper.parsers.AbstractTooltipParser;
import wow.scraper.parsers.GemTooltipParser;
import wow.scraper.parsers.ItemTooltipParser;

import java.io.IOException;
import java.util.List;

/**
 * User: POlszewski
 * Date: 2022-10-29
 */
@Slf4j
public class ItemBaseGeneratorMain extends ScraperTool {
	private static final GameVersion GAME_VERSION = GameVersion.TBC;
	
	public static void main(String[] args) throws Exception {
		new ItemBaseGeneratorMain().run();
	}

	@Override
	protected void run() throws Exception {
		ItemBaseExcelBuilder itemBaseExcelBuilder = new ItemBaseExcelBuilder();
		itemBaseExcelBuilder.start();

		addEquipment(itemBaseExcelBuilder);

		itemBaseExcelBuilder.timeForGems();

		addGems(itemBaseExcelBuilder);

		String itemFilePath = "scraper/item_base.xls";

		itemBaseExcelBuilder.finish(itemFilePath);

		log.info("Saved to {}", itemFilePath);

		AbstractTooltipParser.reportUnmatchedLines(log);
	}

	private void addEquipment(ItemBaseExcelBuilder itemBaseExcelBuilder) throws IOException {
		for (WowheadItemCategory category : WowheadItemCategory.equipment()) {
			List<Integer> itemIds = getItemDetailRepository().getItemIds(GAME_VERSION, category);

			for (Integer itemId : itemIds) {
				var itemDetailsAndTooltip = getItemDetailRepository().getDetail(GAME_VERSION, category, itemId).orElseThrow();
				var parser = new ItemTooltipParser(itemId, itemDetailsAndTooltip.getHtmlTooltip(), GAME_VERSION);

				parser.parse();
				itemBaseExcelBuilder.add(parser, itemDetailsAndTooltip);

				log.info("Added {} {}", parser.getItemId(), parser.getName());
			}
		}
	}

	private void addGems(ItemBaseExcelBuilder itemBaseExcelBuilder) throws IOException {
		WowheadItemCategory category = WowheadItemCategory.GEMS;
		List<Integer> itemIds = getItemDetailRepository().getItemIds(GAME_VERSION, category);

		for (Integer itemId : itemIds) {
			var itemDetailsAndTooltip = getItemDetailRepository().getDetail(GAME_VERSION, category, itemId).orElseThrow();
			var parser = new GemTooltipParser(itemId, itemDetailsAndTooltip.getHtmlTooltip(), GAME_VERSION);

			parser.parse();
			itemBaseExcelBuilder.add(parser, itemDetailsAndTooltip);

			log.info("Added {} {}", parser.getItemId(), parser.getName());
		}
	}
}
