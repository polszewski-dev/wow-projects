package wow.simulator.simulation.spell.tbc.ability.priest.shadow;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.tbc.TbcPriestSpellSimulationTest;
import wow.test.commons.TalentNames;

import static wow.commons.model.spell.ResourceType.HEALTH;
import static wow.commons.model.spell.ResourceType.MANA;
import static wow.test.commons.AbilityNames.*;

/**
 * User: POlszewski
 * Date: 2025-01-19
 */
class VampiricTouchTest extends TbcPriestSpellSimulationTest {
	/*
	Causes 650 Shadow damage over 15 sec to your target and causes all party members to gain mana equal to 5% of any Shadow spell damage you deal.
	 */

	@Test
	void success() {
		player.cast(VAMPIRIC_TOUCH);

		updateUntil(30);

		assertEvents(
					 at(0)
							 .beginCast(player, VAMPIRIC_TOUCH, 1.5)
							 .beginGcd(player),
					 at(1.5)
							 .endCast(player, VAMPIRIC_TOUCH)
							 .decreasedResource(425, MANA, player, VAMPIRIC_TOUCH)
							 .effectApplied(VAMPIRIC_TOUCH, target, 15)
							 .endGcd(player),
					 at(4.5)
							 .decreasedResource(130, HEALTH, target, VAMPIRIC_TOUCH)
							 .increasedResource(6, MANA, player, VAMPIRIC_TOUCH),
					 at(7.5)
							 .decreasedResource(130, HEALTH, target, VAMPIRIC_TOUCH)
							 .increasedResource(7, MANA, player, VAMPIRIC_TOUCH),
					 at(10.5)
							 .decreasedResource(130, HEALTH, target, VAMPIRIC_TOUCH)
							 .increasedResource(6, MANA, player, VAMPIRIC_TOUCH),
					 at(13.5)
							 .decreasedResource(130, HEALTH, target, VAMPIRIC_TOUCH)
							 .increasedResource(7, MANA, player, VAMPIRIC_TOUCH),
					 at(16.5)
							 .decreasedResource(130, HEALTH, target, VAMPIRIC_TOUCH)
							 .increasedResource(6, MANA, player, VAMPIRIC_TOUCH)
							 .effectExpired(VAMPIRIC_TOUCH, target)
		);
	}

	@Test
	void mindBlastDamageIsConvertedToMana() {
		player.cast(VAMPIRIC_TOUCH);
		player.cast(MIND_BLAST);

		updateUntil(60);

		assertEvents(
				event -> event.isDamage() || event.isManaGained(),
				at(3)
						.decreasedResource(731, HEALTH, target, MIND_BLAST)
						.increasedResource(36, MANA, player, VAMPIRIC_TOUCH),
				at(4.5)
						.decreasedResource(130, HEALTH, target, VAMPIRIC_TOUCH)
						.increasedResource(6, MANA, player, VAMPIRIC_TOUCH),
				at(7.5)
						.decreasedResource(130, HEALTH, target, VAMPIRIC_TOUCH)
						.increasedResource(7, MANA, player, VAMPIRIC_TOUCH),
				at(10.5)
						.decreasedResource(130, HEALTH, target, VAMPIRIC_TOUCH)
						.increasedResource(6, MANA, player, VAMPIRIC_TOUCH),
				at(13.5)
						.decreasedResource(130, HEALTH, target, VAMPIRIC_TOUCH)
						.increasedResource(7, MANA, player, VAMPIRIC_TOUCH),
				at(16.5)
						.decreasedResource(130, HEALTH, target, VAMPIRIC_TOUCH)
						.increasedResource(6, MANA, player, VAMPIRIC_TOUCH)
		);
	}

	@Test
	void shadowWordPainDamageIsConvertedToMana() {
		player.cast(VAMPIRIC_TOUCH);
		player.cast(SHADOW_WORD_PAIN);

		updateUntil(60);

		assertEvents(
				event -> event.isDamage() || event.isManaGained(),
				at(4.5)
						.decreasedResource(130, HEALTH, target, VAMPIRIC_TOUCH)
						.increasedResource(6, MANA, player, VAMPIRIC_TOUCH)
						.decreasedResource(206, HEALTH, target, SHADOW_WORD_PAIN)
						.increasedResource(10, MANA, player, VAMPIRIC_TOUCH),
				at(7.5)
						.decreasedResource(130, HEALTH, target, VAMPIRIC_TOUCH)
						.increasedResource(7, MANA, player, VAMPIRIC_TOUCH)
						.decreasedResource(206, HEALTH, target, SHADOW_WORD_PAIN)
						.increasedResource(10, MANA, player, VAMPIRIC_TOUCH),
				at(10.5)
						.decreasedResource(130, HEALTH, target, VAMPIRIC_TOUCH)
						.increasedResource(6, MANA, player, VAMPIRIC_TOUCH)
						.decreasedResource(206, HEALTH, target, SHADOW_WORD_PAIN)
						.increasedResource(10, MANA, player, VAMPIRIC_TOUCH),
				at(13.5)
						.decreasedResource(130, HEALTH, target, VAMPIRIC_TOUCH)
						.increasedResource(7, MANA, player, VAMPIRIC_TOUCH)
						.decreasedResource(206, HEALTH, target, SHADOW_WORD_PAIN)
						.increasedResource(11, MANA, player, VAMPIRIC_TOUCH),
				at(16.5)
						.decreasedResource(130, HEALTH, target, VAMPIRIC_TOUCH)
						.increasedResource(6, MANA, player, VAMPIRIC_TOUCH)
						.decreasedResource(206, HEALTH, target, SHADOW_WORD_PAIN)
						.increasedResource(10, MANA, player, VAMPIRIC_TOUCH),
				at(19.5)
						.decreasedResource(206, HEALTH, target, SHADOW_WORD_PAIN)
		);
	}

	@ParameterizedTest
	@ValueSource(ints = { 0, 100, 1000 })
	void damage_done(int spellDamage) {
		simulateDamagingSpell(VAMPIRIC_TOUCH, spellDamage);

		assertDamageDone(VAMPIRIC_TOUCH_INFO, spellDamage);
	}

	@Override
	protected void afterSetUp() {
		enableTalent(TalentNames.VAMPIRIC_TOUCH, 1);
	}
}
