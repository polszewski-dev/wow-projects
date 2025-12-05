package wow.scraper.parser.spell;

import wow.commons.model.AnyDuration;
import wow.commons.model.Duration;
import wow.commons.model.Percent;
import wow.commons.model.attribute.Attribute;
import wow.commons.model.attribute.AttributeId;
import wow.commons.model.effect.Effect;
import wow.commons.model.effect.component.*;
import wow.commons.model.spell.*;
import wow.scraper.ScraperSpringTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.spell.component.ComponentCommand.ApplyEffect;
import static wow.test.commons.TestConstants.PRECISION;

/**
 * User: POlszewski
 * Date: 2023-08-29
 */
abstract class SpellParserTest extends ScraperSpringTest {
	static void assertCost(Cost cost, int amount, ResourceType resourceType, double baseStatPct, double coeff, SpellSchool school) {
		assertThat(cost.amount()).isEqualTo(amount);
		assertThat(cost.resourceType()).isEqualTo(resourceType);
		assertThat(cost.baseStatPct()).isEqualTo(Percent.of(baseStatPct));
		assertCoefficient(coeff, school, cost.coefficient());
	}

	static void assertTickInterval(PeriodicComponent periodic, double tickInterval) {
		assertThat(periodic.tickInterval()).isEqualTo(Duration.seconds(tickInterval));
	}

	static void assertDuration(AnyDuration duration, double seconds) {
		assertDuration(duration, Duration.seconds(seconds));
	}

	static void assertDuration(AnyDuration duration, Duration expectedDuration) {
		assertThat(duration).isEqualTo(expectedDuration);
	}

	static void assertEffectApplication(Spell spell, SpellTarget target, int duration, int numCharges, int numStacks, int maxStacks) {
		var command = spell.getApplyEffectCommands().getFirst();

		assertApplyEffectCommand(command, target, duration, numCharges, numStacks, maxStacks);
	}

	static void assertApplyEffectCommand(ApplyEffect command, SpellTarget target, int duration, int numCharges, int numStacks, int maxStacks) {
		assertThat(command.target()).isEqualTo(target);
		assertThat(command.duration()).isEqualTo(Duration.seconds(duration));
		assertThat(command.numCharges()).isEqualTo(numCharges);
		assertThat(command.numStacks()).isEqualTo(numStacks);
		assertThat(command.effect()).isNotNull();
		assertThat(command.effect().getMaxStacks()).isEqualTo(maxStacks);
	}

	static void assertModifier(Effect effect, List<Attribute> attributes) {
		assertThat(effect.getModifierAttributeList()).isNotNull();
		assertThat(effect.getModifierAttributeList()).isEqualTo(attributes);
	}

	static void assertEvent(Event event, List<EventType> types, EventCondition condition, double chance, List<EventAction> actions, Duration cooldown) {
		assertThat(event.types()).isEqualTo(types);
		assertThat(event.condition()).isEqualTo(condition);
		assertThat(event.chance()).isEqualTo(Percent.of(chance));
		assertThat(event.actions()).isEqualTo(actions);
		if (event.triggeredSpell() != null) {
			assertThat(event.triggeredSpell().getCooldown()).isEqualTo(cooldown);
		}
	}

	static void assertStatConversion(Effect effect, int idx, AttributeId from, AttributeId to, StatConversionCondition toCondition, int ratio) {
		assertThat(effect.getStatConversions()).hasSizeGreaterThan(idx);

		var statConversion = effect.getStatConversions().get(idx);

		assertThat(statConversion.from()).isEqualTo(from);
		assertThat(statConversion.to()).isEqualTo(to);
		assertThat(statConversion.toCondition()).isEqualTo(toCondition);
		assertThat(statConversion.ratioPct()).isEqualTo(Percent.of(ratio));
	}

	static void assertCoefficient(double tickCoeff, SpellSchool school, Coefficient coefficient) {
		assertThat(coefficient.value().getCoefficient()).isEqualTo(Percent.of(tickCoeff).getCoefficient(), PRECISION);
		assertThat(coefficient.school()).isEqualTo(school);
	}
}