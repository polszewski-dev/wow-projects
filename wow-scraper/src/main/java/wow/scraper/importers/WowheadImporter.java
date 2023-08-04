package wow.scraper.importers;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import wow.commons.model.pve.GameVersionId;
import wow.scraper.config.ScraperConfig;
import wow.scraper.fetchers.WowheadFetcher;
import wow.scraper.model.JsonCommonDetails;
import wow.scraper.util.GameVersionedMap;

import java.util.List;
import java.util.Optional;

/**
 * User: POlszewski
 * Date: 2023-05-19
 */
@RequiredArgsConstructor
@Getter
@Slf4j
public abstract class WowheadImporter<C, D extends JsonCommonDetails>  {
	private final String url;
	private final C category;
	private GameVersionId gameVersion;
	private ScraperConfig scraperConfig;
	private WowheadFetcher wowheadFetcher;
	private final GameVersionedMap<Integer, D> result = new GameVersionedMap<>();

	public void init(ScraperConfig scraperConfig, WowheadFetcher wowheadFetcher) {
		this.scraperConfig = scraperConfig;
		this.wowheadFetcher = wowheadFetcher;
	}

	public Optional<D> get(GameVersionId gameVersion, int id) {
		importAll(gameVersion);
		return result.getOptional(gameVersion, id);
	}

	public List<Integer> getIds(GameVersionId gameVersion) {
		importAll(gameVersion);
		return result.keySet(gameVersion).stream()
				.sorted()
				.toList();
	}

	private void importAll(GameVersionId gameVersion) {
		if (result.containsKey(gameVersion)) {
			return;
		}

		this.gameVersion = gameVersion;

		List<D> detailsList = fetchDetailsList(url);

		for (D details : detailsList) {
			if (isToBeSaved(details)) {
				saveDetails(details);
			}
		}
	}

	private void saveDetails(D details) {
		try {
			beforeSave(details);
			result.put(gameVersion, details.getId(), details);
		} catch (Exception e) {
			log.error("Error while fetching tooltip for id: {} [{}]: {}", details.getId(), details.getName(), e.getMessage());
		}
	}

	protected abstract List<D> fetchDetailsList(String url);

	protected abstract boolean isToBeSaved(D details);

	protected abstract void beforeSave(D details);

	protected String fixTooltip(String tooltip) {
		//replace with something that doesn't change line numbers unlike <br>
		return tooltip.trim().replace("\n", "<span></span>");
	}
}
