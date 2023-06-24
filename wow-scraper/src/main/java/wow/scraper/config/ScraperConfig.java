package wow.scraper.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import wow.commons.model.pve.GameVersionId;
import wow.commons.model.pve.PhaseId;
import wow.commons.model.pve.Side;
import wow.commons.util.CollectionUtil;
import wow.scraper.fetchers.PageCache;
import wow.scraper.fetchers.PageFetcher;
import wow.scraper.fetchers.impl.CachedPageFetcher;
import wow.scraper.fetchers.impl.WebPageFetcher;
import wow.scraper.model.WowheadItemQuality;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * User: POlszewski
 * Date: 2022-11-15
 */
@PropertySource("classpath:scraper.properties")
@Component
@Data
public class ScraperConfig {
	@Value("${directory.path}")
	private String directoryPath;

	@Value("#{'${game.versions}'.split(',')}")
	private List<GameVersionId> gameVersions;

	@Value("#{${scraper.min.quality}}")
	private Map<GameVersionId, WowheadItemQuality> minQuality;

	@Value("#{${scraper.min.item.level}}")
	private Map<GameVersionId, Integer> minItemLevel;

	@Value("#{'${ignored.item.ids}'.split(',')}")
	private Set<Integer> ignoredItemIds;

	@Value("#{'${ignored.spell.ids}'.split(',')}")
	private Set<Integer> ignoredSpellIds;

	@Value("#{'${ignored.boss.ids}'.split(',')}")
	private Set<Integer> ignoredBossIds;

	@Value("#{'${ignored.zone.ids}'.split(',')}")
	private Set<Integer> ignoredZoneIds;

	@Value("#{${dungeon.short.names}}")
	private Map<String, String> dungeonShortNames;

	@Value("#{${pvp.item.name.parts}}")
	private List<String> pvpItemNameParts;

	@Value("#{${pvp.item.ids}}")
	private Set<Integer> pvpItemIds;

	@Value("#{'${world.drop.overrides}'.split(',')}")
	private List<Integer> worldDropOverrides;

	@Value("#{${boss.drop.overrides}}")
	private Map<Integer, List<Integer>> bossDropOverrides;

	@Value("#{${token.to.traded.for}}")
	private Map<Integer, List<Integer>> tokenToTradedFor;

	@Value("#{${item.starting.quest.to.traded.for}}")
	private Map<Integer, List<Integer>> itemStartingQuestToTradedFor;

	@Value("#{${item.phase.overrides}}")
	private Map<Integer, PhaseId> itemPhaseOverrides;

	@Value("#{${spell.phase.overrides}}")
	private Map<Integer, PhaseId> spellPhaseOverrides;

	@Value("#{${item.required.side.overrides}}")
	private Map<Side, Set<Integer>> itemRequiredSideOverrides;

	@Value("#{${spell.required.side.overrides}}")
	private Map<Side, Set<Integer>> spellRequiredSideOverrides;

	public Optional<Side> getItemRequiredSideOverride(int itemId) {
		return getSide(itemRequiredSideOverrides, itemId);
	}

	public Optional<Side> getSpellRequiredSideOverride(int spellId) {
		return getSide(spellRequiredSideOverrides, spellId);
	}

	private Optional<Side> getSide(Map<Side, Set<Integer>> map, int id) {
		return map.entrySet().stream()
				.filter(x -> x.getValue().contains(id))
				.map(Map.Entry::getKey)
				.collect(CollectionUtil.toOptionalSingleton());
	}

	@Value("#{${alliance.only.factions}}")
	private Set<String> allianceOnlyFactions;

	@Value("#{${horde.only.factions}}")
	private Set<String> hordeOnlyFactions;

	public Side getRequiredSideFromFaction(String faction) {
		if (allianceOnlyFactions.contains(faction)) {
			return Side.ALLIANCE;
		}

		if (hordeOnlyFactions.contains(faction)) {
			return Side.HORDE;
		}

		return null;
	}

	public WowheadItemQuality getMinQuality(GameVersionId gameVersion) {
		return minQuality.get(gameVersion);
	}

	public int getMinItemLevel(GameVersionId gameVersion) {
		return minItemLevel.get(gameVersion);
	}

	@Bean
	public PageFetcher pageFetcher(PageCache pageCache) {
		return new CachedPageFetcher(new WebPageFetcher(), pageCache);
	}
}
