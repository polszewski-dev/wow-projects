package wow.scraper.parser.spell;

import wow.commons.model.AnyDuration;
import wow.commons.model.Duration;
import wow.commons.model.Percent;
import wow.commons.model.attribute.Attribute;
import wow.commons.model.attribute.AttributeId;
import wow.commons.model.attribute.condition.AttributeCondition;
import wow.commons.model.effect.Effect;
import wow.commons.model.effect.component.*;
import wow.commons.model.spell.*;
import wow.commons.model.spell.component.DirectComponent;
import wow.scraper.ScraperSpringTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

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

	static void assertDirectComponent(DirectComponent direct, ComponentType type, double coeff, SpellSchool school, int min, int max) {
		assertThat(direct.type()).isEqualTo(type);
		assertCoefficient(coeff, school, direct.coefficient());
		assertThat(direct.min()).isEqualTo(min);
		assertThat(direct.max()).isEqualTo(max);
	}

	static void assertDirectComponent(DirectComponent direct, ComponentType type, double coeff, SpellSchool school, int min, int max, int minBonus, int maxBonus, AbilityId bonusRequiredEffect) {
		assertDirectComponent(direct, type, coeff, school, min, max);
		assertThat(direct.bonus()).isNotNull();
		assertThat(direct.bonus().min()).isEqualTo(minBonus);
		assertThat(direct.bonus().max()).isEqualTo(maxBonus);
		assertThat(direct.bonus().requiredEffect()).isEqualTo(bonusRequiredEffect);
	}

	static void assertPeriodicComponent(PeriodicComponent periodic, ComponentType type, double tickCoeff, SpellSchool school, int amount, int numTicks, double tickInterval, TickScheme tickScheme) {
		assertThat(periodic.type()).isEqualTo(type);
		assertCoefficient(tickCoeff, school, periodic.coefficient());
		assertThat(periodic.amount()).isEqualTo(amount);
		assertThat(periodic.numTicks()).isEqualTo(numTicks);
		assertThat(periodic.tickInterval().getSeconds()).isEqualTo(tickInterval);
		assertThat(periodic.tickScheme()).isEqualTo(tickScheme);
	}

	static void assertDuration(AnyDuration duration, double seconds) {
		assertDuration(duration, Duration.seconds(seconds));
	}

	static void assertDuration(AnyDuration duration, Duration expectedDuration) {
		assertThat(duration).isEqualTo(expectedDuration);
	}

	static void assertEffectApplication(Spell spell, SpellTarget target, int duration, int numCharges, int numStacks, int maxStacks) {
		var effectApplication = spell.getEffectApplication();

		assertThat(effectApplication).isNotNull();
		assertThat(effectApplication.target()).isEqualTo(target);
		assertThat(effectApplication.duration()).isEqualTo(Duration.seconds(duration));
		assertThat(effectApplication.numCharges()).isEqualTo(numCharges);
		assertThat(effectApplication.numStacks()).isEqualTo(numStacks);
		assertThat(effectApplication.effect()).isNotNull();
		assertThat(effectApplication.effect().getMaxStacks()).isEqualTo(maxStacks);
	}

	static void assertModifier(Effect effect, List<Attribute> attributes) {
		assertThat(effect.getModifierAttributeList()).isNotNull();
		assertThat(effect.getModifierAttributeList()).isEqualTo(attributes);
	}

	static void assertEvent(Event event, List<EventType> types, AttributeCondition condition, double chance, List<EventAction> actions, Duration cooldown) {
		assertThat(event.types()).isEqualTo(types);
		assertThat(event.condition()).isEqualTo(condition);
		assertThat(event.chance()).isEqualTo(Percent.of(chance));
		assertThat(event.actions()).isEqualTo(actions);
		if (event.triggeredSpell() != null) {
			assertThat(event.triggeredSpell().getCooldown()).isEqualTo(cooldown);
		}
	}

	static void assertStatConversion(Effect effect, int idx, AttributeId from, AttributeId to, AttributeCondition toCondition, int ratio) {
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