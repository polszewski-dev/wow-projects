package wow.simulator.simulation.asset.tbc.paladin;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.asset.tbc.TbcAssetTest;

import static wow.test.commons.AssetNames.SANCTITY_AURA;

/**
 * User: POlszewski
 * Date: 2026-03-16
 */
class SanctityAuraAssetTest extends TbcAssetTest {
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
		setUpRetributionPaladin(SANCTITY_AURA);
	}
}
