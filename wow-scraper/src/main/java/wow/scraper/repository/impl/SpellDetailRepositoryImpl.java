package wow.scraper.repository.impl;

import org.springframework.stereotype.Repository;
import wow.scraper.config.ScraperConfig;
import wow.scraper.fetchers.WowheadFetcher;
import wow.scraper.importers.spell.SpellEnchantImporter;
import wow.scraper.importers.spell.SpellImporter;
import wow.scraper.model.JsonSpellDetails;
import wow.scraper.model.WowheadSpellCategory;
import wow.scraper.repository.SpellDetailRepository;

import java.util.stream.Stream;

/**
 * User: POlszewski
 * Date: 2022-10-28
 */
@Repository
public class SpellDetailRepositoryImpl extends DetailRepository<WowheadSpellCategory, JsonSpellDetails, SpellImporter> implements SpellDetailRepository {
	public SpellDetailRepositoryImpl(ScraperConfig scraperConfig, WowheadFetcher wowheadFetcher) {
		super(scraperConfig, wowheadFetcher);
	}

	@Override
	protected Stream<SpellImporter> createImporters() {
		return Stream.of(
				new SpellEnchantImporter()
		);
	}
}
