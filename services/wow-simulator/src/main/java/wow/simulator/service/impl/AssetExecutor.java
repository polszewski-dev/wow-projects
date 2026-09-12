package wow.simulator.service.impl;

import wow.character.model.asset.AssetExecution;
import wow.commons.model.spell.AbilityId;
import wow.simulator.model.unit.Player;
import wow.simulator.model.unit.Unit;
import wow.simulator.script.ScriptParams;
import wow.simulator.script.SinglePassScriptExecutor;

import java.util.List;

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

	AssetExecutor(ScriptParams params, List<AssetExecution<Player>> executions) {
		this.player = params.caster();
		this.params = params;
		this.executions = executions;
	}

	void execute() {
		for (var execution : executions) {
			executeBuffCommand(execution);
		}
	}

	private void executeBuffCommand(AssetExecution<Player> command) {
		var asset = command.asset();

		if (asset.buffCommand() == null) {
			return;
		}

		switch (asset.buffCommand()) {
			case CastAbility(var target, var abilityId) ->
					execCastAbility(abilityId, target);

			case ExecuteScript(var target, var scriptName) ->
					executeScript(scriptName, target);
		}
	}

	private void execCastAbility(AbilityId abilityId, BuffTarget target) {
		switch (target) {
			case EACH_RAID_MEMBER ->
					player.getRaid().forEachMemberAndPet(memberOrPet -> player.cast(abilityId, memberOrPet));

			case EACH_PARTY_FIRST_MEMBER ->
					player.getRaid().forEachPartyFirstMember(firstMember -> player.cast(abilityId, firstMember));

			case SELF ->
					player.cast(abilityId);

			case TARGET_ENEMY ->
					player.cast(abilityId, player.getTarget());
		}
	}

	private void executeScript(String scriptName, BuffTarget target) {
		if (target != BuffTarget.SELF) {
			throw new IllegalArgumentException();
		}

		var scriptExecutor = SinglePassScriptExecutor.compileScript(scriptName, PREPARATION, params);

		scriptExecutor.execute();
	}
}
