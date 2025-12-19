package wow.commons.model.spell;

import wow.commons.model.Condition;
import wow.commons.model.character.CharacterClassId;
import wow.commons.model.character.CreatureType;
import wow.commons.model.character.DruidFormType;
import wow.commons.model.character.PetType;
import wow.commons.util.condition.ConditionCache;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import static wow.commons.model.spell.SpellTargetConditionCache.getCachedValue;

/**
 * User: POlszewski
 * Date: 2025-11-28
 */
public sealed interface SpellTargetCondition extends Condition {
	Empty EMPTY = new Empty();

	Friendly FRIENDLY = new Friendly();

	Hostile HOSTILE = new Hostile();

	HasPet HAS_PET = new HasPet();

	static SpellTargetCondition of(CreatureType creatureType) {
		return getCachedValue(creatureType, IsCreatureType::new);
	}

	static SpellTargetCondition of(DruidFormType druidFormType) {
		return getCachedValue(druidFormType, HasDruidForm::new);
	}

	static Or or(SpellTargetCondition left, SpellTargetCondition right) {
		return new Or(left, right);
	}

	static And and(SpellTargetCondition left, SpellTargetCondition right) {
		return new And(left, right);
	}

	static Comma comma(SpellTargetCondition... conditions) {
		return new Comma(List.of(conditions));
	}

	static Comma comma(List<SpellTargetCondition> conditions) {
		return new Comma(List.copyOf(conditions));
	}

	static Not not(SpellTargetCondition condition) {
		return new Not(condition);
	}

	record Empty() implements SpellTargetCondition {
		@Override
		public boolean isEmpty() {
			return true;
		}
	}

	sealed interface Operator extends SpellTargetCondition {}

	sealed interface BinaryOperator extends Operator {
		SpellTargetCondition left();

		SpellTargetCondition right();
	}

	record Or(SpellTargetCondition left, SpellTargetCondition right) implements BinaryOperator {
		public Or {
			Objects.requireNonNull(left);
			Objects.requireNonNull(right);
		}
	}

	record And(SpellTargetCondition left, SpellTargetCondition right) implements BinaryOperator {
		public And {
			Objects.requireNonNull(left);
			Objects.requireNonNull(right);
		}
	}

	record Comma(List<SpellTargetCondition> conditions) implements Operator {
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

	record Not(SpellTargetCondition condition) implements Operator {
		public Not {
			Objects.requireNonNull(condition);
		}
	}

	record HasEffect(String effectName) implements SpellTargetCondition {
		public HasEffect {
			Objects.requireNonNull(effectName);
		}
	}

	record HealthPctLessThan(double value) implements SpellTargetCondition {}

	record HealthPctLessThanOrEqual(double value) implements SpellTargetCondition {}

	record HealthPctGreaterThan(double value) implements SpellTargetCondition {}

	record HealthPctGreaterThanOrEqual(double value) implements SpellTargetCondition {}

	record IsCreatureType(CreatureType creatureType) implements SpellTargetCondition {
		public IsCreatureType {
			Objects.requireNonNull(creatureType);
		}
	}

	record IsClass(CharacterClassId characterClassId) implements SpellTargetCondition {
		public IsClass {
			Objects.requireNonNull(characterClassId);
		}
	}

	record HasDruidForm(DruidFormType druidFormType) implements SpellTargetCondition {
		public HasDruidForm {
			Objects.requireNonNull(druidFormType);
		}
	}

	record Friendly() implements SpellTargetCondition {}

	record Hostile() implements SpellTargetCondition {}

	record HasPet() implements SpellTargetCondition {}

	record SacrificedPet(PetType petType) implements SpellTargetCondition {
		public SacrificedPet {
			Objects.requireNonNull(petType);
		}
	}
}

class SpellTargetConditionCache extends ConditionCache<SpellTargetCondition> {
	private static final SpellTargetConditionCache INSTANCE = new SpellTargetConditionCache();

	static <K> SpellTargetCondition getCachedValue(K key, Function<K, SpellTargetCondition> conditionMapper) {
		return INSTANCE.getValue(key, conditionMapper);
	}

	@Override
	protected SpellTargetCondition emptyCondition() {
		return SpellTargetCondition.EMPTY;
	}
}
