package wow.scraper.parsers.tooltip;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import wow.scraper.ScraperTestConfig;
import wow.scraper.model.JsonCommonDetails;
import wow.scraper.repository.SpellPatternRepository;
import wow.scraper.repository.StatPatternRepository;

import java.io.IOException;

/**
 * User: POlszewski
 * Date: 2022-12-02
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = ScraperTestConfig.class)
public abstract class TooltipParserTest<D extends JsonCommonDetails, T extends AbstractTooltipParser<D>> {
	@Autowired
	StatPatternRepository statPatternRepository;

	@Autowired
	SpellPatternRepository spellPatternRepository;

	static final ObjectMapper MAPPER = new ObjectMapper();

	T getTooltip(String path) throws IOException {
		D data = MAPPER.readValue(
				TooltipParserTest.class.getResourceAsStream("/tooltips/" + path),
				getDetailsClass()
		);
		T parser = createParser(data);
		parser.parse();
		return parser;
	}

	protected abstract T createParser(D data);

	protected abstract Class<D> getDetailsClass();
}
