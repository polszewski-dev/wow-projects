package wow.scraper;

import lombok.extern.slf4j.Slf4j;
import wow.commons.model.pve.GameVersionId;
import wow.commons.model.pve.Side;
import wow.scraper.model.*;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

/**
 * User: POlszewski
 * Date: 2022-10-26
 */
@Slf4j
public class ScraperMain extends ScraperTool {
	public static void main(String[] args) throws IOException {
		new ScraperMain().run();
	}

	@Override
	protected void run() throws IOException {
		fetch("items/armor/cloth/slot:5", WowheadItemCategory.CHEST);
		fetch("items/armor/cloth/slot:8", WowheadItemCategory.FEET);
		fetch("items/armor/cloth/slot:10", WowheadItemCategory.HANDS);
		fetch("items/armor/cloth/slot:1", WowheadItemCategory.HEAD);
		fetch("items/armor/cloth/slot:7", WowheadItemCategory.LEGS);
		fetch("items/armor/cloth/slot:3", WowheadItemCategory.SHOULDER);
		fetch("items/armor/cloth/slot:6", WowheadItemCategory.WAIST);
		fetch("items/armor/cloth/slot:9", WowheadItemCategory.WRIST);
		fetch("items/armor/amulets/slot:9", WowheadItemCategory.AMULETS);
		fetch("items/armor/rings", WowheadItemCategory.RINGS);
		fetch("items/armor/trinkets", WowheadItemCategory.TRINKETS);
		fetch("items/armor/cloaks", WowheadItemCategory.CLOAKS);
		fetch("items/armor/off-hand-frills", WowheadItemCategory.OFF_HANDS);
		fetch("items/weapons/daggers", WowheadItemCategory.DAGGERS);
		fetch("items/weapons/one-handed-swords", WowheadItemCategory.ONE_HANDED_SWORDS);
		fetch("items/weapons/one-handed-maces", WowheadItemCategory.ONE_HANDED_MACES);
		fetch("items/weapons/staves", WowheadItemCategory.STAVES);
		fetch("items/weapons/wands", WowheadItemCategory.WANDS);
		if (getGameVersion() != GameVersionId.VANILLA) {
			fetch("items/gems/type:0:1:2:3:4:5:6:8?filter=81;7;0", WowheadItemCategory.GEMS);
		}
		fetch("items/miscellaneous/armor-tokens", WowheadItemCategory.TOKENS);
		fetch("items/quest/quality:3:4:5?filter=6;1;0", WowheadItemCategory.QUEST);
	}

	private void fetch(String url, WowheadItemCategory category) throws IOException {
		log.info("Fetching {} ...", url);

		List<JsonItemDetails> itemDetailsList = getWowheadFetcher().fetchItemDetails(getGameVersion(), url);

		for (JsonItemDetails itemDetails : itemDetailsList) {
			if (isToBeSaved(itemDetails, category)) {
				try {
					fixSource(itemDetails);
					saveItemDetails(itemDetails, category);
				} catch (Exception e) {
					log.error("Error while fetching:  " + itemDetails.getName(), e);
				}
			}
		}

		if (category == WowheadItemCategory.TOKENS) {
			for (Integer tokenId : getScraperConfig().getTokenToTradedFor().keySet()) {
				fetchMissingToken(tokenId);
			}
		}
	}

	private void fetchMissingToken(Integer tokenId) {
		JsonItemDetails itemDetails = new JsonItemDetails();
		itemDetails.setId(tokenId);
		itemDetails.setName("" + tokenId);
		itemDetails.setSources(List.of());
		itemDetails.setQuality(WowheadItemQuality.EPIC.getCode());
		saveItemDetails(itemDetails, WowheadItemCategory.TOKENS);
	}

	private boolean isToBeSaved(JsonItemDetails itemDetails, WowheadItemCategory category) {
		if (itemDetails.getSources() == null) {
			return false;
		}
		if (category.isEquipment() && itemDetails.getLevel() != null && itemDetails.getLevel() < getScraperConfig().getMinItemLevel()) {
			return false;
		}
		if (itemDetails.getQuality() < getScraperConfig().getMinQuality().getCode()) {
			return false;
		}
		return !getScraperConfig().getIgnoredItemIds().contains(itemDetails.getId());
	}

	private void fixSource(JsonItemDetails itemDetails) {
		List<Integer> sources = itemDetails.getSources();

		if (sources.size() == 1) {
			return;
		}

		int craftedPosition = IntStream.iterate(0, i -> i < sources.size(), i -> i + 1)
				.filter(i -> sources.get(i) == WowheadSource.CRAFTED.getCode())
				.findFirst().orElse(-1);

		if (craftedPosition >= 0) {
			sources.clear();
			sources.add(WowheadSource.CRAFTED.getCode());
			JsonSourceMore sourceMore = itemDetails.getSourceMores().get(craftedPosition);
			itemDetails.getSourceMores().clear();
			itemDetails.getSourceMores().add(sourceMore);
		}

		if (sources.size() != 1) {
			throw new IllegalArgumentException("Unfixable double source: " + itemDetails.getName());
		}
	}

	private void saveItemDetails(JsonItemDetails itemDetails, WowheadItemCategory category) {
		if (getItemDetailRepository().hasItemDetail(getGameVersion(), category, itemDetails.getId())) {
			log.info("Tooltip for item id: {} [{}] already exists", itemDetails.getId(), itemDetails.getName());
			return;
		}

		try {
			WowheadItemInfo itemInfo = getWowheadFetcher().fetchItemTooltip(getGameVersion(), itemDetails.getId());
			String tooltip = fixTooltip(itemInfo.getTooltip());
			String icon = itemInfo.getIcon();
			JsonItemDetailsAndTooltip detailsAndTooltip = new JsonItemDetailsAndTooltip(itemDetails, tooltip, icon);
			getItemDetailRepository().saveItemDetail(getGameVersion(), category, itemDetails.getId(), detailsAndTooltip);
			log.info("Fetched tooltip for item id: {} [{}]", itemDetails.getId(), itemDetails.getName());
			fetchSourceQuest(itemDetails);
		} catch (IOException e) {
			log.error("Error while fetching tooltip for item id: {} [{}]: {}", itemDetails.getId(), itemDetails.getName(), e.getMessage());
		}
	}

	private void fetchSourceQuest(JsonItemDetails itemDetails) throws IOException {
		for (var questSource : itemDetails.getSourcesOf(WowheadSource.QUEST)) {
			processSourceQuest(questSource, itemDetails);
		}
	}

	private void processSourceQuest(JsonSourceMore sourceMore, JsonItemDetails itemDetails) throws IOException {
		Integer questId = sourceMore.getTi();

		if (questId == null) {
			log.error("No Ti field for {}", itemDetails.getId());
			return;
		}

		if (getQuestInfoRepository().hasQuestInfo(getGameVersion(), questId)) {
			log.info("Tooltip for quest {} already exists", questId);
			return;
		}

		String questHtml = getWowheadFetcher().fetchRaw(getGameVersion(), "quest=" + questId);

		getQuestInfoRepository().saveQuestInfo(getGameVersion(), questId, parseQuestInfo(questHtml));

		log.info("Fetched tooltip for quest {}", questId);
	}

	private WowheadQuestInfo parseQuestInfo(String questHtml) {
		var questInfo = new WowheadQuestInfo();
		questInfo.setHtml(questHtml);
		questInfo.setRequiredLevel(parseQuestRequiredLevel(questHtml));
		questInfo.setRequiredSide(parseRequiredSide(questHtml));
		return questInfo;
	}

	private Integer parseQuestRequiredLevel(String html) {
		Pattern pattern = Pattern.compile("\\[li]Requires level (\\d+)\\[\\\\/li]");
		Matcher matcher = pattern.matcher(html);
		if (matcher.find()) {
			return Integer.valueOf(matcher.group(1));
		}
		return null;
	}

	private Side parseRequiredSide(String html) {
		Pattern pattern = Pattern.compile("\\[li]Side: \\[span class=icon-.+](Horde|Alliance)\\[\\\\/span]\\[\\\\/li]");
		Matcher matcher = pattern.matcher(html);
		if (matcher.find()) {
			return Side.parse(matcher.group(1));
		}
		return null;
	}

	private String fixTooltip(String tooltip) {
		//replace with something that doesn't change line numbers unlike <br>
		return tooltip.trim().replace("\n", "<span></span>");
	}
}
