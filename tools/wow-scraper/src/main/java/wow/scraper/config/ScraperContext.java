package wow.scraper.config;

import wow.scraper.fetcher.WowheadFetcher;
import wow.scraper.repository.*;

/**
 * User: POlszewski
 * Date: 2023-06-20
 */
public interface ScraperContext {
	WowheadFetcher getWowheadFetcher();

	ItemDetailRepository getItemDetailRepository();

	SpellDetailRepository getSpellDetailRepository();

	ZoneDetailRepository getZoneDetailRepository();

	NpcDetailRepository getNpcDetailRepository();

	FactionDetailRepository getFactionDetailRepository();

	StatPatternRepository getStatPatternRepository();

	SpellPatternRepository getSpellPatternRepository();

	ItemSpellRepository getItemSpellRepository();

	ScraperConfig getScraperConfig();

	ScraperDatafixes getScraperDatafixes();
}
