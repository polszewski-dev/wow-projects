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
public final class WowSimulatorSpringTestConfig {
}
