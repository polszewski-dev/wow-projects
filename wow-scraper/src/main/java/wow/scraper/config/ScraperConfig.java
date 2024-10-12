package wow.scraper.config;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import wow.commons.model.pve.GameVersionId;
import wow.scraper.model.WowheadItemQuality;

import java.util.List;
import java.util.Map;

/**
 * User: POlszewski
 * Date: 2022-11-15
 */
@PropertySource("classpath:scraper.properties")
@Component
@NoArgsConstructor
@Getter
@Setter
public class ScraperConfig {
	@Value("${directory.path}")
	private String directoryPath;

	@Value("${error.on.unmatched.line}")
	private boolean errorOnUnmatchedLine;

	@Value("#{'${game.versions}'.split(',')}")
	private List<GameVersionId> gameVersions;

	@Value("#{${scraper.min.quality}}")
	private Map<GameVersionId, WowheadItemQuality> minQuality;

	@Value("#{${scraper.min.item.level}}")
	private Map<GameVersionId, Integer> minItemLevel;

	public WowheadItemQuality getMinQuality(GameVersionId gameVersion) {
		return minQuality.get(gameVersion);
	}

	public int getMinItemLevel(GameVersionId gameVersion) {
		return minItemLevel.get(gameVersion);
	}
}
