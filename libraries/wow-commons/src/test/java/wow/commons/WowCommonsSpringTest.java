package wow.commons;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import wow.commons.model.Duration;
import wow.commons.model.Percent;
import wow.commons.model.attribute.Attribute;
import wow.commons.model.attribute.AttributeId;
import wow.commons.model.attribute.Attributes;
import wow.commons.model.attribute.condition.AttributeCondition;
import wow.commons.model.effect.Effect;
import wow.commons.model.effect.EffectSource;
import wow.commons.model.effect.component.*;
import wow.commons.model.item.AbstractItem;
import wow.commons.model.item.Enchant;
import wow.commons.model.spell.*;
import wow.commons.model.talent.Talent;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.test.commons.TestConstants.PRECISION;

/**
 * User: POlszewski
 * Date: 2022-12-19
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = WowCommonsSpringTestConfig.class)
public abstract class WowCommonsSpringTest {
	protected static void assertEffectApplication(Spell spell, SpellTarget target, int duration, int numCharges, int numStacks, int maxStacks) {
		var effectApplication = spell.getEffectApplication();

		assertThat(effectApplication).isNotNull();
		assertThat(effectApplication.target()).isEqualTo(target);
		assertThat(effectApplication.duration()).isEqualTo(Duration.seconds(duration));
		assertThat(effectApplication.numCharges()).isEqualTo(numCharges);
		assertThat(effectApplication.numStacks()).isEqualTo(numStacks);
		assertThat(effectApplication.effect()).isNotNull();
		assertThat(effectApplication.effect().getMaxStacks()).isEqualTo(maxStacks);
	}

	protected static void assertPeriodicComponent(PeriodicComponent periodic, ComponentType type, double coeff, SpellSchool school, int amount, int numTicks, double tickInterval, TickScheme tickScheme) {
		assertThat(periodic.type()).isEqualTo(type);
		assertCoefficient(coeff, school, periodic.coefficient());
		assertThat(periodic.amount()).isEqualTo(amount);
		assertThat(periodic.numTicks()).isEqualTo(numTicks);
		assertThat(periodic.tickInterval().getSeconds()).isEqualTo(tickInterval);
		assertThat(periodic.tickScheme()).isEqualTo(tickScheme);
	}

	protected static void assertModifier(Effect effect, List<Attribute> attributes) {
		assertThat(effect.getModifierAttributeList()).isNotNull();
		assertThat(effect.getModifierAttributeList()).isEqualTo(attributes);
	}

	protected static void assertStatConversion(Effect effect, int idx, AttributeId from, AttributeId to, int ratio, AttributeCondition toCondition) {
		assertThat(effect.getStatConversions()).hasSizeGreaterThan(idx);

		var statConversion = effect.getStatConversions().get(idx);

		assertThat(statConversion.from()).isEqualTo(from);
		assertThat(statConversion.to()).isEqualTo(to);
		assertThat(statConversion.toCondition()).isEqualTo(toCondition);
		assertThat(statConversion.ratioPct()).isEqualTo(Percent.of(ratio));
	}

	protected static void assertEvent(Event event, List<EventType> types, AttributeCondition condition, double chance, List<EventAction> actions, Duration cooldown) {
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
		assertThat(effect.getEffectId()).isEqualTo(effectId);
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
}
