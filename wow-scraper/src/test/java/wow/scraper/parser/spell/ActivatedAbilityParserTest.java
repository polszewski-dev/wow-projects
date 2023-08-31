package wow.scraper.parser.spell;

import org.junit.jupiter.api.Test;
import wow.commons.model.Duration;
import wow.commons.model.attribute.Attribute;
import wow.commons.model.attribute.condition.MiscCondition;
import wow.commons.model.pve.GameVersionId;
import wow.commons.model.spell.ActivatedAbility;
import wow.commons.model.spell.SpellTarget;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.attribute.primitive.PrimitiveAttributeId.*;

/**
 * User: POlszewski
 * Date: 2023-09-23
 */
class ActivatedAbilityParserTest extends SpellParserTest {
	@Test
	void buff1() {
		var spell = parse("Use: Increases damage done by magical spells and effects by up to 50, and decreases the magical resistances of your spell targets by 100 for 30 sec. (3 Min Cooldown)");

		assertEffectApplication(spell, SpellTarget.SELF, 30, 1, 1, 1);

		var effect = spell.getEffectApplication().effect();

		assertModifier(effect, List.of(
				Attribute.of(POWER, 50, MiscCondition.SPELL_DAMAGE),
				Attribute.of(PENETRATION, 100, MiscCondition.SPELL)
		));

		assertDuration(spell.getCooldown(), Duration.minutes(3));
	}

	@Test
	void buff2() {
		var spell = parse("Use: Increases your spell damage by up to 100 and your healing by up to 190 for 15 sec. (1 Min, 15 Sec Cooldown)");

		assertEffectApplication(spell, SpellTarget.SELF, 15, 1, 1, 1);

		var effect = spell.getEffectApplication().effect();

		assertModifier(effect, List.of(
				Attribute.of(POWER, 100, MiscCondition.SPELL_DAMAGE),
				Attribute.of(POWER, 190, MiscCondition.HEALING)
		));
		assertDuration(spell.getCooldown(), 75);
	}

	@Test
	void buff3() {
		var spell = parse("Use: Increases spell damage and healing by up to 250, and increases mana cost of spells by 20% for 20 sec. (5 Min Cooldown)");

		assertEffectApplication(spell, SpellTarget.SELF, 20, 1, 1, 1);

		var effect = spell.getEffectApplication().effect();

		assertModifier(effect, List.of(
				Attribute.of(POWER, 250, MiscCondition.SPELL),
				Attribute.of(MANA_COST_PCT, 20)
		));
		assertDuration(spell.getCooldown(), Duration.minutes(5));
	}

	private ActivatedAbility parse(String line) {
		var parser = spellPatternRepository.getActivatedAbilityParser(GameVersionId.TBC);
		boolean success = parser.tryParse(line);
		assertThat(success).isTrue();
		return parser.getSuccessfulMatcher().orElseThrow().getActivatedAbility();
	}
}
