package wow.scraper.importers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import wow.scraper.config.ScraperContext;
import wow.scraper.config.ScraperContextSource;
import wow.scraper.importers.parsers.QuestInfoParser;
import wow.scraper.model.*;

import java.io.IOException;
import java.util.List;
import java.util.stream.IntStream;

/**
 * User: POlszewski
 * Date: 2023-05-19
 */
@RequiredArgsConstructor
@Getter
@Slf4j
public abstract class WowheadImporter implements ScraperContextSource {
	protected ScraperContext scraperContext;

	public void init(ScraperContext scraperContext) {
		this.scraperContext = scraperContext;
	}

	public abstract void importAll() throws IOException;

	protected void fetch(String url, WowheadItemCategory category) throws IOException {
		new ItemFetcher().fetch(url, category);
	}

	protected void fetch(String url, WowheadSpellCategory category) throws IOException {
		new SpellFetcher().fetch(url, category);
	}

	protected void saveItemDetails(JsonItemDetails itemDetails, WowheadItemCategory category) {
		new ItemFetcher().saveDetails(itemDetails, category);
	}

	@AllArgsConstructor
	protected abstract static class Fetcher<C, D extends JsonCommonDetails> {
		private final String type;

		public void fetch(String url, C category) throws IOException {
			log.info("Fetching {} ...", url);

			List<D> detailsList = fetchDetailsList(url);

			for (D details : detailsList) {
				if (isToBeSaved(category, details)) {
					saveDetails(details, category);
				}
			}
		}

		public void saveDetails(D details, C category) {
			if (hasAlreadySavedDetails(category, details)) {
				log.info("Tooltip for {} id: {} [{}] already exists", type, details.getId(), details.getName());
				return;
			}

			try {
				beforeSave(details);
				saveToRepo(category, details);
				log.info("Fetched tooltip for {} id: {} [{}]", type, details.getId(), details.getName());
				afterSave(details);
			} catch (Exception e) {
				log.error("Error while fetching tooltip for {} id: {} [{}]: {}", type, details.getId(), details.getName(), e.getMessage());
			}
		}

		protected abstract List<D> fetchDetailsList(String url) throws IOException;

		protected abstract boolean isToBeSaved(C category, D details);

		protected abstract boolean hasAlreadySavedDetails(C category, D details);

		protected abstract void beforeSave(D details) throws IOException;

		protected abstract void saveToRepo(C category, D details) throws IOException;

		protected abstract void afterSave(D details) throws IOException;
	}

	protected class ItemFetcher extends Fetcher<WowheadItemCategory, JsonItemDetails> {
		public ItemFetcher() {
			super("item");
		}

		@Override
		protected List<JsonItemDetails> fetchDetailsList(String url) throws IOException {
			return getWowheadFetcher().fetchItemDetails(getGameVersion(), url);
		}

		@Override
		protected boolean isToBeSaved(WowheadItemCategory category, JsonItemDetails details) {
			if (details.getSources() == null) {
				return false;
			}
			if (category.isEquipment() && details.getLevel() != null && details.getLevel() < getScraperConfig().getMinItemLevel()) {
				return false;
			}
			if (details.getQuality() < getScraperConfig().getMinQuality().getCode()) {
				return false;
			}
			return !getScraperConfig().getIgnoredItemIds().contains(details.getId());
		}

		@Override
		protected boolean hasAlreadySavedDetails(WowheadItemCategory category, JsonItemDetails details) {
			return getItemDetailRepository().hasDetail(getGameVersion(), category, details.getId());
		}

		@Override
		protected void beforeSave(JsonItemDetails details) throws IOException {
			fixSource(details);
			fetchTooltip(details);
		}

		@Override
		protected void saveToRepo(WowheadItemCategory category, JsonItemDetails details) throws IOException {
			getItemDetailRepository().saveDetail(getGameVersion(), category, details.getId(), details);
		}

		@Override
		protected void afterSave(JsonItemDetails details) throws IOException {
			fetchSourceQuest(details);
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

		private void fetchTooltip(JsonItemDetails details) throws IOException {
			WowheadItemInfo info = getWowheadFetcher().fetchItemTooltip(getGameVersion(), details.getId());
			details.setHtmlTooltip(fixTooltip(info.getTooltip()));
			details.setIcon(info.getIcon());
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
			WowheadQuestInfo questInfo = new QuestInfoParser(questHtml).parse();

			getQuestInfoRepository().saveQuestInfo(getGameVersion(), questId, questInfo);

			log.info("Fetched tooltip for quest {}", questId);
		}
	}

	protected class SpellFetcher extends Fetcher<WowheadSpellCategory, JsonSpellDetails> {
		public SpellFetcher() {
			super("spell");
		}

		@Override
		protected List<JsonSpellDetails> fetchDetailsList(String url) throws IOException {
			return getWowheadFetcher().fetchSpellDetails(getGameVersion(), url);
		}

		@Override
		protected boolean isToBeSaved(WowheadSpellCategory category, JsonSpellDetails details) {
			return !getScraperConfig().getIgnoredSpellIds().contains(details.getId());
		}

		@Override
		protected boolean hasAlreadySavedDetails(WowheadSpellCategory category, JsonSpellDetails details) {
			return getSpellDetailRepository().hasDetail(getGameVersion(), category, details.getId());
		}

		@Override
		protected void beforeSave(JsonSpellDetails details) throws IOException {
			fetchTooltip(details);
		}

		@Override
		protected void saveToRepo(WowheadSpellCategory category, JsonSpellDetails details) throws IOException {
			getSpellDetailRepository().saveDetail(getGameVersion(), category, details.getId(), details);
		}

		@Override
		protected void afterSave(JsonSpellDetails details) {
			// VOID
		}

		private void fetchTooltip(JsonSpellDetails details) throws IOException {
			WowheadSpellInfo info = getWowheadFetcher().fetchSpellTooltip(getGameVersion(), details.getId());
			details.setHtmlTooltip(fixTooltip(info.getTooltip()));
			details.setIcon(info.getIcon());
		}
	}

	private String fixTooltip(String tooltip) {
		//replace with something that doesn't change line numbers unlike <br>
		return tooltip.trim().replace("\n", "<span></span>");
	}
}
