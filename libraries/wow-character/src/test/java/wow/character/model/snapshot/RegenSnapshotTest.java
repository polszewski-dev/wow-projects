package wow.character.model.snapshot;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import wow.commons.model.Duration;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * User: POlszewski
 * Date: 2025-10-19
 */
class RegenSnapshotTest {
	@Test
	void out_of_combat_health_regen() {
		var result = snapshot.getHealthToRegen(false, sinceLastRegen);

		assertThat(result).isEqualTo(8);
	}


	@Test
	void in_combat_health_regen() {
		var result = snapshot.getHealthToRegen(true, sinceLastRegen);

		assertThat(result).isEqualTo(4);
	}

	@Test
	void uninterrupted_mana_regen_no_mana_spent_earlier() {
		var result = snapshot.getManaToRegen(null, sinceLastRegen);

		assertThat(result).isEqualTo(16);
	}

	@Test
	void uninterrupted_mana_regen_mana_spent_earlier() {
		var result = snapshot.getManaToRegen(Duration.seconds(10), sinceLastRegen);

		assertThat(result).isEqualTo(16);
	}

	@Test
	void interrupted_mana_regen() {
		var result = snapshot.getManaToRegen(Duration.seconds(1), sinceLastRegen);

		assertThat(result).isEqualTo(12);
	}

	RegenSnapshot snapshot;
	final Duration sinceLastRegen = Duration.seconds(2);

	@BeforeEach
	void setUp() {
		snapshot = new RegenSnapshot();
		snapshot.setOutOfCombatHealthRegen(20);
		snapshot.setInCombatHealthRegen(10);
		snapshot.setUninterruptedManaRegen(40);
		snapshot.setInterruptedManaRegen(30);
	}
}