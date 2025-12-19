package wow.commons.model.effect.component;

import wow.commons.model.Condition;
import wow.commons.model.attribute.PowerType;
import wow.commons.model.character.CreatureType;
import wow.commons.model.spell.AbilityCategory;
import wow.commons.model.spell.AbilityId;
import wow.commons.model.spell.ActionType;
import wow.commons.model.spell.SpellSchool;
import wow.commons.model.talent.TalentTree;
import wow.commons.util.condition.ConditionCache;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import static wow.commons.model.effect.component.EventConditionCache.getCachedValue;
import static wow.commons.util.condition.EventConditionParser.parseCondition;

/**
 * User: POlszewski
 * Date: 2025-10-06
 */
public sealed interface EventCondition extends Condition {
	EmptyCondition EMPTY = new EmptyCondition();

	IsDirect IS_DIRECT = new IsDirect();
	IsPeriodic IS_PERIODIC = new IsPeriodic();
	IsHostileSpell IS_HOSTILE_SPELL = new IsHostileSpell();
	IsNormalMeleeAttack IS_NORMAL_MELEE_ATTACK = new IsNormalMeleeAttack();
	IsSpecialAttack IS_SPECIAL_ATTACK = new IsSpecialAttack();
	HasManaCost HAS_MANA_COST = new HasManaCost();
	HasCastTime HAS_CAST_TIME = new HasCastTime();
	HasCastTimeUnder10Sec HAS_CAST_TIME_UNDER_10_SEC = new HasCastTimeUnder10Sec();
	CanCrit CAN_CRIT = new CanCrit();
	HadNoCrit HAD_NO_CRIT = new HadNoCrit();
	HadCrit HAD_CRIT = new HadCrit();
	IsTargetingOthers IS_TARGETING_OTHERS = new IsTargetingOthers();

	static EventCondition of(ActionType actionType) {
		return getCachedValue(actionType, ActionTypeCondition::new);
	}

	static EventCondition of(PowerType powerType) {
		return getCachedValue(powerType, PowerTypeCondition::new);
	}

	static EventCondition of(TalentTree talentTree) {
		return getCachedValue(talentTree, TalentTreeCondition::new);
	}

	static EventCondition of(SpellSchool spellSchool) {
		return getCachedValue(spellSchool, SpellSchoolCondition::new);
	}

	static EventCondition of(AbilityId abilityId) {
		return getCachedValue(abilityId, AbilityIdCondition::new);
	}

	static EventCondition of(AbilityCategory abilityCategory) {
		return getCachedValue(abilityCategory, AbilityCategoryCondition::new);
	}

	static EventCondition of(CreatureType creatureType) {
		return getCachedValue(creatureType, TargetTypeCondition::new);
	}

	static EventCondition parse(String value) {
		return getCachedValue(
				value,
				x -> parseCondition(value)
		);
	}

	static Or or(EventCondition left, EventCondition right) {
		return new Or(left, right);
	}

	static And and(EventCondition left, EventCondition right) {
		return new And(left, right);
	}

	static Comma comma(EventCondition... conditions) {
		return new Comma(List.of(conditions));
	}

	static Comma comma(List<EventCondition> conditions) {
		return new Comma(List.copyOf(conditions));
	}

	record EmptyCondition() implements EventCondition {
		@Override
		public boolean isEmpty() {
			return true;
		}
	}

	sealed interface Operator extends EventCondition {}

	sealed interface BinaryOperator extends Operator {
		EventCondition left();

		EventCondition right();
	}

	record Or(EventCondition left, EventCondition right) implements BinaryOperator {
		public Or {
			Objects.requireNonNull(left);
			Objects.requireNonNull(right);
		}
	}

	record And(EventCondition left, EventCondition right) implements BinaryOperator {
		public And {
			Objects.requireNonNull(left);
			Objects.requireNonNull(right);
		}
	}

	record Comma(List<EventCondition> conditions) implements Operator {
		public Comma {
			Objects.requireNonNull(conditions);

			if (conditions.size() < 2) {
				throw new IllegalArgumentException("At least 2 conditions required");
			}

			if (conditions.stream().anyMatch(Operator.class::isInstance)) {
				throw new IllegalArgumentException("Only simple types");
			}
		}
	}

	record ActionTypeCondition(ActionType actionType) implements EventCondition {
		public ActionTypeCondition {
			Objects.requireNonNull(actionType);
		}
	}

	record PowerTypeCondition(PowerType powerType) implements EventCondition {
		public PowerTypeCondition {
			Objects.requireNonNull(powerType);
		}
	}

	record TalentTreeCondition(TalentTree talentTree) implements EventCondition {
		public TalentTreeCondition {
			Objects.requireNonNull(talentTree);
		}
	}

	record SpellSchoolCondition(SpellSchool spellSchool) implements EventCondition {
		public SpellSchoolCondition {
			Objects.requireNonNull(spellSchool);
		}
	}

	record AbilityIdCondition(AbilityId abilityId) implements EventCondition {
		public AbilityIdCondition {
			Objects.requireNonNull(abilityId);
		}
	}

	record AbilityCategoryCondition(AbilityCategory abilityCategory) implements EventCondition {
		public AbilityCategoryCondition {
			Objects.requireNonNull(abilityCategory);
		}
	}

	record TargetTypeCondition(CreatureType creatureType) implements EventCondition {
		public TargetTypeCondition {
			Objects.requireNonNull(creatureType);
		}
	}

	record OwnerHasEffectCondition(String effectName) implements EventCondition {
		public OwnerHasEffectCondition {
			Objects.requireNonNull(effectName);
		}
	}

	record OwnerIsChannelingCondition(AbilityId abilityId) implements EventCondition {
		public OwnerIsChannelingCondition {
			Objects.requireNonNull(abilityId);
		}
	}

	record IsDirect() implements EventCondition {}

	record IsPeriodic() implements EventCondition {}

	record IsHostileSpell() implements EventCondition {}

	record IsSpecialAttack() implements EventCondition {}

	record IsNormalMeleeAttack() implements EventCondition {}

	record HasManaCost() implements EventCondition {}

	record HasCastTime() implements EventCondition {}

	record HasCastTimeUnder10Sec() implements EventCondition {}

	record CanCrit() implements EventCondition {}

	record HadNoCrit() implements EventCondition {}

	record HadCrit() implements EventCondition {}

	record IsTargetingOthers() implements EventCondition {}

	record OwnerHealthPctLessThan(double value) implements EventCondition {}

	record TargetHealthPctLessThan(double value) implements EventCondition {}
}

class EventConditionCache extends ConditionCache<EventCondition> {
	private static final EventConditionCache INSTANCE = new EventConditionCache();

	static <K> EventCondition getCachedValue(K key, Function<K, EventCondition> conditionMapper) {
		return INSTANCE.getValue(key, conditionMapper);
	}

	@Override
	protected EventCondition emptyCondition() {
		return EventCondition.EMPTY;
	}
}
