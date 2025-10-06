package wow.scraper.parser.spell;

import org.junit.jupiter.api.Test;
import wow.commons.model.Duration;
import wow.commons.model.attribute.Attribute;
import wow.commons.model.attribute.AttributeCondition;
import wow.commons.model.effect.Effect;
import wow.commons.model.effect.component.EventAction;
import wow.commons.model.effect.component.EventType;
import wow.commons.model.pve.GameVersionId;
import wow.commons.model.spell.SpellTarget;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.attribute.AttributeId.POWER;
import static wow.commons.model.effect.component.ComponentType.MANA_GAIN;
import static wow.commons.model.effect.component.EventType.*;
import static wow.scraper.constant.AttributeConditions.SPELL_DAMAGE;
import static wow.scraper.constant.AttributeConditions.*;

/**
 * User: POlszewski
 * Date: 2023-09-23
 */
class ProcParserTest extends SpellParserTest {
	@Test
	void singleTrigger() {
		var effect = parse("Equip: Your healing and damage spells have a chance to increase your healing by up to 175 and damage by up to 59 for 10 secs. (Proc chance: 10%, 1m cooldown)");

		assertThat(effect.getEvents()).hasSize(1);

		var procTrigger = effect.getEvents().getFirst();

		assertEvent(
				procTrigger,
				List.of(SPELL_HEAL, SPELL_HIT),
				AttributeCondition.comma(
						SPELL_DAMAGE,
						HEALING
				),
				10,
				List.of(EventAction.TRIGGER_SPELL),
				Duration.minutes(1)
		);

		var triggeredSpell = procTrigger.triggeredSpell();

		assertEffectApplication(triggeredSpell, SpellTarget.SELF, 10, 1, 1, 1);

		var triggeredEffect = triggeredSpell.getEffectApplication().effect();

		assertModifier(triggeredEffect, List.of(
				Attribute.of(POWER, 175, HEALING),
				Attribute.of(POWER, 59, SPELL_DAMAGE)
		));
	}

	@Test
	void twoTriggers() {
		var effect = parse("Equip: Each time you deal melee or ranged damage to an opponent, you gain 6 attack power for the next 10 sec., stacking up to 20 times.  Each time you land a harmful spell on an opponent, you gain 8 spell damage for the next 10 sec., stacking up to 10 times.");

		assertThat(effect.getEvents()).hasSize(2);

		var procTrigger1 = effect.getEvents().get(0);
		var procTrigger2 = effect.getEvents().get(1);

		assertEvent(
				procTrigger1,
				List.of(PHYSICAL_HIT),
				AttributeCondition.EMPTY,
				100,
				List.of(EventAction.TRIGGER_SPELL),
				Duration.ZERO
		);

		assertEvent(
				procTrigger2,
				List.of(SPELL_HIT),
				AttributeCondition.IS_HOSTILE_SPELL,
				100,
				List.of(EventAction.TRIGGER_SPELL),
				Duration.ZERO
		);

		assertEffectApplication(procTrigger1.triggeredSpell(), SpellTarget.SELF, 10, 1, 1, 20);
		assertEffectApplication(procTrigger2.triggeredSpell(), SpellTarget.SELF, 10, 1, 1, 10);

		var effect1 = procTrigger1.triggeredSpell().getEffectApplication().effect();
		var effect2 = procTrigger2.triggeredSpell().getEffectApplication().effect();

		assertModifier(effect1, List.of(Attribute.of(POWER, 6, PHYSICAL)));
		assertModifier(effect2, List.of(Attribute.of(POWER, 8, SPELL_DAMAGE)));
	}

	@Test
	void threeTriggersDifferentChances() {
		var effect = parse("Equip: Lesser Healing Wave has a 10% chance to grant 170 mana, Lightning Bolt has a 15% chance to grant up to 170 mana, and Stormstrike has a 50% chance to grant up to 275 attack power for 10 sec.");

		assertThat(effect.getEvents()).hasSize(3);

		var procTrigger1 = effect.getEvents().get(0);
		var procTrigger2 = effect.getEvents().get(1);
		var procTrigger3 = effect.getEvents().get(2);

		assertEvent(
				procTrigger1,
				List.of(SPELL_CAST),
				LESSER_HEALING_WAVE,
				10,
				List.of(EventAction.TRIGGER_SPELL),
				Duration.ZERO
		);

		assertEvent(
				procTrigger2,
				List.of(SPELL_HIT),
				LIGHTNING_BOLT,
				15,
				List.of(EventAction.TRIGGER_SPELL),
				Duration.ZERO
		);

		assertEvent(
				procTrigger3,
				List.of(EventType.SPELL_DAMAGE),
				STORMSTRIKE,
				50,
				List.of(EventAction.TRIGGER_SPELL),
				Duration.ZERO
		);

		var direct1 = procTrigger1.triggeredSpell().getDirectComponents().getFirst();
		var direct2 = procTrigger2.triggeredSpell().getDirectComponents().getFirst();

		assertDirectComponent(direct1, MANA_GAIN, 0, null, 170, 170);
		assertDirectComponent(direct2, MANA_GAIN, 0, null, 170, 170);

		assertEffectApplication(procTrigger3.triggeredSpell(), SpellTarget.SELF, 10, 1, 1, 1);

		var effect3 = procTrigger3.triggeredSpell().getEffectApplication().effect();

		assertModifier(effect3, List.of(
				Attribute.of(POWER, 275, PHYSICAL)
		));
	}

	private Effect parse(String line) {
		var parser = spellPatternRepository.getProcParser(GameVersionId.TBC);
		boolean success = parser.tryParse(line);
		assertThat(success).isTrue();
		return parser.getSuccessfulMatcher().orElseThrow().getEffect();
	}
}
