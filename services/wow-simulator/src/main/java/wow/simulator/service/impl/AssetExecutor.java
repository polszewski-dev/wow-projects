package wow.simulator.service.impl;

import wow.character.model.asset.AssetExecution;
import wow.character.model.script.ScriptCommandCondition;
import wow.character.model.script.ScriptCommandTarget;
import wow.commons.model.spell.AbilityId;
import wow.simulator.model.unit.Player;
import wow.simulator.model.unit.Unit;
import wow.simulator.script.ScriptParams;
import wow.simulator.script.SinglePassScriptExecutor;
import wow.simulator.script.command.CastSpellOnTargetExecutor;
import wow.simulator.script.command.ScriptCommandExecutor;

import java.util.List;
import java.util.stream.Stream;

import static wow.character.model.asset.Asset.*;
import static wow.character.model.script.ScriptCommand.CastSpell;
import static wow.character.model.script.ScriptSectionType.PREPARATION;

/**
 * User: POlszewski
 * Date: 2026-08-13
 */
class AssetExecutor {
	private final Unit player;
	private final ScriptParams params;
	private final List<AssetExecution<Player>> executions;
	private final Runnable finalAction;

	AssetExecutor(ScriptParams params, List<AssetExecution<Player>> executions, Runnable finalAction) {
		this.player = params.caster();
		this.params = params;
		this.executions = executions;
		this.finalAction = finalAction;
	}

	void execute() {
		var commands = executions.stream()
				.flatMap(this::executeBuffCommand)
				.toList();

		var scriptExecutor = new SinglePassScriptExecutor(params, commands);

		scriptExecutor.setFinalAction(finalAction);
		scriptExecutor.execute();
	}

	private Stream<ScriptCommandExecutor> executeBuffCommand(AssetExecution<Player> command) {
		var asset = command.asset();

		if (asset.buffCommand() == null) {
			return Stream.of();
		}

		return switch (asset.buffCommand()) {
			case CastAbility(var target, var abilityId) ->
					execCastAbility(abilityId, target);

			case ExecuteScript(var target, var scriptName) ->
					executeScript(scriptName, target);
		};
	}

	private Stream<ScriptCommandExecutor> execCastAbility(AbilityId abilityId, BuffTarget target) {
		return switch (target) {
			case EACH_RAID_MEMBER ->
					player.getRaid().<Unit>getEachMemberAndPetStream()
							.map(memberOrPet -> castExecutor(abilityId, memberOrPet));

			case EACH_PARTY_FIRST_MEMBER ->
					player.getRaid().getEachPartyFirstMemberStream()
							.map(firstMember -> castExecutor(abilityId, firstMember));

			case SELF ->
					Stream.of(castExecutor(abilityId, player));

			case TARGET_ENEMY ->
					Stream.of(castExecutor(abilityId, player.getTarget()));
		};
	}

	private ScriptCommandExecutor castExecutor(AbilityId abilityId, Unit target) {
		var command = new CastSpell(
				ScriptCommandCondition.EMPTY, abilityId, ScriptCommandTarget.DEFAULT, false
		);

		return CastSpellOnTargetExecutor.create(command, target, params);
	}

	private Stream<ScriptCommandExecutor> executeScript(String scriptName, BuffTarget target) {
		if (target != BuffTarget.SELF) {
			throw new IllegalArgumentException();
		}

		var scriptExecutor = SinglePassScriptExecutor.compileScript(scriptName, PREPARATION, params);

		return scriptExecutor.getCommands().stream();
	}
}
