package wow.scraper.repository.impl;

import wow.commons.model.pve.GameVersionId;
import wow.scraper.config.ScraperConfig;
import wow.scraper.fetchers.WowheadFetcher;
import wow.scraper.importers.WowheadImporter;
import wow.scraper.model.JsonCommonDetails;

import java.io.IOException;
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

	protected DetailRepository(ScraperConfig scraperConfig, WowheadFetcher wowheadFetcher) {
		this.importers = createImporters()
				.collect(Collectors.toMap(WowheadImporter::getCategory, x -> x));
		importers.values().forEach(x -> x.init(scraperConfig, wowheadFetcher));
	}

	protected abstract Stream<I> createImporters();

	public Optional<D> getDetail(GameVersionId gameVersion, C category, int id) throws IOException {
		return importers.get(category).get(gameVersion, id);
	}

	public List<Integer> getDetailIds(GameVersionId gameVersion, C category) throws IOException {
		return importers.get(category).getIds(gameVersion);
	}
}
