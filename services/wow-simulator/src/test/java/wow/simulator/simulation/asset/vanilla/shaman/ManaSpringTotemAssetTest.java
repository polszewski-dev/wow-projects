package wow.simulator.simulation.asset.vanilla.shaman;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.asset.vanilla.VanillaAssetTest;

import static wow.test.commons.AssetNames.MANA_SPRING_TOTEM;

/**
 * User: POlszewski
 * Date: 2026-03-16
 */
class ManaSpringTotemAssetTest extends VanillaAssetTest {
	@Test
	void ability_is_cast() {
		assertSpellCast(0, MANA_SPRING_TOTEM, partyAsset);
	}

	@Test
	void asset_has_buff_for_entire_simulation() {
		assertHasEffectForEntireSimulation(partyAsset, MANA_SPRING_TOTEM);
	}

	@Override
	protected void beforeSetUp() {
		setUpElementalShaman(MANA_SPRING_TOTEM);
	}
}
