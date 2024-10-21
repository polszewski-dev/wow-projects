package wow.minmax.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import wow.commons.model.character.CharacterClassId;
import wow.commons.model.character.CreatureType;
import wow.commons.model.pve.GameVersionId;

import java.util.Comparator;
import java.util.List;

/**
 * User: POlszewski
 * Date: 2023-04-03
 */
@Configuration
@ConfigurationProperties("wow.minmax.profile")
@Getter
@Setter
public class ProfileConfig {
	private List<GameVersionId> supportedVersions;
	private List<CharacterClassId> supportedClasses;
	private CreatureType defaultEnemyType;
	private int defaultLevelDiff;

	public GameVersionId getLatestSupportedVersionId() {
		return supportedVersions.stream()
				.max(Comparator.naturalOrder())
				.orElseThrow();
	}
}
