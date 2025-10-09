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
	sealed interface ComposableCommand extends ScriptCommand permits CastSpell, CastSpellRank, UseItem {
		boolean optional();
	}

	record CastSpell(ScriptCommandCondition condition, AbilityId abilityId, ScriptCommandTarget target, boolean optional) implements ComposableCommand {
		public CastSpell {
			Objects.requireNonNull(condition);
			Objects.requireNonNull(abilityId);
			Objects.requireNonNull(target);
		}
	}

	record CastSpellRank(ScriptCommandCondition condition, String abilityName, int rank, ScriptCommandTarget target, boolean optional) implements ComposableCommand {
		public CastSpellRank {
			Objects.requireNonNull(condition);
			Objects.requireNonNull(abilityName);
			Objects.requireNonNull(target);
		}
	}

	record UseItem(ScriptCommandCondition condition, ItemSlot itemSlot, ScriptCommandTarget target, boolean optional) implements ComposableCommand {
		public UseItem {
			Objects.requireNonNull(condition);
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
