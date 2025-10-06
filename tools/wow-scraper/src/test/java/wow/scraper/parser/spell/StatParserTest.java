package wow.scraper.parser.spell;

import org.junit.jupiter.api.Test;
import wow.commons.model.attribute.Attribute;
import wow.commons.model.attribute.AttributeId;
import wow.commons.model.pve.GameVersionId;
import wow.scraper.ScraperSpringTest;
import wow.scraper.constant.AttributeConditions;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * User: POlszewski
 * Date: 2023-10-07
 */
class StatParserTest extends ScraperSpringTest {
	@Test
	void test() {
		var parser = parse("Reduces cast time on your Flamestrike ability by 0.25 sec.");

		assertThat(parser).isEqualTo(List.of(
				Attribute.of(AttributeId.CAST_TIME, -0.25, AttributeConditions.FLAMESTRIKE)
		));
	}

	private List<Attribute> parse(String line) {
		var parser = statPatternRepository.getItemStatParser(GameVersionId.TBC);
		boolean success = parser.tryParse(line);
		assertThat(success).isTrue();
		return parser.getParsedStats().list();
	}
}
