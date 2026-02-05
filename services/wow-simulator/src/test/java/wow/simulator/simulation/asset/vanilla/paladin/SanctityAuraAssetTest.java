package wow.simulator.simulation.asset.vanilla.paladin;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.asset.vanilla.VanillaAssetTest;

import static wow.commons.model.character.RaceId.HUMAN;
import static wow.test.commons.AssetNames.SANCTITY_AURA;

/**
 * User: POlszewski
 * Date: 2026-03-16
 */
class SanctityAuraAssetTest extends VanillaAssetTest {
	@Test
	void ability_is_cast() {
		assertSpellCast(0, SANCTITY_AURA, partyAsset);
	}

	@Test
	void asset_has_buff_for_entire_simulation() {
		assertHasEffectForEntireSimulation(partyAsset, SANCTITY_AURA);
	}

	@Override
	protected void beforeSetUp() {
		raceId = HUMAN;
		setUpRetributionPaladin(SANCTITY_AURA);
	}
}
