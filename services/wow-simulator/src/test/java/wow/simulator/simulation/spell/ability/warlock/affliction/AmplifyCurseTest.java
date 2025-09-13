package wow.simulator.simulation.spell.ability.warlock.affliction;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.spell.WarlockSpellSimulationTest;
import wow.test.commons.TalentNames;

import static wow.commons.model.spell.ResourceType.HEALTH;
import static wow.commons.model.spell.ResourceType.MANA;
import static wow.test.commons.AbilityNames.*;

/**
 * User: POlszewski
 * Date: 2025-01-18
 */
class AmplifyCurseTest extends WarlockSpellSimulationTest {
	/*
	Increases the effect of your next Curse of Doom or Curse of Agony by 50%, or your next Curse of Exhaustion by an additional 20%.  Lasts 30 sec.
	 */

	@Test
	void success() {
		player.cast(AMPLIFY_CURSE);

		updateUntil(180);

		assertEvents(
			at(0)
					.beginCast(player, AMPLIFY_CURSE)
					.endCast(player, AMPLIFY_CURSE)
					.cooldownStarted(player, AMPLIFY_CURSE, 180)
					.effectApplied(AMPLIFY_CURSE, player, 30),
			at(30)
					.effectExpired(AMPLIFY_CURSE, player),
			at(180)
					.cooldownExpired(player, AMPLIFY_CURSE)
		);
	}

	@Test
	void effectIsRemovedAfterCastingCurse() {
		player.cast(AMPLIFY_CURSE);
		player.cast(CURSE_OF_DOOM);

		updateUntil(180);

		assertEvents(
				at(0)
						.beginCast(player, AMPLIFY_CURSE)
						.endCast(player, AMPLIFY_CURSE)
						.cooldownStarted(player, AMPLIFY_CURSE, 180)
						.effectApplied(AMPLIFY_CURSE, player, 30)
						.beginCast(player, CURSE_OF_DOOM)
						.beginGcd(player)
						.endCast(player, CURSE_OF_DOOM)
						.decreasedResource(380, MANA, player, CURSE_OF_DOOM)
						.cooldownStarted(player, CURSE_OF_DOOM, 60)
						.effectRemoved(AMPLIFY_CURSE, player)
						.effectApplied(CURSE_OF_DOOM, target, 60),
				at(1.5)
						.endGcd(player),
				at(60)
						.cooldownExpired(player, CURSE_OF_DOOM)
						.decreasedResource(6300, HEALTH, target, CURSE_OF_DOOM)
						.effectExpired(CURSE_OF_DOOM, target),
				at(180)
						.cooldownExpired(player, AMPLIFY_CURSE)
		);
	}

	@Test
	void curseOfDoomDamageIsIncreased() {
		player.cast(AMPLIFY_CURSE);
		player.cast(CURSE_OF_DOOM);

		updateUntil(180);

		assertDamageDone(CURSE_OF_DOOM, CURSE_OF_DOOM_INFO.damage(), 50);
	}

	@Test
	void curseOfAgonyDamageIsIncreased() {
		player.cast(AMPLIFY_CURSE);
		player.cast(CURSE_OF_AGONY);

		updateUntil(180);

		assertDamageDone(CURSE_OF_AGONY, CURSE_OF_AGONY_INFO.damage(), 50);
	}

	@Override
	protected void afterSetUp() {
		enableTalent(TalentNames.AMPLIFY_CURSE, 1);
	}
}
