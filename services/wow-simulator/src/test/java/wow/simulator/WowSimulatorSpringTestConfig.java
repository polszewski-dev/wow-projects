package wow.simulator;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;

/**
 * User: POlszewski
 * Date: 2023-08-09
 */
@ComponentScan(basePackages = {
		"wow.commons",
		"wow.character",
		"wow.simulator"
})
@PropertySource({
		"classpath:wow-commons.properties",
		"classpath:wow-character.properties",
		"classpath:wow-simulator.properties"
})
public final class WowSimulatorSpringTestConfig {
}
