package wow.simulator.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import wow.character.model.asset.Asset;
import wow.character.model.script.ScriptSectionType;
import wow.character.service.CharacterService;
import wow.simulator.model.unit.Player;
import wow.simulator.model.unit.impl.PlayerImpl;
import wow.simulator.script.SinglePassScriptExecutor;
import wow.simulator.service.AssetService;

import static wow.character.model.script.ScriptSectionType.PREPARATION;
import static wow.character.model.script.ScriptSectionType.WARM_UP;

/**
 * User: POlszewski
 * Date: 2025-12-13
 */
@Service
@RequiredArgsConstructor
public class AssetServiceImpl implements AssetService {
	private final CharacterService characterService;

	@Override
	public Player createPlayer(String name, Asset asset, Player mainPlayer) {
		var player = characterService.createPlayerCharacter(
				name,
				asset.characterClassId(),
				asset.getRaceId(),
				mainPlayer.getLevel(),
				mainPlayer.getPhaseId(),
				PlayerImpl::new
		);

		for (var option : asset.getSelectedOptions()) {
			var talentName = option.talentName();

			if (talentName != null) {
				player.getTalents().enableMaxRank(talentName);
			}
		}

		characterService.updateAfterRestrictionChange(player);

		player.whenNoActionIdleForever();

		return player;
	}

	@Override
	public void executePreparationPhaseScripts(Player partyAsset, Asset asset, Player mainPlayer) {
		partyAsset.setTarget(mainPlayer);

		for (var option : asset.getSelectedOptions()) {
			var scriptName = option.preparationPhaseScript();

			executeScript(scriptName, partyAsset, mainPlayer, PREPARATION);
		}
	}

	@Override
	public void executeWarmUpPhaseScripts(Player partyAsset, Asset asset, Player mainPlayer) {
		partyAsset.setTarget(mainPlayer.getTarget());

		for (var option : asset.getSelectedOptions()) {
			var scriptName = option.warmUpPhaseScript();

			executeScript(scriptName, partyAsset, mainPlayer, WARM_UP);
		}
	}

	private void executeScript(String scriptName, Player partyAsset, Player mainPlayer, ScriptSectionType sectionType) {
		if (scriptName == null) {
			return;
		}

		var scriptExecutor = new SinglePassScriptExecutor("asset/" + scriptName, sectionType, partyAsset, mainPlayer);

		scriptExecutor.setupPlayer();

		scriptExecutor.execute();
	}
}
