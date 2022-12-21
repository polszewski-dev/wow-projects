package wow.character;

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
@PropertySource("classpath:test.properties")
public class WowCharacterSpringTestConfig {
}
