package wow.scraper.repository.impl;

import wow.commons.model.pve.GameVersionId;
import wow.scraper.config.ScraperConfig;
import wow.scraper.config.ScraperDatafixes;
import wow.scraper.fetcher.WowheadFetcher;
import wow.scraper.importer.WowheadImporter;
import wow.scraper.model.JsonCommonDetails;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * User: POlszewski
 * Date: 2023-06-25
 */
public abstract class DetailRepository<C, D extends JsonCommonDetails, I extends WowheadImporter<C, D>> {
	private final Map<C, I> importers;

	protected DetailRepository(ScraperConfig scraperConfig, ScraperDatafixes scraperDatafixes, WowheadFetcher wowheadFetcher) {
		this.importers = createImporters()
				.collect(Collectors.toMap(WowheadImporter::getCategory, x -> x));
		importers.values().forEach(x -> x.init(scraperConfig, scraperDatafixes, wowheadFetcher));
	}

	protected abstract Stream<I> createImporters();

	public Optional<D> getDetail(GameVersionId gameVersion, C category, int id) {
		return importers.get(category).get(gameVersion, id);
	}

	public List<Integer> getDetailIds(GameVersionId gameVersion, C category) {
		return importers.get(category).getIds(gameVersion);
	}

	public boolean appearedInPreviousVersion(Integer detailId, GameVersionId gameVersion, C category) {
		var previousVersion = gameVersion.getPreviousVersion();

		if (previousVersion.isEmpty()) {
			return false;
		}

		return getDetail(previousVersion.get(), category, detailId).isPresent();
	}
}
