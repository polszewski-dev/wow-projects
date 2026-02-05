package wow.simulator.simulation.asset.vanilla.warlock;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.asset.vanilla.VanillaAssetTest;

import static wow.test.commons.AbilityNames.DEMONIC_SACRIFICE;
import static wow.test.commons.AbilityNames.SUMMON_SUCCUBUS;
import static wow.test.commons.AssetNames.TOUCH_OF_SHADOW;

/**
 * User: POlszewski
 * Date: 2026-03-16
 */
class TouchOfShadowTest extends VanillaAssetTest {
	@Test
	void ability_is_cast() {
		assertSpellCast(0, SUMMON_SUCCUBUS, partyAsset);
		assertSpellCast(6, DEMONIC_SACRIFICE, partyAsset);
	}

	@Test
	void asset_has_buff_for_entire_simulation() {
		assertHasEffectForEntireSimulation(partyAsset, TOUCH_OF_SHADOW);
	}

	@Override
	protected void beforeSetUp() {
		setUpDestroWarlock(TOUCH_OF_SHADOW);
	}
}
