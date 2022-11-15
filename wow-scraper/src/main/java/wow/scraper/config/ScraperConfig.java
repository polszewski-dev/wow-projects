package wow.scraper.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import wow.commons.model.pve.GameVersion;
import wow.scraper.model.WowheadItemQuality;

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
	@Value("${game.version}")
	private GameVersion gameVersion;

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
}
