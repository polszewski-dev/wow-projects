package wow.simulator.simulation.asset.tbc.shaman;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.asset.tbc.TbcAssetTest;

import static wow.test.commons.AssetNames.TOTEM_OF_WRATH;

/**
 * User: POlszewski
 * Date: 2026-03-16
 */
class TotemOfWrathAssetTest extends TbcAssetTest {
	@Test
	void ability_is_cast() {
		assertSpellCast(0, TOTEM_OF_WRATH, partyAsset);
	}

	@Test
	void asset_has_buff_for_entire_simulation() {
		assertHasEffectForEntireSimulation(partyAsset, TOTEM_OF_WRATH);
	}

	@Override
	protected void beforeSetUp() {
		setUpElementalShaman(TOTEM_OF_WRATH);
	}
}
