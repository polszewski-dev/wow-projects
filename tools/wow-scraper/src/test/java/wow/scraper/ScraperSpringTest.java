package wow.scraper;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import wow.scraper.config.ScraperContext;
import wow.scraper.repository.SpellPatternRepository;
import wow.scraper.repository.StatPatternRepository;

/**
 * User: POlszewski
 * Date: 2023-09-15
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = ScraperSpringTestConfig.class)
public abstract class ScraperSpringTest {
	@Autowired
	protected ScraperContext scraperContext;

	@Autowired
	protected StatPatternRepository statPatternRepository;

	@Autowired
	protected SpellPatternRepository spellPatternRepository;
}
