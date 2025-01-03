package wow.simulator.simulation.spell.talent.priest;

import org.junit.jupiter.api.Test;
import wow.commons.model.character.CharacterClassId;
import wow.commons.model.talent.TalentId;
import wow.simulator.model.time.Time;
import wow.simulator.simulation.spell.SpellSimulationTest;

import static wow.commons.model.spell.AbilityId.MIND_BLAST;
import static wow.commons.model.spell.AbilityId.SHADOW_WORD_PAIN;
import static wow.commons.model.spell.ResourceType.HEALTH;
import static wow.commons.model.talent.TalentId.SHADOW_WEAVING;
import static wow.simulator.WowSimulatorSpringTest.EventCollectingHandler.Event;

/**
 * User: POlszewski
 * Date: 2024-11-16
 */
class ShadowWeavingTest extends SpellSimulationTest {
	@Test
	void effectIsStackedTo5() {
		rng.eventRoll = true;

		enableTalent(SHADOW_WEAVING, 5);

		player.cast(SHADOW_WORD_PAIN);
		player.cast(SHADOW_WORD_PAIN);
		player.cast(SHADOW_WORD_PAIN);
		player.cast(SHADOW_WORD_PAIN);
		player.cast(SHADOW_WORD_PAIN);
		player.cast(SHADOW_WORD_PAIN);

		simulation.updateUntil(Time.at(30));

		assertEvents(
				Event::isTalentEffect,
				at(0)
						.effectApplied(SHADOW_WEAVING, target),
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
		rng.eventRoll = true;

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

		simulation.updateUntil(Time.at(90));

		assertEvents(
				event -> event.isDamage() || event.isTalentEffect(),
				at(1.5)
						.decreasedResource(731, HEALTH, target, MIND_BLAST)
						.effectApplied(SHADOW_WEAVING, target),
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

	@Override
	protected void beforeSetUp() {
		characterClassId = CharacterClassId.PRIEST;
	}
}
