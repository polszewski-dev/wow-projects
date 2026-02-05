package wow.simulator.simulation.asset.vanilla.paladin;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.asset.vanilla.VanillaAssetTest;

import static wow.commons.model.character.RaceId.HUMAN;
import static wow.test.commons.AbilityNames.GREATER_BLESSING_OF_WISDOM;

/**
 * User: POlszewski
 * Date: 2026-03-16
 */
class GreaterBlessingOfWisdomAssetTest extends VanillaAssetTest {
	@Test
	void ability_is_cast() {
		assertSpellCast(0, GREATER_BLESSING_OF_WISDOM, player);
	}

	@Test
	void player_has_buff_for_entire_simulation() {
		assertHasEffectForEntireSimulation(player, GREATER_BLESSING_OF_WISDOM);
	}

	@Test
	void asset_has_buff_for_entire_simulation() {
		assertHasEffectForEntireSimulation(partyAsset, GREATER_BLESSING_OF_WISDOM);
	}

	@Override
	protected void beforeSetUp() {
		raceId = HUMAN;
		setUpHolyPaladin(GREATER_BLESSING_OF_WISDOM);
	}
}
