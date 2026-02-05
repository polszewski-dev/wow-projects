package wow.simulator.simulation.asset.vanilla.mage;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.asset.vanilla.VanillaAssetTest;

import static wow.test.commons.AbilityNames.ARCANE_BRILLIANCE;

/**
 * User: POlszewski
 * Date: 2026-03-16
 */
class ArcaneBrillianceAssetTest extends VanillaAssetTest {
	@Test
	void ability_is_cast() {
		assertSpellCast(0, ARCANE_BRILLIANCE, player);
	}

	@Test
	void player_has_buff_for_entire_simulation() {
		assertHasEffectForEntireSimulation(player, ARCANE_BRILLIANCE);
	}

	@Test
	void asset_has_buff_for_entire_simulation() {
		assertHasEffectForEntireSimulation(partyAsset, ARCANE_BRILLIANCE);
	}

	@Override
	protected void beforeSetUp() {
		setUpFireMage(ARCANE_BRILLIANCE);
	}
}
