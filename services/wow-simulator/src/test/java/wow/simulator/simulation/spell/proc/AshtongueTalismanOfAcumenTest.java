package wow.simulator.simulation.spell.proc;

import org.junit.jupiter.api.Test;
import wow.commons.model.character.CharacterClassId;
import wow.simulator.model.time.Time;
import wow.simulator.simulation.spell.SpellSimulationTest;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.categorization.ItemSlot.TRINKET_1;
import static wow.commons.model.spell.AbilityId.SHADOW_WORD_PAIN;
import static wow.commons.model.spell.ResourceType.HEALTH;
import static wow.commons.model.spell.ResourceType.MANA;

/**
 * User: POlszewski
 * Date: 2024-12-01
 */
class AshtongueTalismanOfAcumenTest extends SpellSimulationTest {
	/*
	Equip: Each time your Shadow Word: Pain deals damage, it has a 10% chance to grant you 220 spell damage for 10 sec 
	and each time your Renew heals, it has a 10% chance to grant you 220 healing for 5 sec. (Proc chance: 10%)
	 */
	@Test
	void procIsTriggered() {
		rng.eventRoll = true;

		player.cast(SHADOW_WORD_PAIN);

		simulation.updateUntil(Time.at(30));

		assertEvents(
				at(0)
						.beginCast(player, SHADOW_WORD_PAIN)
						.endCast(player, SHADOW_WORD_PAIN)
						.decreasedResource(575, MANA, player, SHADOW_WORD_PAIN)
						.effectApplied(SHADOW_WORD_PAIN, target)
						.beginGcd(player),
				at(1.5)
						.endGcd(player),
				at(3)
						.decreasedResource(206, HEALTH, target, SHADOW_WORD_PAIN)
						.effectApplied("Ashtongue Talisman of Acumen", player),
				at(6)
						.decreasedResource(246, HEALTH, target, SHADOW_WORD_PAIN)
						.effectRemoved("Ashtongue Talisman of Acumen", player)
						.effectApplied("Ashtongue Talisman of Acumen", player),
				at(9)
						.decreasedResource(246, HEALTH, target, SHADOW_WORD_PAIN)
						.effectRemoved("Ashtongue Talisman of Acumen", player)
						.effectApplied("Ashtongue Talisman of Acumen", player),
				at(12)
						.decreasedResource(247, HEALTH, target, SHADOW_WORD_PAIN)
						.effectRemoved("Ashtongue Talisman of Acumen", player)
						.effectApplied("Ashtongue Talisman of Acumen", player),
				at(15)
						.decreasedResource(246, HEALTH, target, SHADOW_WORD_PAIN)
						.effectRemoved("Ashtongue Talisman of Acumen", player)
						.effectApplied("Ashtongue Talisman of Acumen", player),
				at(18)
						.decreasedResource(246, HEALTH, target, SHADOW_WORD_PAIN)
						.effectRemoved("Ashtongue Talisman of Acumen", player)
						.effectApplied("Ashtongue Talisman of Acumen", player)
						.effectExpired(SHADOW_WORD_PAIN, target),
				at(28)
						.effectExpired("Ashtongue Talisman of Acumen", player)
		);
	}

	@Test
	void modifierIsTakenIntoAccount() {
		var dmgBefore = player.getStats().getSpellDamage();

		rng.eventRoll = true;
		player.cast(SHADOW_WORD_PAIN);
		simulation.updateUntil(Time.at(10));

		var dmgAfter = player.getStats().getSpellDamage();

		assertThat(dmgAfter).isEqualTo(dmgBefore + 220);
	}

	@Override
	protected void beforeSetUp() {
		characterClassId = CharacterClassId.PRIEST;
	}

	@Override
	protected void afterSetUp() {
		equip("Ashtongue Talisman of Acumen", TRINKET_1);
	}
}
