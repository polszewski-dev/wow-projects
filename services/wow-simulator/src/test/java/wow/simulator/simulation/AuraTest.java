package wow.simulator.simulation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import wow.commons.model.categorization.ItemSlot;
import wow.simulator.WowSimulatorSpringTest;
import wow.simulator.model.unit.Player;
import wow.simulator.util.TestEventCollectingHandler;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.character.CharacterClassId.WARLOCK;

/**
 * User: POlszewski
 * Date: 2025-02-22
 */
class AuraTest extends WowSimulatorSpringTest {
	@Test
	void correctValueOfSpellPowerAuraForOwner() {
		assertThat(player.getStats().getSpellDamage()).isEqualTo(183);
	}

	@Test
	void correctValueOfSpellPowerAuraForMemberOfTheSameParty() {
		assertThat(player2.getStats().getSpellDamage()).isEqualTo(33);
	}

	@Test
	void correctValueOfSpellPowerAuraForMemberOfAnotherParty() {
		assertThat(player3.getStats().getSpellDamage()).isZero();
	}

	Player player2;
	Player player3;

	@BeforeEach
	void setUp() {
		setupTestObjects();

		handler = new TestEventCollectingHandler();
		simulation.addHandler(handler);

		player2 = getNakedPlayer(WARLOCK, "Player2");
		player3 = getNakedPlayer(WARLOCK, "Player3");

		player.getParty().add(player2);
		player.getRaid().addNewParty();
		player.getRaid().getParty(1).add(player3);

		simulation.add(player);
		simulation.add(player2);
		simulation.add(target);

		equip(22630, ItemSlot.MAIN_HAND);
	}
}
