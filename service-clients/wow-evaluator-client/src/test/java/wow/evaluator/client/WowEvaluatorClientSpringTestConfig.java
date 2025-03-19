package wow.evaluator.client;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;

/**
 * User: POlszewski
 * Date: 2022-11-12
 */
@ComponentScan(basePackages = {
		"wow.commons",
		"wow.character"
})
@PropertySource({
		"classpath:wow-commons.properties",
		"classpath:wow-character.properties"
})
public class WowEvaluatorClientSpringTestConfig {
}
