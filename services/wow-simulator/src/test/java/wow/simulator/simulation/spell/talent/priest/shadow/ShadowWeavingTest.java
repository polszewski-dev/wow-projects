package wow.simulator.simulation.spell.talent.priest.shadow;

import org.junit.jupiter.api.Test;
import wow.commons.model.talent.TalentId;
import wow.simulator.model.time.Time;
import wow.simulator.simulation.spell.PriestSpellSimulationTest;

import static wow.commons.model.spell.AbilityId.MIND_BLAST;
import static wow.commons.model.spell.AbilityId.SHADOW_WORD_PAIN;
import static wow.commons.model.spell.ResourceType.HEALTH;
import static wow.commons.model.talent.TalentId.SHADOW_WEAVING;
import static wow.simulator.WowSimulatorSpringTest.EventCollectingHandler.Event;

/**
 * User: POlszewski
 * Date: 2024-11-16
 */
class ShadowWeavingTest extends PriestSpellSimulationTest {
	@Test
	void effectIsStackedTo5() {
		enableTalent(SHADOW_WEAVING, 5);

		player.cast(SHADOW_WORD_PAIN);
		player.cast(SHADOW_WORD_PAIN);
		player.cast(SHADOW_WORD_PAIN);
		player.cast(SHADOW_WORD_PAIN);
		player.cast(SHADOW_WORD_PAIN);
		player.cast(SHADOW_WORD_PAIN);

		updateUntil(30);

		assertEvents(
				Event::isTalentEffect,
				at(0)
						.effectApplied(SHADOW_WEAVING, target, 15),
				at(1.5)
						.effectStacked(SHADOW_WEAVING, target, 2),
				at(3)
						.effectStacked(SHADOW_WEAVING, target, 3),
				at(4.5)
						.effectStacked(SHADOW_WEAVING, target, 4),
				at(6)
						.effectStacked(SHADOW_WEAVING, target, 5),
				at(7.5)
						.effectStacked(SHADOW_WEAVING, target, 5),
				at(22.5)
						.effectExpired(SHADOW_WEAVING, target)
		);
	}

	@Test
	void damageBonusIsTakenIntoAccount() {
		enableTalent(SHADOW_WEAVING, 5);
		enableTalent(TalentId.MIND_FLAY, 1);

		player.cast(MIND_BLAST);
		player.idleUntil(Time.at(10));
		player.cast(MIND_BLAST);
		player.idleUntil(Time.at(20));
		player.cast(MIND_BLAST);
		player.idleUntil(Time.at(30));
		player.cast(MIND_BLAST);
		player.idleUntil(Time.at(40));
		player.cast(MIND_BLAST);
		player.idleUntil(Time.at(50));
		player.cast(MIND_BLAST);

		updateUntil(90);

		assertEvents(
				event -> event.isDamage() || event.isTalentEffect(),
				at(1.5)
						.decreasedResource(731, HEALTH, target, MIND_BLAST)
						.effectApplied(SHADOW_WEAVING, target, 15),
				at(11.5)
						.decreasedResource(753, HEALTH, target, MIND_BLAST)
						.effectStacked(SHADOW_WEAVING, target, 2),
				at(21.5)
						.decreasedResource(775, HEALTH, target, MIND_BLAST)
						.effectStacked(SHADOW_WEAVING, target, 3),
				at(31.5)
						.decreasedResource(797, HEALTH, target, MIND_BLAST)
						.effectStacked(SHADOW_WEAVING, target, 4),
				at(41.5)
						.decreasedResource(819, HEALTH, target, MIND_BLAST)
						.effectStacked(SHADOW_WEAVING, target, 5),
				at(51.5)
						.decreasedResource(841, HEALTH, target, MIND_BLAST)
						.effectStacked(SHADOW_WEAVING, target, 5),
				at(66.5)
						.effectExpired(SHADOW_WEAVING, target)
		);
	}
}
