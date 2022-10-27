package wow.scraper;

import wow.commons.model.pve.GameVersion;
import wow.scraper.excel.ItemBaseExcelBuilder;
import wow.scraper.model.WowheadItemCategory;
import wow.scraper.parsers.GemTooltipParser;
import wow.scraper.parsers.ItemTooltipParser;
import wow.scraper.repository.ItemDetailRepository;

import java.io.IOException;
import java.util.List;

/**
 * User: POlszewski
 * Date: 2022-10-29
 */
public class ItemBaseGeneratorMain {
	private static final GameVersion GAME_VERSION = GameVersion.TBC;

	public static void main(String[] args) throws IOException {
		ItemBaseExcelBuilder itemBaseExcelBuilder = new ItemBaseExcelBuilder();
		itemBaseExcelBuilder.start();

		addEquipment(itemBaseExcelBuilder);

		itemBaseExcelBuilder.timeForGems();

		addGems(itemBaseExcelBuilder);

		String itemFilePath = "scraper/item_base.xls";

		itemBaseExcelBuilder.finish(itemFilePath);
		System.out.printf("Saved to %s%n", itemFilePath);

		ItemTooltipParser.UNMATCHED_LINES.forEach(System.out::println);
	}

	private static void addEquipment(ItemBaseExcelBuilder itemBaseExcelBuilder) throws IOException {
		for (WowheadItemCategory category : WowheadItemCategory.equipment()) {
			List<Integer> itemIds = ItemDetailRepository.getItemIds(GAME_VERSION, category);

			for (Integer itemId : itemIds) {
				var itemDetailsAndTooltip = ItemDetailRepository.getDetail(GAME_VERSION, category, itemId).orElseThrow();
				var parser = new ItemTooltipParser(itemId, itemDetailsAndTooltip.getHtmlTooltip());

				parser.parse();
				itemBaseExcelBuilder.add(parser, itemDetailsAndTooltip);

				System.out.printf("Added %s %s%n", parser.getItemId(), parser.getName());
			}
		}
	}

	private static void addGems(ItemBaseExcelBuilder itemBaseExcelBuilder) throws IOException {
		WowheadItemCategory category = WowheadItemCategory.gems;
		List<Integer> itemIds = ItemDetailRepository.getItemIds(GAME_VERSION, category);

		for (Integer itemId : itemIds) {
			var itemDetailsAndTooltip = ItemDetailRepository.getDetail(GAME_VERSION, category, itemId).orElseThrow();
			var parser = new GemTooltipParser(itemId, itemDetailsAndTooltip.getHtmlTooltip());

			parser.parse();
			itemBaseExcelBuilder.add(parser, itemDetailsAndTooltip);

			System.out.printf("Added %s %s%n", parser.getItemId(), parser.getName());
		}
	}
}
