package wow.simulator.simulation.asset.vanilla.priest;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.asset.vanilla.VanillaAssetTest;

import static wow.test.commons.AbilityNames.PRAYER_OF_FORTITUDE;

/**
 * User: POlszewski
 * Date: 2026-03-16
 */
class PrayerOfFortitudeAssetTest extends VanillaAssetTest {
	@Test
	void ability_is_cast() {
		assertSpellCast(0, PRAYER_OF_FORTITUDE, player);
	}

	@Test
	void player_has_buff_for_entire_simulation() {
		assertHasEffectForEntireSimulation(player, PRAYER_OF_FORTITUDE);
	}

	@Test
	void asset_has_buff_for_entire_simulation() {
		assertHasEffectForEntireSimulation(partyAsset, PRAYER_OF_FORTITUDE);
	}

	@Override
	protected void beforeSetUp() {
		setUpDisciplinePriest(PRAYER_OF_FORTITUDE);
	}
}
