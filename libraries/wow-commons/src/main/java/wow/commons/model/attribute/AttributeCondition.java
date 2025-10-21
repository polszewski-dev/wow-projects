package wow.commons.model.attribute;

import wow.commons.model.Condition;
import wow.commons.model.categorization.WeaponSubType;
import wow.commons.model.character.CreatureType;
import wow.commons.model.character.MovementType;
import wow.commons.model.character.PetType;
import wow.commons.model.effect.EffectCategory;
import wow.commons.model.profession.ProfessionId;
import wow.commons.model.spell.AbilityCategory;
import wow.commons.model.spell.AbilityId;
import wow.commons.model.spell.ActionType;
import wow.commons.model.spell.SpellSchool;
import wow.commons.model.talent.TalentTree;
import wow.commons.util.condition.ConditionCache;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import static wow.commons.model.attribute.AttributeConditionCache.getCachedValue;
import static wow.commons.util.condition.AttributeConditionParser.parseCondition;

/**
 * User: POlszewski
 * Date: 2021-10-25
 */
public sealed interface AttributeCondition extends Condition {
	EmptyCondition EMPTY = new EmptyCondition();

	IsDirect IS_DIRECT = new IsDirect();
	HasHealingComponent HAS_HEALING_COMPONENT = new HasHealingComponent();
	IsInstantCast IS_INSTANT_CAST = new IsInstantCast();
	HasCastTime HAS_CAST_TIME = new HasCastTime();
	HasCastTimeUnder10Sec HAS_CAST_TIME_UNDER_10_SEC = new HasCastTimeUnder10Sec();
	HadCrit HAD_CRIT = new HadCrit();
	HasPet HAS_PET = new HasPet();

	static AttributeCondition of(ActionType actionType) {
		return getCachedValue(actionType, ActionTypeCondition::new);
	}

	static AttributeCondition of(PowerType powerType) {
		return getCachedValue(powerType, PowerTypeCondition::new);
	}

	static AttributeCondition of(TalentTree talentTree) {
		return getCachedValue(talentTree, TalentTreeCondition::new);
	}

	static AttributeCondition of(SpellSchool spellSchool) {
		return getCachedValue(spellSchool, SpellSchoolCondition::new);
	}

	static AttributeCondition of(AbilityId abilityId) {
		return getCachedValue(abilityId, AbilityIdCondition::new);
	}

	static AttributeCondition of(AbilityCategory abilityCategory) {
		return getCachedValue(abilityCategory, AbilityCategoryCondition::new);
	}

	static AttributeCondition of(PetType petType) {
		return getCachedValue(petType, PetTypeCondition::new);
	}

	static AttributeCondition of(CreatureType creatureType) {
		return getCachedValue(creatureType, TargetTypeCondition::new);
	}

	static AttributeCondition of(EffectCategory effectCategory) {
		return getCachedValue(effectCategory, EffectCategoryCondition::new);
	}

	static AttributeCondition of(WeaponSubType weaponSubType) {
		return getCachedValue(weaponSubType, WeaponTypeCondition::new);
	}

	static AttributeCondition of(ProfessionId professionId) {
		return getCachedValue(professionId, ProfessionCondition::new);
	}

	static AttributeCondition of(MovementType movementType) {
		return getCachedValue(movementType, MovementTypeCondition::new);
	}

	static AttributeCondition parse(String value) {
		return getCachedValue(
				value,
				x -> parseCondition(value)
		);
	}

	static Or or(AttributeCondition left, AttributeCondition right) {
		return new Or(left, right);
	}

	static And and(AttributeCondition left, AttributeCondition right) {
		return new And(left, right);
	}

	static Comma comma(AttributeCondition... conditions) {
		return new Comma(List.of(conditions));
	}

	static Comma comma(List<AttributeCondition> conditions) {
		return new Comma(List.copyOf(conditions));
	}

	record EmptyCondition() implements AttributeCondition {
		@Override
		public boolean isEmpty() {
			return true;
		}
	}

	sealed interface Operator extends AttributeCondition {}

	sealed interface BinaryOperator extends Operator {
		AttributeCondition left();

		AttributeCondition right();
	}

	record Or(AttributeCondition left, AttributeCondition right) implements BinaryOperator {
		public Or {
			Objects.requireNonNull(left);
			Objects.requireNonNull(right);
		}
	}

	record And(AttributeCondition left, AttributeCondition right) implements BinaryOperator {
		public And {
			Objects.requireNonNull(left);
			Objects.requireNonNull(right);
		}
	}

	record Comma(List<AttributeCondition> conditions) implements Operator {
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

	record ActionTypeCondition(ActionType actionType) implements AttributeCondition {
		public ActionTypeCondition {
			Objects.requireNonNull(actionType);
		}
	}

	record PowerTypeCondition(PowerType powerType) implements AttributeCondition {
		public PowerTypeCondition {
			Objects.requireNonNull(powerType);
		}
	}

	record TalentTreeCondition(TalentTree talentTree) implements AttributeCondition {
		public TalentTreeCondition {
			Objects.requireNonNull(talentTree);
		}
	}

	record SpellSchoolCondition(SpellSchool spellSchool) implements AttributeCondition {
		public SpellSchoolCondition {
			Objects.requireNonNull(spellSchool);
		}
	}

	record AbilityIdCondition(AbilityId abilityId) implements AttributeCondition {
		public AbilityIdCondition {
			Objects.requireNonNull(abilityId);
		}
	}

	record AbilityCategoryCondition(AbilityCategory abilityCategory) implements AttributeCondition {
		public AbilityCategoryCondition {
			Objects.requireNonNull(abilityCategory);
		}
	}

	record PetTypeCondition(PetType petType) implements AttributeCondition {
		public PetTypeCondition {
			Objects.requireNonNull(petType);
		}
	}

	record TargetTypeCondition(CreatureType creatureType) implements AttributeCondition {
		public TargetTypeCondition {
			Objects.requireNonNull(creatureType);
		}
	}

	record EffectCategoryCondition(EffectCategory effectCategory) implements AttributeCondition {
		public EffectCategoryCondition {
			Objects.requireNonNull(effectCategory);
		}
	}

	record WeaponTypeCondition(WeaponSubType weaponType) implements AttributeCondition {
		public WeaponTypeCondition {
			Objects.requireNonNull(weaponType);
		}
	}

	record ProfessionCondition(ProfessionId professionId) implements AttributeCondition {
		public ProfessionCondition {
			Objects.requireNonNull(professionId);
		}
	}

	record MovementTypeCondition(MovementType movementType) implements AttributeCondition {
		public MovementTypeCondition {
			Objects.requireNonNull(movementType);
		}
	}

	record IsDirect() implements AttributeCondition {}

	record HasHealingComponent() implements AttributeCondition {}

	record IsInstantCast() implements AttributeCondition {}

	record HasCastTime() implements AttributeCondition {}

	record HasCastTimeUnder10Sec() implements AttributeCondition {}

	record HadCrit() implements AttributeCondition {}

	record HasPet() implements AttributeCondition {}

	record OwnerHealthBelowPct(int value) implements AttributeCondition {}
}

class AttributeConditionCache extends ConditionCache<AttributeCondition> {
	private static final AttributeConditionCache INSTANCE = new AttributeConditionCache();

	static <K> AttributeCondition getCachedValue(K key, Function<K, AttributeCondition> conditionMapper) {
		return INSTANCE.getValue(key, conditionMapper);
	}

	@Override
	protected AttributeCondition emptyCondition() {
		return AttributeCondition.EMPTY;
	}
}
