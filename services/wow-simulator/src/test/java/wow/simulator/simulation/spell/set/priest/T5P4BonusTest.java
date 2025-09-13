package wow.simulator.simulation.spell.set.priest;

import org.junit.jupiter.api.Test;
import wow.simulator.model.time.Time;
import wow.simulator.simulation.spell.PriestSpellSimulationTest;
import wow.simulator.util.TestEvent;

import static wow.simulator.util.EffectType.ITEM_SET;
import static wow.test.commons.AbilityNames.MIND_BLAST;
import static wow.test.commons.AbilityNames.SHADOW_WORD_PAIN;

/**
 * User: POlszewski
 * Date: 2025-01-24
 */
class T5P4BonusTest extends PriestSpellSimulationTest {
	/*
	Each time your Shadow Word: Pain deals damage, it has a chance to grant your next spell cast within 15 sec up to 100 damage and healing.
	*/

	@Test
	void effectIsTriggered() {
		eventsOnlyOnFollowingRolls(1);

		player.cast(SHADOW_WORD_PAIN);

		updateUntil(30);

		assertEvents(
				TestEvent::isEffect,
				at(0)
						.effectApplied(SHADOW_WORD_PAIN, target, 18),
				at(3)
						.effectApplied("Avatar Regalia (4)", ITEM_SET, player, 15),
				at(18)
						.effectExpired("Avatar Regalia (4)", ITEM_SET, player)
						.effectExpired(SHADOW_WORD_PAIN, target)
		);
	}

	@Test
	void effectIsRemovedAfterAnotherSpellIsCasted() {
		eventsOnlyOnFollowingRolls(1);

		player.cast(SHADOW_WORD_PAIN);
		player.idleUntil(Time.at(3));
		player.cast(MIND_BLAST);

		updateUntil(30);

		assertEvents(
				TestEvent::isEffect,
				at(0)
						.effectApplied(SHADOW_WORD_PAIN, target, 18),
				at(3)
						.effectApplied("Avatar Regalia (4)", ITEM_SET, player, 15),
				at(4.5)
						.effectRemoved("Avatar Regalia (4)", ITEM_SET, player),
				at(18)
						.effectExpired(SHADOW_WORD_PAIN, target)
		);
	}

	@Test
	void secondSpellReceivesDamageBonus() {
		eventsOnlyOnFollowingRolls(1);

		player.cast(SHADOW_WORD_PAIN);
		player.idleUntil(Time.at(3));
		player.cast(MIND_BLAST);

		updateUntil(30);

		assertDamageDone(MIND_BLAST, MIND_BLAST_INFO.damage(totalSpellDamage + 100));
	}

	@Override
	protected void afterSetUp() {
		equip("Handguards of the Avatar");
		equip("Hood of the Avatar");
		equip("Leggings of the Avatar");
		equip("Shroud of the Avatar");
	}
}