package wow.scraper.parsers.tooltip;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import wow.scraper.ScraperTestConfig;
import wow.scraper.model.JsonItemDetailsAndTooltip;
import wow.scraper.parsers.stats.StatPatternRepository;

import java.io.IOException;

/**
 * User: POlszewski
 * Date: 2022-12-02
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = ScraperTestConfig.class)
public abstract class TooltipParserTest<T extends AbstractTooltipParser> {
	@Autowired
	StatPatternRepository statPatternRepository;

	static final ObjectMapper MAPPER = new ObjectMapper();

	T getTooltip(String path) throws IOException {
		JsonItemDetailsAndTooltip data = MAPPER.readValue(
				ItemTooltipParserTest.class.getResourceAsStream("/tooltips/" + path),
				JsonItemDetailsAndTooltip.class
		);
		T parser = createParser(data);
		parser.parse();
		return parser;
	}

	protected abstract T createParser(JsonItemDetailsAndTooltip data);
}
