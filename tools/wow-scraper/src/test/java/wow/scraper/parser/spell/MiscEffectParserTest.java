package wow.scraper.parser.spell;

import org.junit.jupiter.api.Test;
import wow.commons.model.attribute.Attribute;
import wow.commons.model.attribute.condition.MiscCondition;
import wow.commons.model.effect.Effect;
import wow.commons.model.pve.GameVersionId;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.attribute.AttributeId.*;
import static wow.scraper.constant.AbilityIds.BATTLE_SHOUT;
import static wow.scraper.constant.AbilityIds.LIGHTNING_SHIELD;

/**
 * User: POlszewski
 * Date: 2023-09-28
 */
class MiscEffectParserTest extends SpellParserTest {
	@Test
	void augment1() {
		var effect = parse("Your Lightning Shield spell also grants you 15 mana per 5 sec. while active.");

		assertThat(effect.getAugmentedAbilities()).isEqualTo(List.of(LIGHTNING_SHIELD));

		assertModifier(effect, List.of(
				Attribute.of(MP5, 15)
		));
	}

	@Test
	void augment2() {
		var effect = parse("Your Battle Shout ability grants an additional 70 attack power.");

		assertThat(effect.getAugmentedAbilities()).isEqualTo(List.of((BATTLE_SHOUT)));

		assertModifier(effect, List.of(
				Attribute.of(POWER, 70, MiscCondition.PHYSICAL)
		));
	}

	@Test
	void statConversion() {
		var effect = parse("Increases healing by up to 10% of your total Intellect.");

		assertStatConversion(effect, 0, INTELLECT, POWER, MiscCondition.HEALING, 10);
	}

	private Effect parse(String line) {
		var parser = spellPatternRepository.getMiscEffectParser(GameVersionId.TBC);
		boolean success = parser.tryParse(line);
		assertThat(success).isTrue();
		return parser.getSuccessfulMatcher().orElseThrow().getEffect();
	}
}
