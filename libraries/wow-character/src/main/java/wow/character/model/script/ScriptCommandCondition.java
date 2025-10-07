package wow.character.model.script;

import wow.commons.model.Condition;

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

	record EmptyCondition() implements ScriptCommandCondition {
		@Override
		public boolean isEmpty() {
			return true;
		}
	}
	
	record CasterHasEffect(String effectNamePattern) implements ScriptCommandCondition {}

	record TargetHasEffect(String effectNamePattern) implements ScriptCommandCondition {}
}
