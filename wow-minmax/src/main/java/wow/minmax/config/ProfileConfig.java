package wow.minmax.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import wow.commons.model.professions.Profession;
import wow.commons.model.pve.Phase;
import wow.commons.model.unit.CharacterClass;
import wow.commons.model.unit.CharacterProfession;
import wow.commons.model.unit.CreatureType;
import wow.commons.model.unit.Race;

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
	private String defaultBuild;
	private List<Profession> defaultProfessions;

	public List<CharacterProfession> getDefaultProfessions(Phase phase) {
		return defaultProfessions.stream()
				.map(profession -> new CharacterProfession(profession, phase.getGameVersion().getMaxProfession(), null))
				.collect(Collectors.toList());
	}
}