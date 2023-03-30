package wow.scraper.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import wow.commons.model.pve.GameVersionId;
import wow.commons.model.pve.PhaseId;
import wow.scraper.model.WowheadItemQuality;

import java.util.List;
import java.util.Map;
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
}
