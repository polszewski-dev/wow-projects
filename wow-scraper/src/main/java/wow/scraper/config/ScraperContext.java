package wow.scraper.config;

import wow.scraper.fetchers.WowheadFetcher;
import wow.scraper.parsers.stats.StatPatternRepository;
import wow.scraper.repository.ItemDetailRepository;
import wow.scraper.repository.QuestInfoRepository;
import wow.scraper.repository.SpellDetailRepository;

/**
 * User: POlszewski
 * Date: 2023-06-20
 */
public interface ScraperContext {
	WowheadFetcher getWowheadFetcher();

	ItemDetailRepository getItemDetailRepository();

	SpellDetailRepository getSpellDetailRepository();

	QuestInfoRepository getQuestInfoRepository();

	StatPatternRepository getStatPatternRepository();

	ScraperConfig getScraperConfig();
}
