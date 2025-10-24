package wow.simulator.simulation.spell.tbc.proc;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.spell.tbc.TbcWarlockSpellSimulationTest;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.categorization.ItemSlot.TRINKET_1;
import static wow.commons.model.spell.ResourceType.MANA;
import static wow.simulator.util.EffectType.ITEM;
import static wow.test.commons.AbilityNames.SHADOW_BOLT;

/**
 * User: POlszewski
 * Date: 2024-12-02
 */
class EyeOfMagtheridonTest extends TbcWarlockSpellSimulationTest {
	/*
	Equip: Grants 170 increased spell damage for 10 sec when one of your spells is resisted.
	 */
	@Test
	void procIsTriggered() {
		missesOnlyOnFollowingRolls(0);

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
						.spellResisted(player, SHADOW_BOLT, target)
						.effectApplied("Eye of Magtheridon", ITEM, player, 10),
				at(13)
						.effectExpired("Eye of Magtheridon", ITEM, player)
		);
	}

	@Test
	void modifierIsTakenIntoAccount() {
		missesOnlyOnFollowingRolls(0);

		player.cast(SHADOW_BOLT);

		updateUntil(10);

		var dmgBefore = statsAt(0).getSpellDamage();
		var dmgAfter = statsAt(10).getSpellDamage();

		assertThat(dmgAfter).isEqualTo(dmgBefore + 170);
	}

	@Override
	protected void afterSetUp() {
		equip("Eye of Magtheridon", TRINKET_1);
	}
}
