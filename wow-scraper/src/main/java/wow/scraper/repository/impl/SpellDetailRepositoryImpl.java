package wow.scraper.repository.impl;

import org.springframework.stereotype.Repository;
import wow.scraper.config.ScraperConfig;
import wow.scraper.fetcher.WowheadFetcher;
import wow.scraper.importer.spell.SpellEnchantImporter;
import wow.scraper.importer.spell.SpellImporter;
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
		var stream1 = Stream.of(WowheadSpellCategory.values())
				.filter(x -> x.getType() != WowheadSpellCategory.Type.ENCHANT)
				.map(SpellImporter::new);
		var stream2 = Stream.of(new SpellEnchantImporter());

		return Stream.concat(stream1, stream2);
	}
}
