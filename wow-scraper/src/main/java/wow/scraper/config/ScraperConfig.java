package wow.scraper.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import wow.commons.model.pve.GameVersionId;
import wow.commons.model.pve.PhaseId;
import wow.commons.model.pve.Side;
import wow.commons.util.CollectionUtil;
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

	@Value("${game.version}")
	private GameVersionId gameVersion;

	@Value("${scraper.min.quality}")
	private WowheadItemQuality minQuality;

	@Value("${scraper.min.item.level}")
	private int minItemLevel;

	@Value("#{'${ignored.item.ids}'.split(',')}")
	private Set<Integer> ignoredItemIds;

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

	@Value("#{${phase.overrides}}")
	private Map<Integer, PhaseId> phaseOverrides;

	@Value("#{${required.side.overrides}}")
	private Map<Side, Set<Integer>> requiredSideOverrides;

	public Optional<Side> getRequiredSideOverride(int itemId) {
		return requiredSideOverrides.entrySet().stream()
				.filter(x -> x.getValue().contains(itemId))
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
}
