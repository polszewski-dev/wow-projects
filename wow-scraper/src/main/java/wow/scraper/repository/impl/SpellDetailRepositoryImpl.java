package wow.scraper.repository.impl;

import org.springframework.stereotype.Repository;
import wow.commons.model.pve.GameVersionId;
import wow.scraper.config.ScraperConfig;
import wow.scraper.model.JsonSpellDetails;
import wow.scraper.model.WowheadSpellCategory;
import wow.scraper.repository.SpellDetailRepository;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * User: POlszewski
 * Date: 2022-10-28
 */
@Repository
public class SpellDetailRepositoryImpl implements SpellDetailRepository {
	private final JsonFileRepository<JsonSpellDetails> repository;

	public SpellDetailRepositoryImpl(ScraperConfig scraperConfig) {
		this.repository = new JsonFileRepository<>(
				scraperConfig,
				JsonSpellDetails.class
		);
	}

	@Override
	public boolean hasDetail(GameVersionId gameVersion, WowheadSpellCategory category, int spellId) {
		return repository.has(getIdParts(gameVersion, category, spellId));
	}

	@Override
	public Optional<JsonSpellDetails> getDetail(GameVersionId gameVersion, WowheadSpellCategory category, int spellId) throws IOException {
		return repository.get(getIdParts(gameVersion, category, spellId));
	}

	@Override
	public void saveDetail(GameVersionId gameVersion, WowheadSpellCategory category, int spellId, JsonSpellDetails spellDetails) throws IOException {
		repository.save(getIdParts(gameVersion, category, spellId), spellDetails);
	}

	@Override
	public List<Integer> getSpellIds(GameVersionId gameVersion, WowheadSpellCategory category) {
		return repository.getIds(getIdParts(gameVersion, category, 0)).stream()
				.map(Integer::valueOf)
				.sorted()
				.toList();
	}

	private String[] getIdParts(GameVersionId gameVersion, WowheadSpellCategory category, int spellId) {
		return new String[] {
				"spells",
				gameVersion.toString().toLowerCase(),
				category.name().toLowerCase(),
				Integer.toString(spellId)
		};
	}
}
