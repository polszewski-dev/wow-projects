package wow.simulator.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * User: POlszewski
 * Date: 2023-08-08
 */
@Component
@ComponentScan(basePackages = {
		"wow.commons",
		"wow.character"
})
@PropertySource("classpath:simulator.properties")
@Getter
@Setter
public class SimulatorConfig {
}
