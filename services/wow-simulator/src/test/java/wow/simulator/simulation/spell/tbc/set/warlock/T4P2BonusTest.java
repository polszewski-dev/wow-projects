package wow.simulator.simulation.spell.tbc.set.warlock;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.spell.tbc.TbcWarlockSpellSimulationTest;

import static wow.commons.model.spell.ResourceType.HEALTH;
import static wow.commons.model.spell.ResourceType.MANA;
import static wow.simulator.util.EffectType.ITEM_SET;
import static wow.test.commons.AbilityNames.INCINERATE;
import static wow.test.commons.AbilityNames.SHADOW_BOLT;
import static wow.test.commons.EffectNames.FLAMESHADOW;
import static wow.test.commons.EffectNames.SHADOWFLAME;

/**
 * User: POlszewski
 * Date: 2024-12-01
 */
class T4P2BonusTest extends TbcWarlockSpellSimulationTest {
	/*
	Your shadow damage spells have a chance to grant you 135 bonus shadow damage for 15 sec.
	Your fire damage spells have a chance to grant you 135 bonus fire damage for 15 sec.
	 */

	@Test
	void shadowSpellTriggersShadowDamageBonus() {
		eventsOnlyOnFollowingRolls(0);

		player.cast(SHADOW_BOLT);

		updateUntil(30);

		assertEvents(
			at(0)
					.beginCast(player, SHADOW_BOLT, 3)
					.beginGcd(player),
			at(1.5)
					.endGcd(player),
			at(3)
					.endCast(player, SHADOW_BOLT)
					.decreasedResource(420, MANA, player, SHADOW_BOLT)
					.effectApplied(FLAMESHADOW, ITEM_SET, player, 15)
					.decreasedResource(755, HEALTH, target, SHADOW_BOLT),
			at(18)
					.effectExpired(FLAMESHADOW, ITEM_SET, player)
		);
	}

	@Test
	void fireSpellTriggersFireDamageBonus() {
		eventsOnlyOnFollowingRolls(0);

		player.cast(INCINERATE);

		updateUntil(30);

		assertEvents(
				at(0)
						.beginCast(player, INCINERATE, 2.5)
						.beginGcd(player),
				at(1.5)
						.endGcd(player),
				at(2.5)
						.endCast(player, INCINERATE)
						.decreasedResource(355, MANA, player, INCINERATE)
						.effectApplied(SHADOWFLAME, ITEM_SET, player, 15)
						.decreasedResource(629, HEALTH, target, INCINERATE),
				at(17.5)
						.effectExpired(SHADOWFLAME, ITEM_SET, player)
		);
	}

	@Test
	void onlyShadowSpellDamageIncreased() {
		eventsOnlyOnFollowingRolls(0);

		player.cast(SHADOW_BOLT);
		player.cast(INCINERATE);
		player.cast(SHADOW_BOLT);
		player.cast(INCINERATE);

		updateUntil(30);

		assertDamageDone(0, SHADOW_BOLT, SHADOW_BOLT_INFO.damage(totalShadowSpellDamage + 135));
		assertDamageDone(0, INCINERATE, INCINERATE_INFO.damage(totalFireSpellDamage));
		assertDamageDone(1, SHADOW_BOLT, SHADOW_BOLT_INFO.damage(totalShadowSpellDamage + 135));
		assertDamageDone(1, INCINERATE, INCINERATE_INFO.damage(totalFireSpellDamage));
	}

	@Test
	void onlyFireSpellDamageIncreased() {
		eventsOnlyOnFollowingRolls(1);

		player.cast(SHADOW_BOLT);
		player.cast(INCINERATE);
		player.cast(SHADOW_BOLT);
		player.cast(INCINERATE);

		updateUntil(30);

		assertDamageDone(0, SHADOW_BOLT, SHADOW_BOLT_INFO.damage(totalShadowSpellDamage));
		assertDamageDone(0, INCINERATE, INCINERATE_INFO.damage(totalFireSpellDamage + 135));
		assertDamageDone(1, SHADOW_BOLT, SHADOW_BOLT_INFO.damage(totalShadowSpellDamage));
		assertDamageDone(1, INCINERATE, INCINERATE_INFO.damage(totalFireSpellDamage + 135));
	}

	@Override
	protected void afterSetUp() {
		equip("Voidheart Crown");
		equip("Voidheart Gloves");
	}
}
