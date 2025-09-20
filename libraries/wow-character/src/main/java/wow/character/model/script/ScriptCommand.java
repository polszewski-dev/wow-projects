package wow.character.model.script;

import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.spell.AbilityId;

import java.util.List;
import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2025-09-17
 */
public sealed interface ScriptCommand {
	sealed interface ComposableCommand extends ScriptCommand permits CastSpell, CastSpellRank, UseItem {}

	record CastSpell(List<ScriptCommandCondition> conditions, AbilityId abilityId, ScriptCommandTarget target) implements ComposableCommand {
		public CastSpell {
			Objects.requireNonNull(conditions);
			Objects.requireNonNull(abilityId);
			Objects.requireNonNull(target);
		}
	}

	record CastSpellRank(List<ScriptCommandCondition> conditions, String abilityName, int rank, ScriptCommandTarget target) implements ComposableCommand {
		public CastSpellRank {
			Objects.requireNonNull(conditions);
			Objects.requireNonNull(abilityName);
			Objects.requireNonNull(target);
		}
	}

	record UseItem(List<ScriptCommandCondition> conditions, ItemSlot itemSlot, ScriptCommandTarget target) implements ComposableCommand {
		public UseItem {
			Objects.requireNonNull(conditions);
			Objects.requireNonNull(itemSlot);
			Objects.requireNonNull(target);
		}
	}

	record CastSequence(List<? extends ComposableCommand> list) implements ScriptCommand {
		public CastSequence {
			Objects.requireNonNull(list);
		}
	}

	static ScriptCommand compose(List<? extends ComposableCommand> commands) {
		if (commands.isEmpty()) {
			throw new IllegalArgumentException();
		}

		return commands.size() == 1
				? commands.getFirst()
				: new CastSequence(commands);
	}
}
