package wow.scraper.classifier;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.commons.model.attribute.condition.*;
import wow.commons.model.attribute.primitive.PowerType;
import wow.commons.model.attribute.primitive.PrimitiveAttribute;
import wow.commons.model.attribute.primitive.PrimitiveAttributeId;
import wow.commons.model.attribute.primitive.PrimitiveAttributeType;
import wow.commons.model.categorization.PveRole;
import wow.commons.model.effect.Effect;
import wow.commons.model.effect.component.Event;
import wow.commons.model.effect.component.EventType;
import wow.commons.model.spell.ActionType;
import wow.commons.model.spell.ActivatedAbility;
import wow.commons.model.spell.Spell;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static wow.commons.model.attribute.condition.ConditionOperator.BinaryConditionOperator;
import static wow.commons.model.attribute.primitive.PrimitiveAttributeType.*;

/**
 * User: POlszewski
 * Date: 2022-12-19
 */
@AllArgsConstructor
@Component
public class CasterDpsStatClassifier implements PveRoleStatClassifier {
	@Override
	public PveRole getRole() {
		return PveRole.CASTER_DPS;
	}

	@Override
	public boolean hasStatsSuitableForRole(List<Effect> effects, ActivatedAbility activatedAbility) {
		var isAttributeEffect = effects.stream().collect(Collectors.partitioningBy(x -> x.getEffectId() == 0));
		var attributeEffects = isAttributeEffect.get(true);
		var permanentEffects = isAttributeEffect.get(false);

		var topLevel = getTopLevelAttributes(attributeEffects);

		if (hasCasterAttributes(topLevel) || isCasterSpell(activatedAbility)) {
			return true;
		}

		return permanentEffects.stream().anyMatch(this::isCasterEffect);
	}

	private List<PrimitiveAttribute> getTopLevelAttributes(List<Effect> effects) {
		return effects.stream()
				.map(Effect::getModifierAttributeList)
				.filter(Objects::nonNull)
				.flatMap(Collection::stream)
				.toList();
	}

	private boolean hasCasterAttributes(List<PrimitiveAttribute> attributes) {
		return attributes.stream().anyMatch(this::isCasterAttribute) && getSpellDamage(attributes) >= getHealingPower(attributes);
	}

	private double getSpellDamage(List<PrimitiveAttribute> attributes) {
		return getPower(attributes, PowerType.SPELL_DAMAGE);
	}

	private double getHealingPower(List<PrimitiveAttribute> attributes) {
		return getPower(attributes, PowerType.HEALING);
	}

	private double getPower(List<PrimitiveAttribute> attributes, PowerType powerType) {
		var args = new AttributeConditionArgs(ActionType.SPELL);

		args.setPowerType(powerType);

		return attributes.stream()
				.filter(x -> x.id() == PrimitiveAttributeId.POWER)
				.filter(x -> x.condition().test(args))
				.mapToDouble(PrimitiveAttribute::value)
				.sum();
	}

	private boolean isCasterEffect(Effect effect) {
		var modifierAttributeList = effect.getModifierAttributeList();

		if (modifierAttributeList != null && hasCasterAttributes(modifierAttributeList)) {
			return true;
		}

		return effect.getEvents().stream().anyMatch(this::isCasterEvent);
	}

	private boolean isCasterEvent(Event event) {
		return hasAnyCasterEventType(event) && isCasterEventEffect(event);
	}

	private boolean hasAnyCasterEventType(Event event) {
		return event.types().stream().anyMatch(this::isCasterEventType);
	}

	private boolean isCasterEventType(EventType eventType) {
		return switch (eventType) {
			case SPELL_CAST, SPELL_HIT, SPELL_CRIT, SPELL_DAMAGE, SPELL_RESISTED -> true;
			default -> false;
		};
	}

	private boolean isCasterEventEffect(Event event) {
		var spell = event.triggeredSpell();
		return isCasterSpell(spell);
	}

	private boolean isCasterSpell(Spell spell) {
		if (spell == null) {
			return false;
		}
		var effectApplication = spell.getEffectApplication();
		if (effectApplication == null) {
			return false;
		}
		return isCasterEffect(effectApplication.effect());
	}

	private boolean isCasterAttribute(PrimitiveAttribute attribute) {
		return CASTER_STATS.contains(attribute.id().getType()) && isCasterCondition(attribute.condition());
	}

	private boolean isCasterCondition(AttributeCondition condition) {
		if (condition.isEmpty()) {
			return true;
		}

		return getBasicConditions(condition).stream().anyMatch(this::isBasicCasterCondition);
	}

	private List<AttributeCondition> getBasicConditions(AttributeCondition condition) {
		if (condition instanceof BinaryConditionOperator operator) {
			return operator.getLeaves();
		} else {
			return List.of(condition);
		}
	}

	private boolean isBasicCasterCondition(AttributeCondition condition) {
		return
				condition == MiscCondition.SPELL_DAMAGE ||
				condition == MiscCondition.SPELL ||
				condition == MiscCondition.HOSTILE_SPELL ||
				condition instanceof SpellSchoolCondition ||
				condition instanceof TalentTreeCondition ||
				condition instanceof AbilityIdCondition
		;
	}

	private static final Set<PrimitiveAttributeType> CASTER_STATS = Set.of(
			POWER, HIT, CRIT, HASTE
	);
}
