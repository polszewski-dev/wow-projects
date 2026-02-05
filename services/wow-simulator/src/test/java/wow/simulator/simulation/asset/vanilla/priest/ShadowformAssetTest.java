package wow.simulator.simulation.asset.vanilla.priest;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.asset.vanilla.VanillaAssetTest;

import static wow.test.commons.AbilityNames.SHADOWFORM;

/**
 * User: POlszewski
 * Date: 2026-03-16
 */
class ShadowformAssetTest extends VanillaAssetTest {
	@Test
	void ability_is_cast() {
		assertSpellCast(0, SHADOWFORM, partyAsset);
	}

	@Test
	void asset_has_buff_for_entire_simulation() {
		assertHasEffectForEntireSimulation(partyAsset, SHADOWFORM);
	}

	@Override
	protected void beforeSetUp() {
		setUpShadowPriest(SHADOWFORM);
	}
}
