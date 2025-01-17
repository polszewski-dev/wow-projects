package wow.simulator.simulation.spell.proc;

import org.junit.jupiter.api.Test;
import wow.commons.model.spell.CooldownId;
import wow.simulator.model.time.Time;
import wow.simulator.simulation.spell.WarlockSpellSimulationTest;

import static wow.commons.model.categorization.ItemSlot.TRINKET_1;
import static wow.commons.model.spell.AbilityId.CORRUPTION;
import static wow.commons.model.spell.ResourceType.HEALTH;
import static wow.commons.model.spell.ResourceType.MANA;
import static wow.commons.model.talent.TalentId.IMPROVED_CORRUPTION;

/**
 * User: POlszewski
 * Date: 2024-12-02
 */
class TimbalsFocusingCrystalTest extends WarlockSpellSimulationTest {
	/*
	Equip: Each time one of your spells deals periodic damage, there is a chance 285 to 475 additional damage will be dealt.
	(Proc chance: 10%, 15s cooldown)
	 */
	@Test
	void procIsTriggered() {
		rng.eventRoll = true;

		enableTalent(IMPROVED_CORRUPTION, 5);

		player.cast(CORRUPTION);

		simulation.updateUntil(Time.at(30));

		assertEvents(
				at(0)
						.beginCast(player, CORRUPTION)
						.endCast(player, CORRUPTION)
						.decreasedResource(370, MANA, player, CORRUPTION)
						.effectApplied(CORRUPTION, target)
						.beginGcd(player),
				at(1.5)
						.endGcd(player),
				at(3)
						.decreasedResource(156, HEALTH, target, CORRUPTION)
						.cooldownStarted(player, cooldownId, 15)
						.decreasedResource(380, HEALTH, target, "Timbal's Focusing Crystal - proc #1 - triggered"),
				at(6)
						.decreasedResource(157, HEALTH, target, CORRUPTION),
				at(9)
						.decreasedResource(157, HEALTH, target, CORRUPTION),
				at(12)
						.decreasedResource(157, HEALTH, target, CORRUPTION),
				at(15)
						.decreasedResource(157, HEALTH, target, CORRUPTION),
				at(18)
						.cooldownExpired(player, cooldownId)
						.decreasedResource(157, HEALTH, target, CORRUPTION)
						.cooldownStarted(player, cooldownId, 15)
						.decreasedResource(380, HEALTH, target, "Timbal's Focusing Crystal - proc #1 - triggered")
						.effectExpired(CORRUPTION, target)
		);
	}

	CooldownId cooldownId = CooldownId.of(100134470);

	@Override
	protected void afterSetUp() {
		equip("Timbal's Focusing Crystal", TRINKET_1);
	}
}
