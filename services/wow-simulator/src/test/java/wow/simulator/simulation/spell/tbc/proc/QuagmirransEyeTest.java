package wow.simulator.simulation.spell.tbc.proc;

import org.junit.jupiter.api.Test;
import wow.commons.model.spell.CooldownId;
import wow.commons.model.spell.SpellId;
import wow.simulator.simulation.spell.tbc.TbcWarlockSpellSimulationTest;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.categorization.ItemSlot.TRINKET_1;
import static wow.commons.model.spell.ResourceType.HEALTH;
import static wow.commons.model.spell.ResourceType.MANA;
import static wow.simulator.util.EffectType.ITEM;
import static wow.test.commons.AbilityNames.SHADOW_BOLT;
import static wow.test.commons.EffectNames.SPELL_HASTE;

/**
 * User: POlszewski
 * Date: 2024-12-02
 */
class QuagmirransEyeTest extends TbcWarlockSpellSimulationTest {
	/*
	Equip: Your harmful spells have a chance to increase your spell haste rating by 320 for 6 secs. (Proc chance: 10%, 45s cooldown)
	 */
	@Test
	void procIsTriggered() {
		eventsOnlyOnFollowingRolls(0);

		player.cast(SHADOW_BOLT);

		updateUntil(60);

		assertEvents(
				at(0)
						.beginCast(player, SHADOW_BOLT, 3)
						.beginGcd(player),
				at(1.5)
						.endGcd(player),
				at(3)
						.endCast(player, SHADOW_BOLT)
						.decreasedResource(420, MANA, player, SHADOW_BOLT)
						.cooldownStarted(player, cooldownId, 45)
						.effectApplied(SPELL_HASTE, ITEM, player, 6)
						.decreasedResource(607, HEALTH, false, target, SHADOW_BOLT),
				at(9)
						.effectExpired(SPELL_HASTE, ITEM, player),
				at(48)
						.cooldownExpired(player, cooldownId)
		);
	}

	@Test
	void modifierIsTakenIntoAccount() {
		eventsOnlyOnFollowingRolls(0);

		player.cast(SHADOW_BOLT);

		updateUntil(5);

		var hasteBefore = statsAt(0).getSpellHasteRating();
		var hasteAfter = statsAt(5).getSpellHasteRating();

		assertThat(hasteAfter).isEqualTo(hasteBefore + 320);
	}

	CooldownId cooldownId = CooldownId.of(SpellId.of(33297));

	@Override
	protected void afterSetUp() {
		equip("Quagmirran's Eye", TRINKET_1);
	}
}
