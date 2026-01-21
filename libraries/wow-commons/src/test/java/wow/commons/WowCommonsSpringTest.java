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
import wow.commons.model.spell.Coefficient;
import wow.commons.model.spell.Spell;
import wow.commons.model.spell.SpellSchool;
import wow.commons.model.spell.SpellTarget;
import wow.commons.model.talent.Talent;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.spell.component.ComponentCommand.ApplyEffect;

/**
 * User: POlszewski
 * Date: 2022-12-19
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = WowCommonsSpringTestConfig.class)
public abstract class WowCommonsSpringTest {
	protected static Coefficient coefficient(double value, SpellSchool school) {
		return new Coefficient(Percent.of(value), school);
	}

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

	protected static void assertEffect(Effect effect, Attributes attributes, String tooltip, EffectSource source) {
		assertThat(effect.getModifierComponent()).isNotNull();
		assertThat(effect.getModifierComponent().attributes()).isEqualTo(attributes);
		assertThat(effect.getTooltip()).isEqualTo(tooltip);
		assertThat(effect.getSource()).isEqualTo(source);
	}

	protected static void assertId(AbstractItem<?> item, int id) {
		assertThat(item.getId().value()).isEqualTo(id);
	}

	protected static void assertId(Talent talent, int id) {
		assertThat(talent.getId().value()).isEqualTo(id);
	}

	protected <T> List<T> toList(String value, Function<String, T> mapper) {
		return value != null
				? Stream.of(value.split("\\+")).map(String::trim).map(mapper).toList()
				: List.of();
	}

	public record StatsData(String name, List<StatLine> statLines) {}

	public record StatLine(Attributes attributes, String tooltip) {
		public StatLine(Effect effect) {
			this(effect.getModifierComponent().attributes(), effect.getTooltip());
		}

		public StatLine(AttributeId id, double value, AttributeCondition condition, String tooltip) {
			this(Attributes.of(id, value, condition), tooltip);
		}

		public StatLine(AttributeId id, double value, String tooltip) {
			this(id, value, AttributeCondition.EMPTY, tooltip);
		}
	}
}
