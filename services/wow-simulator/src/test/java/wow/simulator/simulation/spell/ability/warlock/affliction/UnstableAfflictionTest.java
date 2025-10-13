package wow.simulator.simulation.spell.ability.warlock.affliction;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.spell.WarlockSpellSimulationTest;
import wow.test.commons.TalentNames;

import static wow.commons.model.spell.ResourceType.HEALTH;
import static wow.commons.model.spell.ResourceType.MANA;
import static wow.test.commons.AbilityNames.UNSTABLE_AFFLICTION;

/**
 * User: POlszewski
 * Date: 2024-11-13
 */
class UnstableAfflictionTest extends WarlockSpellSimulationTest {
	/*
	Shadow energy slowly destroys the target, causing 1050 damage over 18 sec.
	In addition, if the Unstable Affliction is dispelled it will cause 1575 damage to the dispeller and silence them for 5 sec.
	 */

	@Test
	void success() {
		player.cast(UNSTABLE_AFFLICTION);

		updateUntil(30);

		assertEvents(
				at(0)
						.beginCast(player, UNSTABLE_AFFLICTION, 1.5)
						.beginGcd(player),
				at(1.5)
						.endCast(player, UNSTABLE_AFFLICTION)
						.decreasedResource(400, MANA, player, UNSTABLE_AFFLICTION)
						.effectApplied(UNSTABLE_AFFLICTION, target, 18)
						.endGcd(player),
				at(4.5)
						.decreasedResource(175, HEALTH, target, UNSTABLE_AFFLICTION),
				at(7.5)
						.decreasedResource(175, HEALTH, target, UNSTABLE_AFFLICTION),
				at(10.5)
						.decreasedResource(175, HEALTH, target, UNSTABLE_AFFLICTION),
				at(13.5)
						.decreasedResource(175, HEALTH, target, UNSTABLE_AFFLICTION),
				at(16.5)
						.decreasedResource(175, HEALTH, target, UNSTABLE_AFFLICTION),
				at(19.5)
						.decreasedResource(175, HEALTH, target, UNSTABLE_AFFLICTION)
						.effectExpired(UNSTABLE_AFFLICTION, target)
		);
	}

	@Test
	void resisted() {
		missesOnlyOnFollowingRolls(0);

		player.cast(UNSTABLE_AFFLICTION);

		updateUntil(30);

		assertEvents(
				at(0)
						.beginCast(player, UNSTABLE_AFFLICTION, 1.5)
						.beginGcd(player),
				at(1.5)
						.endCast(player, UNSTABLE_AFFLICTION)
						.decreasedResource(400, MANA, player, UNSTABLE_AFFLICTION)
						.spellResisted(player, UNSTABLE_AFFLICTION, target)
						.endGcd(player)
		);
	}

	@Test
	void interrupted() {
		player.cast(UNSTABLE_AFFLICTION);

		runAt(1, player::interruptCurrentAction);

		updateUntil(30);

		assertEvents(
				at(0)
						.beginCast(player, UNSTABLE_AFFLICTION, 1.5)
						.beginGcd(player),
				at(1)
						.castInterrupted(player, UNSTABLE_AFFLICTION)
						.endGcd(player)
		);
	}

	@Test
	void damageDone() {
		player.cast(UNSTABLE_AFFLICTION);

		updateUntil(30);

		assertDamageDone(UNSTABLE_AFFLICTION, UNSTABLE_AFFLICTION_INFO.damage());
	}

	@Override
	protected void afterSetUp() {
		enableTalent(TalentNames.UNSTABLE_AFFLICTION, 1);
	}
}
