package wow.scraper.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Component;
import wow.commons.model.pve.GameVersionId;
import wow.scraper.fetchers.WowheadFetcher;
import wow.scraper.parsers.stats.StatPatternRepository;
import wow.scraper.repository.ItemDetailRepository;
import wow.scraper.repository.QuestInfoRepository;
import wow.scraper.repository.SpellDetailRepository;

/**
 * User: POlszewski
 * Date: 2023-06-20
 */
@Component
@AllArgsConstructor
@Getter
public class ScraperContextImpl implements ScraperContext {
	private final WowheadFetcher wowheadFetcher;
	private final ItemDetailRepository itemDetailRepository;
	private final SpellDetailRepository spellDetailRepository;
	private final QuestInfoRepository questInfoRepository;
	private final StatPatternRepository statPatternRepository;
	private final ScraperConfig scraperConfig;

	@Override
	public GameVersionId getGameVersion() {
		return scraperConfig.getGameVersion();
	}
}
