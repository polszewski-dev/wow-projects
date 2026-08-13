package wow.simulator.simulation.asset.tbc.warlock;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.asset.tbc.TbcAssetTest;

import static wow.test.commons.AbilityNames.BLOOD_PACT;
import static wow.test.commons.AbilityNames.SUMMON_IMP;
import static wow.test.commons.AssetNames.IMP;

/**
 * User: POlszewski
 * Date: 2026-08-12
 */
class ImpTest extends TbcAssetTest {
	@Test
	void ability_is_cast() {
		assertSpellCast(0, SUMMON_IMP, partyAsset);
		assertSpellCast(6, BLOOD_PACT, partyAsset.getActivePet(), partyAsset.getActivePet());
	}

	@Test
	void asset_has_buff_for_entire_simulation() {
		assertHasEffectForEntireSimulation(partyAsset.getActivePet(), BLOOD_PACT);
	}

	@Override
	protected void beforeSetUp() {
		setUpDestroWarlock(IMP);
	}
}
