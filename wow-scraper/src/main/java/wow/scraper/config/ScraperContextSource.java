package wow.scraper.config;

import wow.scraper.fetchers.WowheadFetcher;
import wow.scraper.parsers.stats.StatPatternRepository;
import wow.scraper.repository.*;

/**
 * User: POlszewski
 * Date: 2023-06-20
 */
public interface ScraperContextSource extends ScraperContext {
	ScraperContext getScraperContext();

	@Override
	default WowheadFetcher getWowheadFetcher() {
		return getScraperContext().getWowheadFetcher();
	}

	@Override
	default ItemDetailRepository getItemDetailRepository() {
		return getScraperContext().getItemDetailRepository();
	}

	@Override
	default SpellDetailRepository getSpellDetailRepository() {
		return getScraperContext().getSpellDetailRepository();
	}

	@Override
	default QuestInfoRepository getQuestInfoRepository() {
		return getScraperContext().getQuestInfoRepository();
	}

	@Override
	default ZoneDetailRepository getZoneDetailRepository() {
		return getScraperContext().getZoneDetailRepository();
	}

	@Override
	default BossDetailRepository getBossDetailRepository() {
		return getScraperContext().getBossDetailRepository();
	}

	@Override
	default FactionDetailRepository getFactionDetailRepository() {
		return getScraperContext().getFactionDetailRepository();
	}

	@Override
	default StatPatternRepository getStatPatternRepository() {
		return getScraperContext().getStatPatternRepository();
	}

	@Override
	default ScraperConfig getScraperConfig() {
		return getScraperContext().getScraperConfig();
	}
}