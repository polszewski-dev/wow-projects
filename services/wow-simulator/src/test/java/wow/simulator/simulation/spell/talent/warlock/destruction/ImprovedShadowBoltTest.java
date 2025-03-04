package wow.simulator.simulation.spell.talent.warlock.destruction;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.spell.WarlockSpellSimulationTest;

import static wow.commons.model.spell.AbilityId.CORRUPTION;
import static wow.commons.model.spell.AbilityId.SHADOW_BOLT;
import static wow.commons.model.spell.ResourceType.HEALTH;
import static wow.commons.model.spell.ResourceType.MANA;
import static wow.commons.model.talent.TalentId.IMPROVED_CORRUPTION;
import static wow.commons.model.talent.TalentId.IMPROVED_SHADOW_BOLT;

/**
 * User: POlszewski
 * Date: 2024-11-14
 */
class ImprovedShadowBoltTest extends WarlockSpellSimulationTest {
	/*
	Your Shadow Bolt critical strikes increase Shadow damage dealt to the target by 20% until 4 non-periodic damage sources are applied. Effect lasts a maximum of 12 sec.
	 */

	@Test
	void isbIsAppliedAfterCrit() {
		critsOnlyOnFollowingRolls(0);

		enableTalent(IMPROVED_SHADOW_BOLT, 5);

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
						.decreasedResource(863, HEALTH, true, target, SHADOW_BOLT)
						.effectApplied(IMPROVED_SHADOW_BOLT, target, 12),
				at(15)
						.effectExpired(IMPROVED_SHADOW_BOLT, target)
		);
	}

	@Test
	void sbDecreasesIsbChargesToZero() {
		critsOnlyOnFollowingRolls(0);

		enableTalent(IMPROVED_SHADOW_BOLT, 5);

		player.cast(SHADOW_BOLT);
		player.cast(SHADOW_BOLT);
		player.cast(SHADOW_BOLT);
		player.cast(SHADOW_BOLT);
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
						.decreasedResource(863, HEALTH, true, target, SHADOW_BOLT)
						.effectApplied(IMPROVED_SHADOW_BOLT, target, 12)
						.beginCast(player, SHADOW_BOLT, 3)
						.beginGcd(player),
				at(4.5)
						.endGcd(player),
				at(6)
						.endCast(player, SHADOW_BOLT)
						.decreasedResource(420, MANA, player, SHADOW_BOLT)
						.decreasedResource(690, HEALTH, false, target, SHADOW_BOLT)
						.effectChargesDecreased(IMPROVED_SHADOW_BOLT, target, 3)
						.beginCast(player, SHADOW_BOLT, 3)
						.beginGcd(player),
				at(7.5)
						.endGcd(player),
				at(9)
						.endCast(player, SHADOW_BOLT)
						.decreasedResource(420, MANA, player, SHADOW_BOLT)
						.decreasedResource(690, HEALTH, false, target, SHADOW_BOLT)
						.effectChargesDecreased(IMPROVED_SHADOW_BOLT, target, 2)
						.beginCast(player, SHADOW_BOLT, 3)
						.beginGcd(player),
				at(10.5)
						.endGcd(player),
				at(12)
						.endCast(player, SHADOW_BOLT)
						.decreasedResource(420, MANA, player, SHADOW_BOLT)
						.decreasedResource(690, HEALTH, false, target, SHADOW_BOLT)
						.effectChargesDecreased(IMPROVED_SHADOW_BOLT, target, 1)
						.beginCast(player, SHADOW_BOLT, 3)
						.beginGcd(player),
				at(13.5)
						.endGcd(player),
				at(15)
						.effectExpired(IMPROVED_SHADOW_BOLT, target)
						.endCast(player, SHADOW_BOLT)
						.decreasedResource(420, MANA, player, SHADOW_BOLT)
						.decreasedResource(690, HEALTH, false, target, SHADOW_BOLT)
		);

	}

	@Test
	void overwritingIsb() {
		critsOnlyOnFollowingRolls(0, 1);

		enableTalent(IMPROVED_SHADOW_BOLT, 5);

		player.cast(SHADOW_BOLT);
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
						.decreasedResource(863, HEALTH, true, target, SHADOW_BOLT)
						.effectApplied(IMPROVED_SHADOW_BOLT, target, 12)
						.beginCast(player, SHADOW_BOLT, 3)
						.beginGcd(player),
				at(4.5)
						.endGcd(player),
				at(6)
						.endCast(player, SHADOW_BOLT)
						.decreasedResource(420, MANA, player, SHADOW_BOLT)
						.decreasedResource(1035, HEALTH, true, target, SHADOW_BOLT)
						.effectChargesDecreased(IMPROVED_SHADOW_BOLT, target, 3)
						.effectRemoved(IMPROVED_SHADOW_BOLT, target)
						.effectApplied(IMPROVED_SHADOW_BOLT, target, 12),
				at(18)
						.effectExpired(IMPROVED_SHADOW_BOLT, target)
		);
	}

	@Test
	void isbIncreasesCorruptionDamage() {
		critsOnlyOnFollowingRolls(0);

		enableTalent(IMPROVED_CORRUPTION, 5);
		enableTalent(IMPROVED_SHADOW_BOLT, 5);

		player.cast(SHADOW_BOLT);
		player.cast(CORRUPTION);

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
						.decreasedResource(863, HEALTH, true, target, SHADOW_BOLT)
						.effectApplied(IMPROVED_SHADOW_BOLT, target, 12)
						.beginCast(player, CORRUPTION)
						.beginGcd(player)
						.endCast(player, CORRUPTION)
						.decreasedResource(370, MANA, player, CORRUPTION)
						.effectApplied(CORRUPTION, target, 18),
				at(4.5)
						.endGcd(player),
				at(6)
						.decreasedResource(180, HEALTH, target, CORRUPTION),
				at(9)
						.decreasedResource(180, HEALTH, target, CORRUPTION),
				at(12)
						.decreasedResource(180, HEALTH, target, CORRUPTION),
				at(15)
						.effectExpired(IMPROVED_SHADOW_BOLT, target)
						.decreasedResource(180, HEALTH, target, CORRUPTION),
				at(18)
						.decreasedResource(180, HEALTH, target, CORRUPTION),
				at(21)
						.decreasedResource(180, HEALTH, target, CORRUPTION)
						.effectExpired(CORRUPTION, target)
		);
	}
}
