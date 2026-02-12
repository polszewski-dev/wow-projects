package wow.simulator.script.command;

import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.spell.AbilityId;
import wow.simulator.simulation.spell.tbc.TbcWarlockSpellSimulationTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.character.model.script.ScriptCommand.*;
import static wow.character.model.script.ScriptCommandCondition.EMPTY;
import static wow.character.model.script.ScriptCommandTarget.DEFAULT;

/**
 * User: POlszewski
 * Date: 2025-09-20
 */
abstract class CommandExecutorTest extends TbcWarlockSpellSimulationTest {
	CastSpell castSpell(String abilityName) {
		return new CastSpell(
				EMPTY,
				AbilityId.of(abilityName),
				DEFAULT,
				false
		);
	}

	CastSpellExecutor getCastSpellExecutor(String abilityName) {
		var command = castSpell(abilityName);

		return CastSpellExecutor.create(command, player, player);
	}

	CastSpellRank castSpellRank(String abilityName, int rank) {
		return new CastSpellRank(
				EMPTY,
				abilityName,
				rank,
				DEFAULT,
				false
		);
	}

	CastSpellRankExecutor getCastSpellRankExecutor(String abilityName, int rank) {
		var command = castSpellRank(abilityName, rank);

		return CastSpellRankExecutor.create(command, player, player);
	}

	UseItemExecutor getUseItemExecutor(ItemSlot itemSlot) {
		var command = useItem(itemSlot);

		return UseItemExecutor.create(command, player, player);
	}

	UseItem useItem(ItemSlot itemSlot) {
		return new UseItem(
				EMPTY,
				itemSlot,
				DEFAULT,
				false
		);
	}

	CastSequenceExecutor getCastSequenceExecutor(ComposableCommand... commands) {
		var castSequence = new CastSequence(List.of(commands));

		return CastSequenceExecutor.create(castSequence, player, player);
	}

	double getCastTime(String abilityName) {
		return player.getAbility(AbilityId.of(abilityName)).orElseThrow().getCastTime().getSeconds();
	}

	private final TestSnapshots<Boolean> allConditionsAreMetSnapshots = new TestSnapshots<>();

	void snapshotAt(ScriptCommandExecutor executor, double... times) {
		allConditionsAreMetSnapshots.makeSnapshotsAt(executor::allConditionsAreMet, times);
	}

	void assertResultAt(double time, boolean expectedValue) {
		var actualValue = allConditionsAreMetSnapshots.get(time);

		assertThat(actualValue)
				.withFailMessage("at time = %s allConditionsAreMet should return: %s".formatted(time, expectedValue))
				.isEqualTo(expectedValue);
	}
}
