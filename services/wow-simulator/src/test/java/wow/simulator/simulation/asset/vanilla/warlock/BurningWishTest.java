package wow.simulator.simulation.asset.vanilla.warlock;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.asset.vanilla.VanillaAssetTest;

import static wow.test.commons.AbilityNames.DEMONIC_SACRIFICE;
import static wow.test.commons.AbilityNames.SUMMON_IMP;
import static wow.test.commons.AssetNames.BURNING_WISH;

/**
 * User: POlszewski
 * Date: 2026-03-16
 */
class BurningWishTest extends VanillaAssetTest {
	@Test
	void ability_is_cast() {
		assertSpellCast(0, SUMMON_IMP, partyAsset);
		assertSpellCast(6, DEMONIC_SACRIFICE, partyAsset);
	}

	@Test
	void asset_has_buff_for_entire_simulation() {
		assertHasEffectForEntireSimulation(partyAsset, BURNING_WISH);
	}

	@Override
	protected void beforeSetUp() {
		setUpDestroWarlock(BURNING_WISH);
	}
}
