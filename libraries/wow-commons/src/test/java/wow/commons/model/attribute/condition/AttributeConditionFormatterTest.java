package wow.commons.model.attribute.condition;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.attribute.condition.AttributeConditionFormatter.formatCondition;
import static wow.commons.model.attribute.condition.AttributeConditionParser.parseCondition;

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
		var condition = parseCondition(expectedValue);
		var actualValue = formatCondition(condition);

		assertThat(actualValue).isEqualTo(expectedValue);
	}
}