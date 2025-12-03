package wow.commons;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import wow.commons.model.Duration;
import wow.commons.model.Percent;
import wow.commons.model.attribute.Attribute;
import wow.commons.model.attribute.AttributeCondition;
import wow.commons.model.attribute.AttributeId;
import wow.commons.model.attribute.Attributes;
import wow.commons.model.effect.Effect;
import wow.commons.model.effect.EffectSource;
import wow.commons.model.effect.component.*;
import wow.commons.model.item.AbstractItem;
import wow.commons.model.item.Enchant;
import wow.commons.model.spell.*;
import wow.commons.model.talent.Talent;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.spell.component.ComponentCommand.ApplyEffect;
import static wow.commons.model.spell.component.ComponentCommand.PeriodicCommand;
import static wow.test.commons.TestConstants.PRECISION;

/**
 * User: POlszewski
 * Date: 2022-12-19
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = WowCommonsSpringTestConfig.class)
public abstract class WowCommonsSpringTest {
	protected static void assertEffectApplication(Spell spell, SpellTarget target, int duration, int numCharges, int numStacks, int maxStacks) {
		var command = spell.getApplyEffectCommands().getFirst();

		assertApplyEffectCommand(command, target, duration, numCharges, numStacks, maxStacks);
	}

	protected static void assertApplyEffectCommand(ApplyEffect command, SpellTarget target, int duration, int numCharges, int numStacks, int maxStacks) {
		assertThat(command.target()).isEqualTo(target);
		assertThat(command.duration()).isEqualTo(Duration.seconds(duration));
		assertThat(command.numCharges()).isEqualTo(numCharges);
		assertThat(command.numStacks()).isEqualTo(numStacks);
		assertThat(command.effect()).isNotNull();
		assertThat(command.effect().getMaxStacks()).isEqualTo(maxStacks);
	}

	protected static void assertPeriodicComponent(PeriodicComponent periodic, ComponentType type, double coeff, SpellSchool school, int amount, int numTicks, double tickInterval, TickScheme tickScheme) {
		var command = periodic.commands().getFirst();

		assertPeriodicCommand(command, type, coeff, school, amount, numTicks, tickInterval, tickScheme);
		assertThat(periodic.tickInterval().getSeconds()).isEqualTo(tickInterval);
	}

	protected static void assertPeriodicCommand(PeriodicCommand command, ComponentType type, double coeff, SpellSchool school, int amount, int numTicks, double tickInterval, TickScheme tickScheme) {
		assertThat(command.type()).isEqualTo(type);
		assertCoefficient(coeff, school, command.coefficient());
		assertThat(command.amount()).isEqualTo(amount);
		assertThat(command.numTicks()).isEqualTo(numTicks);

		assertThat(command.tickScheme()).isEqualTo(tickScheme);
	}

	protected static void assertModifier(Effect effect, List<Attribute> attributes) {
		assertThat(effect.getModifierAttributeList()).isNotNull();
		assertThat(effect.getModifierAttributeList()).isEqualTo(attributes);
	}

	protected static void assertStatConversion(Effect effect, int idx, AttributeId from, AttributeId to, int ratio, StatConversionCondition toCondition) {
		assertThat(effect.getStatConversions()).hasSizeGreaterThan(idx);

		var statConversion = effect.getStatConversions().get(idx);

		assertThat(statConversion.from()).isEqualTo(from);
		assertThat(statConversion.to()).isEqualTo(to);
		assertThat(statConversion.toCondition()).isEqualTo(toCondition);
		assertThat(statConversion.ratioPct()).isEqualTo(Percent.of(ratio));
	}

	protected static void assertEvent(Event event, List<EventType> types, EventCondition condition, double chance, List<EventAction> actions, Duration cooldown) {
		assertThat(event.types()).isEqualTo(types);
		assertThat(event.condition()).isEqualTo(condition);
		assertThat(event.chance()).isEqualTo(Percent.of(chance));
		assertThat(event.actions()).isEqualTo(actions);
		if (event.triggeredSpell() != null) {
			assertThat(event.triggeredSpell().getCooldown()).isEqualTo(cooldown);
		}
	}

	protected static void assertCoefficient(double tickCoeff, SpellSchool school, Coefficient coefficient) {
		assertThat(coefficient.value().getCoefficient()).isEqualTo(Percent.of(tickCoeff).getCoefficient(), PRECISION);
		assertThat(coefficient.school()).isEqualTo(school);
	}

	protected static void assertEffect(Effect effect, AttributeId id, int value, String tooltip, EffectSource source) {
		assertEffect(effect, id, value, AttributeCondition.EMPTY, tooltip, source);
	}

	protected static void assertEffect(Effect effect, AttributeId id, int value, AttributeCondition condition, String tooltip, EffectSource source) {
		assertEffect(effect, Attributes.of(id, value, condition), tooltip, source);
	}

	protected static void assertEffect(Effect effect, Attributes attributes, String tooltip, EffectSource source) {
		assertThat(effect.getModifierComponent()).isNotNull();
		assertThat(effect.getModifierComponent().attributes()).isEqualTo(attributes);
		assertThat(effect.getTooltip()).isEqualTo(tooltip);
		assertThat(effect.getSource()).isEqualTo(source);
	}

	protected static void assertEffect(Effect effect, int effectId, String tooltip) {
		assertId(effect, effectId);
		assertThat(effect.getTooltip()).isEqualTo(tooltip);
	}

	protected static void assertId(AbstractItem<?> item, int id) {
		assertThat(item.getId().value()).isEqualTo(id);
	}

	protected static void assertId(Enchant enchant, int id) {
		assertThat(enchant.getId().value()).isEqualTo(id);
	}

	protected static void assertId(Talent talent, int id) {
		assertThat(talent.getId().value()).isEqualTo(id);
	}

	protected static void assertId(Effect effect, int id) {
		assertThat(effect.getId().value()).isEqualTo(id);
	}

	protected static void assertId(Spell spell, int id) {
		assertThat(spell.getId().value()).isEqualTo(id);
	}
}
