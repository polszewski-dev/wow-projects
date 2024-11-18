package wow.minmax.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import wow.simulator.client.dto.RngType;

/**
 * User: POlszewski
 * Date: 2024-11-18
 */
@Configuration
@ConfigurationProperties("wow.minmax.simulation")
@Getter
@Setter
public class SimulationConfig {
	private int duration;
	private RngType rngType;
}
