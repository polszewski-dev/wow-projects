package wow.commons.client.util;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import wow.commons.model.attribute.Attribute;
import wow.commons.model.attribute.AttributeCondition;
import wow.commons.model.attribute.AttributeId;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * User: POlszewski
 * Date: 2023-11-17
 */
class AttributeFormatterTest {
	@ParameterizedTest
	@CsvSource({
			"Strength,         ,            1 str",
			"Agility,          ,            1 agi",
			"Stamina,          ,            1 sta",
			"Intellect,        ,            1 int",
			"Spirit,           ,            1 spi",
			"ExpertiseRating,  ,            1 expertise",
			"Mp5,              ,            1 mp5",
			"DodgeRating,      ,            1 dodge",
			"DefenseRating,    ,            1 defense",
			"Block,            ,            1 block",
			"ParryRating,      ,            1 parry",
			"ResilienceRating, ,            1 resi",
			"Power,            Physical,    1 ap",
			"CritRating,       Physical,    1 crit",
			"HitRating,        Physical,    1 hit",
			"HasteRating,      Physical,    1 haste",
			"Power,            Spell,       1 sp",
			"Power,            SpellDamage, 1 sd",
			"Power,            Healing,     1 healing",
			"CritRating,       Spell,       1 crit",
			"HitRating,        Spell,       1 hit",
			"HasteRating,      Spell,       1 haste",
	})
	void format(String idStr, String condStr, String expected) {
		var id = AttributeId.parse(idStr);
		var condition = AttributeCondition.parse(condStr);
		var attribute = Attribute.of(id, 1, condition);

		var formatted = AttributeFormatter.format(attribute);

		assertThat(formatted).isEqualTo(expected);
	}
}