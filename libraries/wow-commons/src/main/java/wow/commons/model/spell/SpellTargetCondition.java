package wow.commons.model.spell;

import wow.commons.model.Condition;
import wow.commons.model.character.CreatureType;
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

	static SpellTargetCondition of(CreatureType creatureType) {
		return getCachedValue(creatureType, IsCreatureType::new);
	}

	static Comma comma(SpellTargetCondition... conditions) {
		return new Comma(List.of(conditions));
	}

	static Comma comma(List<SpellTargetCondition> conditions) {
		return new Comma(List.copyOf(conditions));
	}

	record Empty() implements SpellTargetCondition {
		@Override
		public boolean isEmpty() {
			return true;
		}
	}

	sealed interface Operator extends SpellTargetCondition {}

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

	record HasEffect(AbilityId abilityId) implements SpellTargetCondition {
		public HasEffect {
			Objects.requireNonNull(abilityId);
		}
	}

	record HealthAtMostPct(int value) implements SpellTargetCondition {}

	record IsCreatureType(CreatureType creatureType) implements SpellTargetCondition {
		public IsCreatureType {
			Objects.requireNonNull(creatureType);
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
