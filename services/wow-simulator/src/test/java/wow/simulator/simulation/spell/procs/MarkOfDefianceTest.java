package wow.simulator.simulation.spell.procs;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import wow.character.model.equipment.EquippableItem;
import wow.commons.model.item.ItemSource;
import wow.commons.model.spell.CooldownId;
import wow.simulator.model.time.Time;
import wow.simulator.simulation.spell.SpellSimulationTest;

import static wow.commons.model.categorization.ItemSlot.TRINKET_1;
import static wow.commons.model.pve.PhaseId.TBC_P5;
import static wow.commons.model.spell.AbilityId.SHADOW_BOLT;
import static wow.commons.model.spell.ResourceType.HEALTH;
import static wow.commons.model.spell.ResourceType.MANA;

/**
 * User: POlszewski
 * Date: 2024-11-16
 */
class MarkOfDefianceTest extends SpellSimulationTest {
	@Test
	void eventAndItsCooldownAreTriggered() {
		rng.eventRoll = true;

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
						.decreasedResource(602, HEALTH, false, target, SHADOW_BOLT)
						.cooldownStarted(player, cooldownId)
						.increasedResource(150, MANA, player, "Mark of Defiance - proc #1 - triggered"),
				at(20)
						.cooldownExpired(player, cooldownId)
		);
	}

	@Test
	void cooldownIsTriggeredAfterItExpires() {
		rng.eventRoll = true;

		player.cast(SHADOW_BOLT);
		player.cast(SHADOW_BOLT);
		player.cast(SHADOW_BOLT);
		player.cast(SHADOW_BOLT);
		player.cast(SHADOW_BOLT);
		player.cast(SHADOW_BOLT);
		player.cast(SHADOW_BOLT);

		simulation.updateUntil(Time.at(30));

		assertEvents(
				event -> event.isCooldown(),
				at(3)
						.cooldownStarted(player, cooldownId),
				at(20)
						.cooldownExpired(player, cooldownId),
				at(21)
						.cooldownStarted(player, cooldownId)
		);
	}

	CooldownId cooldownId;

	@BeforeEach
	public void setUp() {
		super.setUp();

		var item = getItemRepository().getItem(27924, TBC_P5).orElseThrow();
		player.equip(new EquippableItem(item), TRINKET_1);

		cooldownId = CooldownId.of(new ItemSource(item), 0);
	}
}
