package wow.simulator.simulation.asset.vanilla.druid;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.asset.vanilla.VanillaAssetTest;

import static wow.test.commons.AbilityNames.MOONKIN_FORM;
import static wow.test.commons.AssetNames.MOONKIN_AURA;

/**
 * User: POlszewski
 * Date: 2026-03-16
 */
class MoonkinAuraAssetTest extends VanillaAssetTest {
	@Test
	void ability_is_cast() {
		assertSpellCast(0, MOONKIN_FORM, partyAsset);
	}

	@Test
	void asset_has_buff_for_entire_simulation() {
		assertHasEffectForEntireSimulation(partyAsset, MOONKIN_AURA);
	}

	@Override
	protected void beforeSetUp() {
		setUpBalanceDruid(MOONKIN_AURA);
	}
}
