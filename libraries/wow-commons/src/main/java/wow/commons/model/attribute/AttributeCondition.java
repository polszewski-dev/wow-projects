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

import java.util.List;
import java.util.Objects;

import static wow.commons.util.condition.AttributeConditionParser.parseCondition;

/**
 * User: POlszewski
 * Date: 2021-10-25
 */
public sealed interface AttributeCondition extends Condition {
	EmptyCondition EMPTY = new EmptyCondition();

	static AttributeCondition of(ActionType actionType) {
		return new ActionTypeCondition(actionType);
	}

	static AttributeCondition of(PowerType powerType) {
		return new PowerTypeCondition(powerType);
	}

	static AttributeCondition of(TalentTree talentTree) {
		return new TalentTreeCondition(talentTree);
	}

	static AttributeCondition of(SpellSchool spellSchool) {
		return new SpellSchoolCondition(spellSchool);
	}

	static AttributeCondition of(AbilityId abilityId) {
		return new AbilityIdCondition(abilityId);
	}

	static AttributeCondition of(AbilityCategory abilityCategory) {
		return new AbilityCategoryCondition(abilityCategory);
	}

	static AttributeCondition of(PetType petType) {
		return new PetTypeCondition(petType);
	}

	static AttributeCondition of(CreatureType creatureType) {
		return new TargetTypeCondition(creatureType);
	}

	static AttributeCondition of(EffectCategory effectCategory) {
		return new EffectCategoryCondition(effectCategory);
	}

	static AttributeCondition of(WeaponSubType weaponSubType) {
		return new WeaponTypeCondition(weaponSubType);
	}

	static AttributeCondition of(ProfessionId professionId) {
		return new ProfessionCondition(professionId);
	}

	static AttributeCondition of(MovementType movementType) {
		return new MovementTypeCondition(movementType);
	}

	static AttributeCondition parse(String value) {
		return parseCondition(value);
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

	record EmptyCondition() implements BaseStatsCondition, CastCondition, CostCondition, HitCondition, SpellCondition, DurationCondition, ReceivedEffectCondition, TargetCondition, RegenCondition, ResistanceCondition, CopyCondition, MeleeCondition, MiscCondition {
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

	sealed interface BaseStatsCondition extends AttributeCondition {}

	sealed interface CastCondition extends AttributeCondition {}

	sealed interface CostCondition extends AttributeCondition {}

	sealed interface HitCondition extends AttributeCondition {}

	sealed interface SpellCondition extends AttributeCondition {}

	sealed interface DurationCondition extends AttributeCondition {}

	sealed interface ReceivedEffectCondition extends AttributeCondition {}

	sealed interface TargetCondition extends AttributeCondition {}

	sealed interface RegenCondition extends AttributeCondition {}

	sealed interface ResistanceCondition extends AttributeCondition {}

	sealed interface CopyCondition extends AttributeCondition {}

	sealed interface MeleeCondition extends AttributeCondition {}

	sealed interface MiscCondition extends AttributeCondition {}

	record ActionTypeCondition(ActionType actionType) implements CastCondition, HitCondition, SpellCondition, TargetCondition, ResistanceCondition, MiscCondition {
		public ActionTypeCondition {
			Objects.requireNonNull(actionType);
		}
	}

	record PowerTypeCondition(PowerType powerType) implements CostCondition, HitCondition, SpellCondition, TargetCondition, MiscCondition {
		public PowerTypeCondition {
			Objects.requireNonNull(powerType);
		}
	}

	record TalentTreeCondition(TalentTree talentTree) implements CostCondition, HitCondition, SpellCondition, MiscCondition {
		public TalentTreeCondition {
			Objects.requireNonNull(talentTree);
		}
	}

	record SpellSchoolCondition(SpellSchool spellSchool) implements CostCondition, CastCondition, HitCondition, SpellCondition, TargetCondition, ResistanceCondition, MiscCondition {
		public SpellSchoolCondition {
			Objects.requireNonNull(spellSchool);
		}
	}

	record AbilityIdCondition(AbilityId abilityId) implements CastCondition, CostCondition, HitCondition, SpellCondition, DurationCondition, CopyCondition, ResistanceCondition, MiscCondition {
		public AbilityIdCondition {
			Objects.requireNonNull(abilityId);
		}
	}

	record EffectNameCondition(String effectName) implements DurationCondition, ReceivedEffectCondition {
		public EffectNameCondition {
			Objects.requireNonNull(effectName);
		}
	}

	record AbilityCategoryCondition(AbilityCategory abilityCategory) implements CostCondition, HitCondition, SpellCondition, DurationCondition, ResistanceCondition, MiscCondition {
		public AbilityCategoryCondition {
			Objects.requireNonNull(abilityCategory);
		}
	}

	record PetTypeCondition(PetType petType) implements SpellCondition, TargetCondition, ResistanceCondition, MiscCondition {
		public PetTypeCondition {
			Objects.requireNonNull(petType);
		}
	}

	record TargetTypeCondition(CreatureType creatureType) implements SpellCondition {
		public TargetTypeCondition {
			Objects.requireNonNull(creatureType);
		}
	}

	record EffectCategoryCondition(EffectCategory effectCategory) implements DurationCondition, ReceivedEffectCondition, ResistanceCondition {
		public EffectCategoryCondition {
			Objects.requireNonNull(effectCategory);
		}
	}

	record WeaponTypeCondition(WeaponSubType weaponType) implements MeleeCondition {
		public WeaponTypeCondition {
			Objects.requireNonNull(weaponType);
		}
	}

	record ProfessionCondition(ProfessionId professionId) implements MiscCondition {
		public ProfessionCondition {
			Objects.requireNonNull(professionId);
		}
	}

	record MovementTypeCondition(MovementType movementType) implements MiscCondition {
		public MovementTypeCondition {
			Objects.requireNonNull(movementType);
		}
	}

	record IsDirect() implements SpellCondition {}

	record IsPeriodic() implements SpellCondition {}

	record HasHealingComponent() implements CostCondition {}

	record IsInstantCast() implements CostCondition {}

	record HasCastTime() implements CastCondition {}

	record HasCastTimeUnder10Sec() implements CastCondition {}

	record HadCrit() implements MiscCondition {}

	record HasPet() implements SpellCondition {}

	record OwnerHasEffect(String effectName) implements SpellCondition {
		public OwnerHasEffect {
			Objects.requireNonNull(effectName);
		}
	}

	record TargetHealthPctLessThan(double value) implements SpellCondition {}
}
