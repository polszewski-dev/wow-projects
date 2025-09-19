package wow.simulator.script.command;

import wow.character.model.script.ScriptCommand;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.spell.AbilityId;
import wow.simulator.simulation.spell.WarlockSpellSimulationTest;

import java.util.List;

import static wow.character.model.script.ScriptCommand.CastSpell;
import static wow.character.model.script.ScriptCommand.UseItem;
import static wow.character.model.script.ScriptCommandTarget.DEFAULT;

/**
 * User: POlszewski
 * Date: 2025-09-20
 */
abstract class CommandExecutorTest extends WarlockSpellSimulationTest {
	CastSpell castSpell(String abilityName) {
		return new CastSpell(
				List.of(),
				AbilityId.of(abilityName),
				DEFAULT
		);
	}

	CastSpellExecutor getCastSpellExecutor(String abilityName) {
		var command = castSpell(abilityName);

		return CastSpellExecutor.create(command, player);
	}

	UseItemExecutor getUseItemExecutor(ItemSlot itemSlot) {
		var command = useItem(itemSlot);

		return UseItemExecutor.create(command, player);
	}

	UseItem useItem(ItemSlot itemSlot) {
		return new UseItem(
				List.of(),
				itemSlot,
				DEFAULT
		);
	}

	CastSequenceExecutor getCastSequenceExecutor(ScriptCommand.ComposableCommand... commands) {
		var castSequence = new ScriptCommand.CastSequence(List.of(commands));

		return CastSequenceExecutor.create(castSequence, player);
	}

	double getCastTime(String abilityName) {
		return player.getAbility(AbilityId.of(abilityName)).orElseThrow().getCastTime().getSeconds();
	}
}
