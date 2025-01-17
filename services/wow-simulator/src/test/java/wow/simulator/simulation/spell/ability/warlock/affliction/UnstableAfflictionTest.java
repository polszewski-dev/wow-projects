package wow.simulator.simulation.spell.ability.warlock.affliction;

import org.junit.jupiter.api.Test;
import wow.commons.model.talent.TalentId;
import wow.simulator.model.time.Time;
import wow.simulator.simulation.spell.WarlockSpellSimulationTest;

import static wow.commons.model.spell.AbilityId.UNSTABLE_AFFLICTION;
import static wow.commons.model.spell.ResourceType.HEALTH;
import static wow.commons.model.spell.ResourceType.MANA;

/**
 * User: POlszewski
 * Date: 2024-11-13
 */
class UnstableAfflictionTest extends WarlockSpellSimulationTest {
	@Test
	void success() {
		enableTalent(TalentId.UNSTABLE_AFFLICTION, 1);

		player.cast(UNSTABLE_AFFLICTION);

		simulation.updateUntil(Time.at(30));

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
		rng.hitRoll = false;

		enableTalent(TalentId.UNSTABLE_AFFLICTION, 1);

		player.cast(UNSTABLE_AFFLICTION);

		simulation.updateUntil(Time.at(30));

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
		enableTalent(TalentId.UNSTABLE_AFFLICTION, 1);

		player.cast(UNSTABLE_AFFLICTION);

		simulation.updateUntil(Time.at(1));

		player.interruptCurrentAction();

		simulation.updateUntil(Time.at(30));

		assertEvents(
				at(0)
						.beginCast(player, UNSTABLE_AFFLICTION, 1.5)
						.beginGcd(player),
				at(1)
						.castInterrupted(player, UNSTABLE_AFFLICTION)
						.endGcd(player)
		);
	}
}
