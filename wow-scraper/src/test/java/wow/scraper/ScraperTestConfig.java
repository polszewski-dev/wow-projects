package wow.scraper;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;

/**
 * User: POlszewski
 * Date: 2022-12-02
 */
@ComponentScan(basePackages = {
		"wow.scraper"
})
@PropertySource("classpath:test.properties")
public class ScraperTestConfig {
}
