package wow.character.model.script;

import wow.commons.model.Condition;
import wow.commons.model.spell.AbilityId;

import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2025-09-17
 */
public sealed interface ScriptCommandCondition extends Condition {
	EmptyCondition EMPTY = new EmptyCondition();

	sealed interface Operator extends ScriptCommandCondition {}

	sealed interface BinaryOperator extends Operator {
		ScriptCommandCondition left();

		ScriptCommandCondition right();
	}

	record Or(ScriptCommandCondition left, ScriptCommandCondition right) implements BinaryOperator {
		public Or {
			Objects.requireNonNull(left);
			Objects.requireNonNull(right);
		}
	}

	record And(ScriptCommandCondition left, ScriptCommandCondition right) implements BinaryOperator {
		public And {
			Objects.requireNonNull(left);
			Objects.requireNonNull(right);
		}
	}

	record Not(ScriptCommandCondition condition) implements Operator {
		public Not {
			Objects.requireNonNull(condition);
		}
	}

	sealed interface ComparisonOperator extends Operator {}

	record LessThan(Expression left, Expression right) implements ComparisonOperator {
		public LessThan {
			Objects.requireNonNull(left);
			Objects.requireNonNull(right);
		}
	}

	record LessThanOrEqual(Expression left, Expression right) implements ComparisonOperator {
		public LessThanOrEqual {
			Objects.requireNonNull(left);
			Objects.requireNonNull(right);
		}
	}

	record GreaterThan(Expression left, Expression right) implements ComparisonOperator {
		public GreaterThan {
			Objects.requireNonNull(left);
			Objects.requireNonNull(right);
		}
	}

	record GreaterThanOrEqual(Expression left, Expression right) implements ComparisonOperator {
		public GreaterThanOrEqual {
			Objects.requireNonNull(left);
			Objects.requireNonNull(right);
		}
	}

	record EmptyCondition() implements ScriptCommandCondition {
		@Override
		public boolean isEmpty() {
			return true;
		}
	}
	
	record CasterHasEffect(String effectNamePattern) implements ScriptCommandCondition {
		public CasterHasEffect {
			Objects.requireNonNull(effectNamePattern);
		}
	}

	record TargetHasEffect(String effectNamePattern) implements ScriptCommandCondition {
		public TargetHasEffect {
			Objects.requireNonNull(effectNamePattern);
		}
	}

	record CanCastMoreBeforeSimulationEnds(AbilityId abilityId) implements ScriptCommandCondition {
		public CanCastMoreBeforeSimulationEnds {
			Objects.requireNonNull(abilityId);
		}
	}

	sealed interface Expression {}

	record Constant(double value) implements Expression {}

	record CasterHealth() implements Expression {}

	record CasterMana() implements Expression {}

	record CasterHealthPct() implements Expression {}

	record CasterManaPct() implements Expression {}

	record TargetHealthPct() implements Expression {}

	record TargetManaPct() implements Expression {}

	record FullDuration() implements Expression {}

	record RemainingCooldown(AbilityId abilityId) implements Expression {
		public RemainingCooldown {
			Objects.requireNonNull(abilityId);
		}
	}

	record RemainingSimulationDuration() implements Expression {}
}
