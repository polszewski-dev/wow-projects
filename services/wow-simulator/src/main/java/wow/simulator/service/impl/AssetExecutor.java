package wow.simulator.service.impl;

import wow.character.model.asset.AssetExecution;
import wow.character.model.character.Party;
import wow.character.model.script.ScriptCommand;
import wow.character.model.script.ScriptCommandCondition;
import wow.character.model.script.ScriptCommandTarget;
import wow.commons.model.spell.AbilityId;
import wow.simulator.model.unit.Player;
import wow.simulator.model.unit.Unit;
import wow.simulator.script.ScriptParams;
import wow.simulator.script.SinglePassScriptExecutor;
import wow.simulator.script.command.CastSpellOnTargetExecutor;
import wow.simulator.script.command.ScriptCommandExecutor;
import wow.simulator.util.CountdownCounter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static wow.character.model.asset.Asset.*;
import static wow.character.model.script.ScriptSectionType.PREPARATION;

/**
 * User: POlszewski
 * Date: 2026-08-13
 */
class AssetExecutor {
	private final Unit player;
	private final ScriptParams params;
	private final List<AssetExecution<Player>> executions;
	private final CountdownCounter counter;

	AssetExecutor(ScriptParams params, List<AssetExecution<Player>> executions, CountdownCounter counter) {
		this.player = params.caster();
		this.params = params;
		this.executions = executions;
		this.counter = counter;
	}

	void execute() {
		var commands = new ArrayList<ScriptCommandExecutor>();

		for (var execution : executions) {
			commands.addAll(executeBuffCommand(execution));
		}

		var params = new ScriptParams(player, null);
		var scriptExecutor = new SinglePassScriptExecutor(params, commands);

		scriptExecutor.setFinalAction(counter::decrease);
		scriptExecutor.execute();
	}

	private List<ScriptCommandExecutor> executeBuffCommand(AssetExecution<Player> command) {
		var asset = command.asset();

		if (asset.buffCommand() == null) {
			return List.of();
		}

		return switch (asset.buffCommand()) {
			case CastAbility(var target, var abilityId) ->
					execCastAbility(abilityId, target);

			case ExecuteScript(var target, var scriptName) ->
					executeScript(scriptName, target);
		};
	}

	private List<ScriptCommandExecutor> execCastAbility(AbilityId abilityId, BuffTarget target) {
		return switch (target) {
			case EACH_RAID_MEMBER -> {
				var result = new ArrayList<ScriptCommandExecutor>();

				player.getRaid().forEachMemberAndPet((Unit memberOrPet) -> {
					var executor = castExecutor(abilityId, memberOrPet);

					result.add(executor);
				});

				yield result;
			}

			case EACH_PARTY_FIRST_MEMBER ->
					player.getRaid().getParties().stream()
							.map(Party::getFirstMember)
							.filter(Objects::nonNull)
							.map(firstMember -> castExecutor(abilityId, firstMember))
							.toList();

			case SELF ->
					List.of(castExecutor(abilityId, player));

			case TARGET_ENEMY ->
					List.of(castExecutor(abilityId, player.getTarget()));
		};
	}

	private ScriptCommandExecutor castExecutor(AbilityId abilityId, Unit target) {
		var command = new ScriptCommand.CastSpell(
				ScriptCommandCondition.EMPTY, abilityId, ScriptCommandTarget.DEFAULT, false
		);

		return CastSpellOnTargetExecutor.create(command, target, params);
	}

	private List<ScriptCommandExecutor> executeScript(String scriptName, BuffTarget target) {
		if (target != BuffTarget.SELF) {
			throw new IllegalArgumentException();
		}

		var scriptExecutor = SinglePassScriptExecutor.compileScript(scriptName, PREPARATION, params);

		return scriptExecutor.getCommands();
	}
}
