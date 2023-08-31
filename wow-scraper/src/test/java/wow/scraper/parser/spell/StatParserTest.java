package wow.scraper.parser.spell;

import org.junit.jupiter.api.Test;
import wow.commons.model.attribute.Attribute;
import wow.commons.model.attribute.condition.AttributeCondition;
import wow.commons.model.attribute.primitive.PrimitiveAttribute;
import wow.commons.model.attribute.primitive.PrimitiveAttributeId;
import wow.commons.model.pve.GameVersionId;
import wow.commons.model.spell.AbilityId;
import wow.scraper.ScraperSpringTest;

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
				Attribute.of(PrimitiveAttributeId.CAST_TIME, -0.25, AttributeCondition.of(AbilityId.FLAMESTRIKE))
		));
	}

	private List<PrimitiveAttribute> parse(String line) {
		var parser = statPatternRepository.getItemStatParser(GameVersionId.TBC);
		boolean success = parser.tryParse(line);
		assertThat(success).isTrue();
		return parser.getParsedStats().primitiveAttributes();
	}
}
