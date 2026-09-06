package wow.simulator.simulation.spell.tbc.set.mage;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.spell.tbc.TbcMageSpellSimulationTest;

import static wow.commons.model.spell.ResourceType.HEALTH;
import static wow.commons.model.spell.ResourceType.MANA;
import static wow.simulator.util.EffectType.ITEM_SET;
import static wow.test.commons.AbilityNames.SCORCH;

/**
 * User: POlszewski
 * Date: 2026-09-06
 */
class T5P4BonusTest extends TbcMageSpellSimulationTest {
	/*
	Your spell critical strikes grant you up to 70 spell damage for 6 sec.
	 */

	@Test
	void proc_is_triggered_on_spell_crit() {
		critsOnlyOnFollowingRolls(0);

		player.cast(SCORCH);

		updateUntil(30);

		assertEvents(
				at(0)
						.beginCast(player, SCORCH, 1.5)
						.beginGcd(player),
				at(1.5)
						.endCast(player, SCORCH)
						.decreasedResource(180, MANA, player, SCORCH)
						.decreasedResource(621, HEALTH, true, target, SCORCH)
						.effectApplied("Tirisfal Regalia - P4 bonus - triggered", ITEM_SET, player, 6)
						.endGcd(player),
				at(7.5)
						.effectExpired("Tirisfal Regalia - P4 bonus - triggered", ITEM_SET, player)
		);
	}

	@Test
	void proc_increases_spell_damage() {
		critsOnlyOnFollowingRolls(0);

		player.cast(SCORCH);
		player.immediateAction(self -> spellDamage = self.getStats().getSpellDamage());

		updateUntil(30);

		int baselineSpellDamage = baseline.getStats().getSpellDamage();

		assertIsIncreasedBy(spellDamage, baselineSpellDamage, 70);
	}

	int spellDamage;

	@Override
	protected void afterSetUp() {
		equip("Gloves of Tirisfal");
		equip("Leggings of Tirisfal");
		equip("Mantle of Tirisfal");
		equip("Robes of Tirisfal");
	}
}
