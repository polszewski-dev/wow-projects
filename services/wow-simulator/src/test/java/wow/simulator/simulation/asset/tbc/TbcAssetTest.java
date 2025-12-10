package wow.simulator.simulation.asset.tbc;

import org.springframework.beans.factory.annotation.Autowired;
import wow.character.model.asset.Asset;
import wow.character.repository.AssetTemplateRepository;
import wow.commons.model.character.RaceId;
import wow.simulator.model.unit.Player;
import wow.simulator.model.unit.Unit;
import wow.simulator.service.AssetService;
import wow.simulator.simulation.spell.tbc.TbcWarlockSpellSimulationTest;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * User: POlszewski
 * Date: 2025-12-13
 */
public abstract class TbcAssetTest extends TbcWarlockSpellSimulationTest {
	@Autowired
	AssetTemplateRepository assetTemplateRepository;

	@Autowired
	AssetService assetService;

	protected Asset asset;
	protected Player partyAsset;

	private String assetTemplateName;
	private RaceId assetRaceId;

	@Override
	protected void afterSetUp() {
		super.afterSetUp();

		var assetTemplate = assetTemplateRepository.getAssetTemplate(assetTemplateName, player.getGameVersionId()).orElseThrow();

		asset = new Asset(assetTemplate, assetRaceId);
		partyAsset = assetService.createPlayer("Asset", asset, player);

		simulation.add(partyAsset);

		player.getParty().add(partyAsset);
	}

	protected void setAssetParams(String templateName, RaceId raceId) {
		assetTemplateName = templateName;
		assetRaceId = raceId;
	}

	protected void executePreparationPhase() {
		assetService.executePreparationPhaseScripts(partyAsset, asset, player);

		updateUntil(60);
	}

	protected void executeWarmUpPhase() {
		assetService.executeWarmUpPhaseScripts(partyAsset, asset, player);

		updateUntil(120);
	}

	protected void assertHasTalent(String name) {
		var actual = partyAsset.getTalents().hasMaxRank(name);

		assertThat(actual).isTrue();
	}

	protected void assertSpellCast(double time, String abilityName, Unit target) {
		// todo
	}

	protected void assertHasEffect(Unit unit, String effectName) {
		assertHasEffect(unit, effectName, 1);
	}

	protected void assertHasEffect(Unit unit, String effectName, int numStacks) {
		// todo
	}
}
