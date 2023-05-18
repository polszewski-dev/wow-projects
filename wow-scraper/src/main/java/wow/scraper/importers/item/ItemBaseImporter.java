package wow.scraper.importers.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import wow.commons.model.pve.GameVersionId;
import wow.scraper.config.ScraperConfig;
import wow.scraper.importers.item.parsers.QuestInfoParser;
import wow.scraper.model.*;
import wow.scraper.repository.ItemDetailRepository;
import wow.scraper.repository.QuestInfoRepository;
import wow.scraper.repository.WowheadFetcher;

import java.io.IOException;
import java.util.List;
import java.util.stream.IntStream;

/**
 * User: POlszewski
 * Date: 2023-05-19
 */
@RequiredArgsConstructor
@Slf4j
public abstract class ItemBaseImporter {
	protected ScraperConfig scraperConfig;
	protected GameVersionId gameVersion;
	protected WowheadFetcher wowheadFetcher;
	protected ItemDetailRepository itemDetailRepository;
	protected QuestInfoRepository questInfoRepository;

	public void init(
			ScraperConfig scraperConfig,
			WowheadFetcher wowheadFetcher,
			ItemDetailRepository itemDetailRepository,
			QuestInfoRepository questInfoRepository
	) {
		this.scraperConfig = scraperConfig;
		this.gameVersion = scraperConfig.getGameVersion();
		this.wowheadFetcher = wowheadFetcher;
		this.itemDetailRepository = itemDetailRepository;
		this.questInfoRepository = questInfoRepository;
	}

	public abstract void importAll() throws IOException;

	protected void fetch(String url, WowheadItemCategory category) throws IOException {
		log.info("Fetching {} ...", url);

		List<JsonItemDetails> itemDetailsList = wowheadFetcher.fetchItemDetails(gameVersion, url);

		for (JsonItemDetails itemDetails : itemDetailsList) {
			if (isToBeSaved(itemDetails, category)) {
				saveItemDetails(itemDetails, category);
			}
		}
	}

	private boolean isToBeSaved(JsonItemDetails itemDetails, WowheadItemCategory category) {
		if (itemDetails.getSources() == null) {
			return false;
		}
		if (category.isEquipment() && itemDetails.getLevel() != null && itemDetails.getLevel() < scraperConfig.getMinItemLevel()) {
			return false;
		}
		if (itemDetails.getQuality() < scraperConfig.getMinQuality().getCode()) {
			return false;
		}
		return !scraperConfig.getIgnoredItemIds().contains(itemDetails.getId());
	}

	protected void saveItemDetails(JsonItemDetails itemDetails, WowheadItemCategory category) {
		if (itemDetailRepository.hasDetail(gameVersion, category, itemDetails.getId())) {
			log.info("Tooltip for item id: {} [{}] already exists", itemDetails.getId(), itemDetails.getName());
			return;
		}

		try {
			fixSource(itemDetails);
			fetchTooltip(itemDetails);
			itemDetailRepository.saveDetail(gameVersion, category, itemDetails.getId(), itemDetails);
			log.info("Fetched tooltip for item id: {} [{}]", itemDetails.getId(), itemDetails.getName());
			fetchSourceQuest(itemDetails);
		} catch (Exception e) {
			log.error("Error while fetching tooltip for item id: {} [{}]: {}", itemDetails.getId(), itemDetails.getName(), e.getMessage());
		}
	}

	private void fixSource(JsonItemDetails itemDetails) {
		List<Integer> sources = itemDetails.getSources();

		if (sources.size() <= 1) {
			return;
		}

		if (tryReplaceSourcesWithCrafted(itemDetails, sources)) {
			return;
		}

		throw new IllegalArgumentException("Unfixable double source: " + itemDetails.getName());
	}

	private boolean tryReplaceSourcesWithCrafted(JsonItemDetails itemDetails, List<Integer> sources) {
		int craftedPosition = IntStream.iterate(0, i -> i < sources.size(), i -> i + 1)
				.filter(i -> sources.get(i) == WowheadSource.CRAFTED.getCode())
				.findFirst().orElse(-1);

		if (craftedPosition < 0) {
			return false;
		}

		JsonSourceMore sourceMore = itemDetails.getSourceMores().get(craftedPosition);

		itemDetails.getSources().clear();
		itemDetails.getSources().add(WowheadSource.CRAFTED.getCode());
		itemDetails.getSourceMores().clear();
		itemDetails.getSourceMores().add(sourceMore);

		return true;
	}

	private void fetchTooltip(JsonItemDetails itemDetails) throws IOException {
		WowheadItemInfo itemInfo = wowheadFetcher.fetchItemTooltip(gameVersion, itemDetails.getId());
		itemDetails.setHtmlTooltip(fixTooltip(itemInfo.getTooltip()));
		itemDetails.setIcon(itemInfo.getIcon());
	}

	private String fixTooltip(String tooltip) {
		//replace with something that doesn't change line numbers unlike <br>
		return tooltip.trim().replace("\n", "<span></span>");
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

		if (questInfoRepository.hasQuestInfo(gameVersion, questId)) {
			log.info("Tooltip for quest {} already exists", questId);
			return;
		}

		String questHtml = wowheadFetcher.fetchRaw(gameVersion, "quest=" + questId);
		WowheadQuestInfo questInfo = new QuestInfoParser(questHtml).parse();

		questInfoRepository.saveQuestInfo(gameVersion, questId, questInfo);

		log.info("Fetched tooltip for quest {}", questId);
	}
}
