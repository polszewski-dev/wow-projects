package wow.scraper.config;

import wow.scraper.fetchers.WowheadFetcher;
import wow.scraper.parsers.stats.StatPatternRepository;
import wow.scraper.repository.*;

/**
 * User: POlszewski
 * Date: 2023-06-20
 */
public interface ScraperContext {
	WowheadFetcher getWowheadFetcher();

	ItemDetailRepository getItemDetailRepository();

	SpellDetailRepository getSpellDetailRepository();

	QuestInfoRepository getQuestInfoRepository();

	ZoneDetailRepository getZoneDetailRepository();

	BossDetailRepository getBossDetailRepository();

	FactionDetailRepository getFactionDetailRepository();

	StatPatternRepository getStatPatternRepository();

	ScraperConfig getScraperConfig();
}
