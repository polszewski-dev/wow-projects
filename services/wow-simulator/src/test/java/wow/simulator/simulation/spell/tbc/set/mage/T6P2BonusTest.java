package wow.simulator.simulation.spell.tbc.set.mage;

import org.junit.jupiter.api.Test;
import wow.commons.model.Duration;
import wow.simulator.simulation.spell.tbc.TbcMageSpellSimulationTest;

import static wow.test.commons.AbilityNames.EVOCATION;

/**
 * User: POlszewski
 * Date: 2026-09-06
 */
class T6P2BonusTest extends TbcMageSpellSimulationTest {
	/*
	Increases the duration of your Evocation ability by 2 sec.
	 */
	@Test
	void evocation_duration_is_increased() {
		setMana(player, 0);

		player.cast(EVOCATION);

		updateUntil(30);

		var effectDuration = getChanneledEffectDuration(5, 2);

		assertEffectDuration(EVOCATION, player, effectDuration);
	}

	private Duration getChanneledEffectDuration(int numTicks, int singleTickDuration) {
		var singleTickDurationMillis = getDurationAffectedByHaste(singleTickDuration).millis();

		return Duration.millis(numTicks * singleTickDurationMillis);
	}

	private Duration getDurationAffectedByHaste(int baseDuration) {
		var hastePct = player.getStats().getSpellHastePct();
		var millis = (int) (1000 * baseDuration / (1 + hastePct / 100));

		return Duration.millis(millis);
	}

	@Override
	protected void afterSetUp() {
		equip("Mantle of the Tempest");
		equip("Boots of the Tempest");
	}
}
