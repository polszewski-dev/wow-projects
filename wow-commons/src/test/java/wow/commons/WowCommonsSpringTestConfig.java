package wow.commons;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;

/**
 * User: POlszewski
 * Date: 2022-11-10
 */
@ComponentScan(basePackages = {
		"wow.commons"
})
@PropertySource("classpath:test.properties")
public class WowCommonsSpringTestConfig {
}
