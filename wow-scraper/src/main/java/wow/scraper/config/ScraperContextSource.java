package wow.scraper.config;

import wow.commons.model.pve.GameVersionId;
import wow.scraper.parsers.stats.StatPatternRepository;
import wow.scraper.repository.ItemDetailRepository;
import wow.scraper.repository.QuestInfoRepository;
import wow.scraper.repository.SpellDetailRepository;
import wow.scraper.repository.WowheadFetcher;

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
	default StatPatternRepository getStatPatternRepository() {
		return getScraperContext().getStatPatternRepository();
	}

	@Override
	default ScraperConfig getScraperConfig() {
		return getScraperContext().getScraperConfig();
	}

	@Override
	default GameVersionId getGameVersion() {
		return getScraperContext().getGameVersion();
	}
}
