package wow.minmax.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import wow.commons.model.character.*;
import wow.commons.model.professions.Profession;
import wow.commons.model.pve.Phase;

import java.util.List;
import java.util.stream.Collectors;

/**
 * User: POlszewski
 * Date: 2022-11-14
 */
@Configuration
@ConfigurationProperties("wow.minmax.profile")
@Getter
@Setter
public class ProfileConfig {
	private CharacterClass defaultClass;
	private Race defaultRace;
	private CreatureType defaultEnemyType;
	private BuildId defaultBuild;
	private List<Profession> defaultProfessions;

	public List<CharacterProfession> getDefaultProfessions(Phase phase) {
		return defaultProfessions.stream()
				.map(profession -> new CharacterProfession(profession, phase.getGameVersion().getMaxProfession(), null))
				.collect(Collectors.toList());
	}
}
