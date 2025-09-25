package wow.simulator.simulation.spell.talent.priest.shadow;

import org.junit.jupiter.api.Test;
import wow.simulator.model.time.Time;
import wow.simulator.simulation.spell.PriestSpellSimulationTest;
import wow.simulator.util.TestEvent;
import wow.test.commons.TalentNames;

import static wow.commons.model.spell.ResourceType.HEALTH;
import static wow.simulator.util.EffectType.TALENT;
import static wow.test.commons.AbilityNames.MIND_BLAST;
import static wow.test.commons.AbilityNames.SHADOW_WORD_PAIN;
import static wow.test.commons.TalentNames.SHADOW_WEAVING;

/**
 * User: POlszewski
 * Date: 2024-11-16
 */
class ShadowWeavingTest extends PriestSpellSimulationTest {
	/*
	Your Shadow damage spells have a 100% chance to cause your target to be vulnerable to Shadow damage.
	This vulnerability increases the Shadow damage dealt to your target by 2% and lasts 15 sec. Stacks up to 5 times.
	 */

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
				TestEvent::isTalentEffect,
				at(0)
						.effectApplied(SHADOW_WEAVING, TALENT, target, 15),
				at(1.5)
						.effectStacked(SHADOW_WEAVING, TALENT, target, 2),
				at(3)
						.effectStacked(SHADOW_WEAVING, TALENT, target, 3),
				at(4.5)
						.effectStacked(SHADOW_WEAVING, TALENT, target, 4),
				at(6)
						.effectStacked(SHADOW_WEAVING, TALENT, target, 5),
				at(7.5)
						.effectStacked(SHADOW_WEAVING, TALENT, target, 5),
				at(22.5)
						.effectExpired(SHADOW_WEAVING, TALENT, target)
		);
	}

	@Test
	void damageBonusIsTakenIntoAccount() {
		enableTalent(SHADOW_WEAVING, 5);
		enableTalent(TalentNames.MIND_FLAY, 1);

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
						.effectApplied(SHADOW_WEAVING, TALENT, target, 15)
						.decreasedResource(746, HEALTH, target, MIND_BLAST),
				at(11.5)
						.effectStacked(SHADOW_WEAVING, TALENT, target, 2)
						.decreasedResource(760, HEALTH, target, MIND_BLAST),
				at(21.5)
						.effectStacked(SHADOW_WEAVING, TALENT, target, 3)
						.decreasedResource(775, HEALTH, target, MIND_BLAST),
				at(31.5)
						.effectStacked(SHADOW_WEAVING, TALENT, target, 4)
						.decreasedResource(790, HEALTH, target, MIND_BLAST),
				at(41.5)
						.effectStacked(SHADOW_WEAVING, TALENT, target, 5)
						.decreasedResource(804, HEALTH, target, MIND_BLAST),
				at(51.5)
						.effectStacked(SHADOW_WEAVING, TALENT, target, 5)
						.decreasedResource(804, HEALTH, target, MIND_BLAST),
				at(66.5)
						.effectExpired(SHADOW_WEAVING, TALENT, target)
		);
	}
}
