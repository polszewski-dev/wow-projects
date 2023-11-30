package wow.scraper.importer.pve;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import wow.commons.model.pve.GameVersionId;
import wow.commons.util.GameVersionMap;
import wow.scraper.config.ScraperConfig;
import wow.scraper.fetcher.WowheadFetcher;

import java.util.List;
import java.util.Optional;

/**
 * User: POlszewski
 * Date: 2023-06-26
 */
@AllArgsConstructor
@Getter(AccessLevel.PROTECTED)
public abstract class PveImporter<T> {
	private ScraperConfig scraperConfig;
	private WowheadFetcher wowheadFetcher;
	private final GameVersionMap<Integer, T> result = new GameVersionMap<>();

	public List<T> getList(GameVersionId gameVersion) {
		importAll(gameVersion);
		return result.values(gameVersion).stream().toList();
	}

	public Optional<T> getById(GameVersionId gameVersion, Integer id) {
		importAll(gameVersion);
		return result.getOptional(gameVersion, id);
	}

	private void importAll(GameVersionId gameVersion) {
		if (result.containsKey(gameVersion)) {
			return;
		}

		doImport(gameVersion);
	}

	protected abstract void doImport(GameVersionId gameVersion);

	protected void saveDetails(GameVersionId gameVersion, int id, T value) {
		result.put(gameVersion, id, value);
	}
}
