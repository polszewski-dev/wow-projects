package wow.simulator.simulation.asset.vanilla.warlock;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.asset.vanilla.VanillaAssetTest;

import static wow.test.commons.AbilityNames.DEMON_ARMOR;

/**
 * User: POlszewski
 * Date: 2026-03-16
 */
class DemonArmorAssetTest extends VanillaAssetTest {
	@Test
	void ability_is_cast() {
		assertSpellCast(0, DEMON_ARMOR, partyAsset);
	}

	@Test
	void asset_has_buff_for_entire_simulation() {
		assertHasEffectForEntireSimulation(partyAsset, DEMON_ARMOR);
	}

	@Override
	protected void beforeSetUp() {
		setUpDestroWarlock(DEMON_ARMOR);
	}
}
