package wow.scraper.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Component;
import wow.scraper.fetchers.WowheadFetcher;
import wow.scraper.repository.*;

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
	private final SpellPatternRepository spellPatternRepository;
	private final ZoneDetailRepository zoneDetailRepository;
	private final NpcDetailRepository npcDetailRepository;
	private final FactionDetailRepository factionDetailRepository;
	private final ScraperConfig scraperConfig;
}
