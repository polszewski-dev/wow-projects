package wow.minmax.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * User: POlszewski
 * Date: 2022-11-14
 */
@Profile("dev")
@Configuration
@ConfigurationProperties("wow.minmax.file.repo")
@Getter
@Setter
public class FileRepoConfig {
	private String directory;
}
