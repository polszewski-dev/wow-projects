package wow.minmax.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * User: POlszewski
 * Date: 2022-11-14
 */
@Configuration
@ConfigurationProperties("wow.minmax.upgrade")
@Getter
@Setter
public class UpgradeConfig {
	private int maxUpgrades;
}
