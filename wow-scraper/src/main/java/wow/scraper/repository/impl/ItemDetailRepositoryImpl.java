package wow.scraper.repository.impl;

import org.springframework.stereotype.Repository;
import wow.commons.model.pve.GameVersionId;
import wow.scraper.config.ScraperConfig;
import wow.scraper.model.JsonItemDetails;
import wow.scraper.model.WowheadItemCategory;
import wow.scraper.repository.ItemDetailRepository;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * User: POlszewski
 * Date: 2022-10-28
 */
@Repository
public class ItemDetailRepositoryImpl implements ItemDetailRepository {
	private final JsonFileRepository<JsonItemDetails> repository;

	public ItemDetailRepositoryImpl(ScraperConfig scraperConfig) {
		this.repository = new JsonFileRepository<>(
				scraperConfig,
				JsonItemDetails.class
		);
	}

	@Override
	public boolean hasDetail(GameVersionId gameVersion, WowheadItemCategory category, int itemId) {
		return repository.has(getIdParts(gameVersion, category, itemId));
	}

	@Override
	public Optional<JsonItemDetails> getDetail(GameVersionId gameVersion, WowheadItemCategory category, int itemId) throws IOException {
		return repository.get(getIdParts(gameVersion, category, itemId));
	}

	@Override
	public void saveDetail(GameVersionId gameVersion, WowheadItemCategory category, int itemId, JsonItemDetails itemDetails) throws IOException {
		repository.save(getIdParts(gameVersion, category, itemId), itemDetails);
	}

	@Override
	public List<Integer> getItemIds(GameVersionId gameVersion, WowheadItemCategory category) {
		return repository.getIds(getIdParts(gameVersion, category, 0)).stream()
				.map(Integer::valueOf)
				.sorted()
				.toList();
	}

	private String[] getIdParts(GameVersionId gameVersion, WowheadItemCategory category, int itemId) {
		return new String[] {
				"items",
				gameVersion.toString().toLowerCase(),
				category.name().toLowerCase(),
				Integer.toString(itemId)
		};
	}
}
