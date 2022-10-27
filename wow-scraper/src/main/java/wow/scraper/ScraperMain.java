package wow.scraper;

import wow.commons.model.pve.GameVersion;
import wow.scraper.model.*;
import wow.scraper.repository.ItemDetailRepository;
import wow.scraper.repository.WowheadFetcher;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

/**
 * User: POlszewski
 * Date: 2022-10-26
 */
public class ScraperMain {
	private static final GameVersion GAME_VERSION = GameVersion.TBC;
	private static final int MIN_ITEM_LEVEL = 60;
	private static final WowheadItemQuality MIN_QUALITY = WowheadItemQuality.Uncommon;

	private static final Set<Integer> IGNORED_ITEM_IDS = Set.of(
			22736,
			30311,
			30312,
			30313,
			30314,
			30316,
			30317,
			30318,
			30319
	);

	public static void main(String[] args) throws IOException {
		fetch("items/armor/cloth/slot:5", WowheadItemCategory.chest);
		fetch("items/armor/cloth/slot:8", WowheadItemCategory.feet);
		fetch("items/armor/cloth/slot:10", WowheadItemCategory.hands);
		fetch("items/armor/cloth/slot:1", WowheadItemCategory.head);
		fetch("items/armor/cloth/slot:7", WowheadItemCategory.legs);
		fetch("items/armor/cloth/slot:3", WowheadItemCategory.shoulder);
		fetch("items/armor/cloth/slot:6", WowheadItemCategory.waist);
		fetch("items/armor/cloth/slot:9", WowheadItemCategory.wrist);
		fetch("items/armor/amulets/slot:9", WowheadItemCategory.amulets);
		fetch("items/armor/rings", WowheadItemCategory.rings);
		fetch("items/armor/trinkets", WowheadItemCategory.trinkets);
		fetch("items/armor/cloaks", WowheadItemCategory.cloaks);
		fetch("items/armor/off-hand-frills", WowheadItemCategory.off_hands);
		fetch("items/weapons/daggers", WowheadItemCategory.daggers);
		fetch("items/weapons/one-handed-swords", WowheadItemCategory.one_handed_swords);
		fetch("items/weapons/staves", WowheadItemCategory.staves);
		fetch("items/weapons/wands", WowheadItemCategory.wands);

		fetch("items/gems/type:0:1:2:3:4:5:6:8?filter=81;7;0", WowheadItemCategory.gems);
	}

	private static void fetch(String url, WowheadItemCategory category) throws IOException {
		System.out.printf("Fetching %s...%n", url);

		List<JsonItemDetails> itemDetailsList = WowheadFetcher.fetchItemDetails(GAME_VERSION, url);

		for (JsonItemDetails itemDetails : itemDetailsList) {
			if (isToBeSaved(itemDetails)) {
//				System.out.printf("%s: %s %s %s%n", itemDetails.getId(), itemDetails.getSource(), itemDetails.getName(), itemDetails.getSourceMores());
				fixSource(itemDetails);
				saveItemDetails(itemDetails, category);
			}
		}
	}

	private static boolean isToBeSaved(JsonItemDetails itemDetails) {
		return itemDetails.getSources() != null &&
				(itemDetails.getLevel() == null || itemDetails.getLevel() >= MIN_ITEM_LEVEL) &&
				itemDetails.getQuality() >= MIN_QUALITY.getCode() &&
				!IGNORED_ITEM_IDS.contains(itemDetails.getId())
				;
	}

	private static void fixSource(JsonItemDetails itemDetails) {
		List<Integer> sources = itemDetails.getSources();

		if (sources.size() == 1) {
			return;
		}

		int craftedPosition = IntStream.iterate(0, i -> i < sources.size(), i -> i + 1)
				.filter(i -> sources.get(i) == WowheadSource.crafted.getCode())
				.findFirst().orElse(-1);

		if (craftedPosition >= 0) {
			sources.clear();
			sources.add(WowheadSource.crafted.getCode());
			JsonSourceMore sourceMore = itemDetails.getSourceMores().get(craftedPosition);
			itemDetails.getSourceMores().clear();
			itemDetails.getSourceMores().add(sourceMore);
		}

		if (sources.size() != 1) {
			throw new IllegalArgumentException("Unfixable double source: " + itemDetails.getName());
		}
	}

	private static void saveItemDetails(JsonItemDetails itemDetails, WowheadItemCategory category) {
		if (ItemDetailRepository.hasItemDetail(GAME_VERSION, category, itemDetails.getId())) {
			System.out.printf("Tooltip for item id: %s [%s] already exists%n", itemDetails.getId(), itemDetails.getName());
			return;
		}

		try {
			WowheadItemInfo itemInfo = WowheadFetcher.fetchTooltip(GAME_VERSION, itemDetails.getId());
			JsonItemDetailsAndTooltip detailsAndTooltip = new JsonItemDetailsAndTooltip(itemDetails, itemInfo.getTooltip(), itemInfo.getIcon());
			ItemDetailRepository.saveItemDetail(GAME_VERSION, category, itemDetails.getId(), detailsAndTooltip);
			System.out.printf("Fetched tooltip for item id: %s [%s]%n", itemDetails.getId(), itemDetails.getName());
		} catch (IOException e) {
			System.err.printf("Error while fetching tooltip for item id: %s [%s]: %s%n", itemDetails.getId(), itemDetails.getName(), e.getMessage());
		}
	}
}
