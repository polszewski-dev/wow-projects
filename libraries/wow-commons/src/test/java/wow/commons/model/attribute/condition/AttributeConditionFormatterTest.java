package wow.commons.model.attribute.condition;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * User: POlszewski
 * Date: 2025-10-05
 */
class AttributeConditionFormatterTest {
	@ParameterizedTest
	@ValueSource(strings = {
			"",
			"Smite, Holy Fire, Prayer of Healing, Holy Nova",
			"SpellDamage & Shadow, Fire, Frost",
			"Physical & OwnerHealthBelow70%",
			"Create Firestone (Greater)",
			"Power Word: Shield"
	})
	void format(String expectedValue) {
		var condition = new AttributeConditionParser(expectedValue).parse();

		var actualValue = AttributeConditionFormatter.format(condition);

		assertThat(actualValue).isEqualTo(expectedValue);
	}
}