package wow.simulator.simulation.spell.talent.warlock;

import org.junit.jupiter.api.Test;
import wow.simulator.model.time.Time;
import wow.simulator.simulation.spell.SpellSimulationTest;

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
class ImprovedShadowBoltTest extends SpellSimulationTest {
	@Test
	void isbIsAppliedAfterCrit() {
		rng.critRoll = true;
		rng.eventRoll = true;

		enableTalent(IMPROVED_SHADOW_BOLT, 5);

		player.cast(SHADOW_BOLT);

		simulation.updateUntil(Time.at(30));

		assertEvents(
				at(0)
						.beginCast(player, SHADOW_BOLT)
						.beginGcd(player),
				at(1.5)
						.endGcd(player),
				at(3)
						.endCast(player, SHADOW_BOLT)
						.decreasedResource(420, MANA, player, SHADOW_BOLT)
						.decreasedResource(863, HEALTH, true, target, SHADOW_BOLT)
						.effectApplied(IMPROVED_SHADOW_BOLT, target),
				at(15)
						.effectExpired(IMPROVED_SHADOW_BOLT, target)
		);
	}

	@Test
	void sbDecreasesIsbChargesToZero() {
		rng.critRoll = true;
		rng.eventRoll = true;

		enableTalent(IMPROVED_SHADOW_BOLT, 5);

		player.cast(SHADOW_BOLT);
		player.cast(SHADOW_BOLT);
		player.cast(SHADOW_BOLT);
		player.cast(SHADOW_BOLT);
		player.cast(SHADOW_BOLT);

		simulation.updateUntil(Time.at(3));

		rng.critRoll = false;

		simulation.updateUntil(Time.at(30));

		assertEvents(
				at(0)
						.beginCast(player, SHADOW_BOLT)
						.beginGcd(player),
				at(1.5)
						.endGcd(player),
				at(3)
						.endCast(player, SHADOW_BOLT)
						.decreasedResource(420, MANA, player, SHADOW_BOLT)
						.decreasedResource(863, HEALTH, true, target, SHADOW_BOLT)
						.effectApplied(IMPROVED_SHADOW_BOLT, target)
						.beginCast(player, SHADOW_BOLT)
						.beginGcd(player),
				at(4.5)
						.endGcd(player),
				at(6)
						.endCast(player, SHADOW_BOLT)
						.decreasedResource(420, MANA, player, SHADOW_BOLT)
						.decreasedResource(690, HEALTH, false, target, SHADOW_BOLT)
						.effectChargesDecreased(IMPROVED_SHADOW_BOLT, target, 3)
						.beginCast(player, SHADOW_BOLT)
						.beginGcd(player),
				at(7.5)
						.endGcd(player),
				at(9)
						.endCast(player, SHADOW_BOLT)
						.decreasedResource(420, MANA, player, SHADOW_BOLT)
						.decreasedResource(690, HEALTH, false, target, SHADOW_BOLT)
						.effectChargesDecreased(IMPROVED_SHADOW_BOLT, target, 2)
						.beginCast(player, SHADOW_BOLT)
						.beginGcd(player),
				at(10.5)
						.endGcd(player),
				at(12)
						.endCast(player, SHADOW_BOLT)
						.decreasedResource(420, MANA, player, SHADOW_BOLT)
						.decreasedResource(690, HEALTH, false, target, SHADOW_BOLT)
						.effectChargesDecreased(IMPROVED_SHADOW_BOLT, target, 1)
						.beginCast(player, SHADOW_BOLT)
						.beginGcd(player),
				at(13.5)
						.endGcd(player),
				at(15)
						.endCast(player, SHADOW_BOLT)
						.decreasedResource(420, MANA, player, SHADOW_BOLT)
						.decreasedResource(690, HEALTH, false, target, SHADOW_BOLT)
						.effectRemoved(IMPROVED_SHADOW_BOLT, target)
		);

	}

	@Test
	void overwritingIsb() {
		rng.critRoll = true;
		rng.eventRoll = true;

		enableTalent(IMPROVED_SHADOW_BOLT, 5);

		player.cast(SHADOW_BOLT);
		player.cast(SHADOW_BOLT);

		simulation.updateUntil(Time.at(30));

		assertEvents(
				at(0)
						.beginCast(player, SHADOW_BOLT)
						.beginGcd(player),
				at(1.5)
						.endGcd(player),
				at(3)
						.endCast(player, SHADOW_BOLT)
						.decreasedResource(420, MANA, player, SHADOW_BOLT)
						.decreasedResource(863, HEALTH, true, target, SHADOW_BOLT)
						.effectApplied(IMPROVED_SHADOW_BOLT, target)
						.beginCast(player, SHADOW_BOLT)
						.beginGcd(player),
				at(4.5)
						.endGcd(player),
				at(6)
						.endCast(player, SHADOW_BOLT)
						.decreasedResource(420, MANA, player, SHADOW_BOLT)
						.decreasedResource(1035, HEALTH, true, target, SHADOW_BOLT)
						.effectChargesDecreased(IMPROVED_SHADOW_BOLT, target, 3)
						.effectRemoved(IMPROVED_SHADOW_BOLT, target)
						.effectApplied(IMPROVED_SHADOW_BOLT, target),
				at(18)
						.effectExpired(IMPROVED_SHADOW_BOLT, target)
		);
	}

	@Test
	void isbIncreasesCorruptionDamage() {
		rng.critRoll = true;
		rng.eventRoll = true;

		enableTalent(IMPROVED_CORRUPTION, 5);
		enableTalent(IMPROVED_SHADOW_BOLT, 5);

		player.cast(CORRUPTION);
		player.cast(SHADOW_BOLT);

		simulation.updateUntil(Time.at(30));

		assertEvents(
				at(0)
						.beginCast(player, CORRUPTION)
						.endCast(player, CORRUPTION)
						.decreasedResource(370, MANA, player, CORRUPTION)
						.effectApplied(CORRUPTION, target)
						.beginGcd(player),
				at(1.5)
						.endGcd(player)
						.beginCast(player, SHADOW_BOLT)
						.beginGcd(player),
				at(3)
						.endGcd(player)
						.decreasedResource(150, HEALTH, target, CORRUPTION),
				at(4.5)
						.endCast(player, SHADOW_BOLT)
						.decreasedResource(420, MANA, player, SHADOW_BOLT)
						.decreasedResource(863, HEALTH, true, target, SHADOW_BOLT)
						.effectApplied(IMPROVED_SHADOW_BOLT, target),
				at(6)
						.decreasedResource(180, HEALTH, target, CORRUPTION),
				at(9)
						.decreasedResource(180, HEALTH, target, CORRUPTION),
				at(12)
						.decreasedResource(180, HEALTH, target, CORRUPTION),
				at(15)
						.decreasedResource(180, HEALTH, target, CORRUPTION),
				at(16.5)
						.effectExpired(IMPROVED_SHADOW_BOLT, target),
				at(18)
						.decreasedResource(150, HEALTH, target, CORRUPTION)
						.effectExpired(CORRUPTION, target)
		);
	}
}
