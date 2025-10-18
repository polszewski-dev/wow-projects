package wow.simulator.simulation;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * User: POlszewski
 * Date: 2025-10-18
 */
class MaxHealthTest extends ResourceTest {
	@Test
	void after_effect_is_applied_current_health_remains_the_same() {
		runAt(10, () -> addEffect("Bonus Stamina"));

		updateUntil(60);

		var currentHealthBefore = currentHealthAt(9);
		var currentHealthAfter = currentHealthAt(11);

		assertThat(currentHealthAfter).isEqualTo(currentHealthBefore);
	}

	@Test
	void after_effect_is_applied_max_health_is_increased() {
		runAt(10, () -> addEffect("Bonus Stamina"));

		updateUntil(60);

		var maxHealthBefore = maxHealthAt(9);
		var maxHealthAfter = maxHealthAt(11);

		assertThat(maxHealthAfter).isEqualTo(maxHealthBefore + 100);
	}

	@Test
	void after_effect_expires_current_health_below_max_remains_the_same() {
		runAt(10, () -> addEffect("Bonus Stamina"));

		updateUntil(60);

		var currentHealthBefore = currentHealthAt(39);
		var currentHealthAfter = currentHealthAt(41);

		assertThat(currentHealthAfter).isEqualTo(currentHealthBefore);
	}

	@Test
	void after_effect_expires_maxed_current_health_is_decreased() {
		runAt(10, () -> addEffect("Bonus Stamina"));
		runAt(11, () -> player.setHealthToMax());

		updateUntil(60);

		var currentHealthBefore = currentHealthAt(39);
		var currentHealthAfter = currentHealthAt(41);

		assertThat(currentHealthAfter).isEqualTo(currentHealthBefore - 100);
	}

	@Test
	void after_effect_expires_max_health_is_decreased() {
		runAt(10, () -> addEffect("Bonus Stamina"));

		updateUntil(60);

		var maxHealthBefore = maxHealthAt(39);
		var maxHealthAfter = maxHealthAt(41);

		assertThat(maxHealthAfter).isEqualTo(maxHealthBefore - 100);
	}

	@Test
	void stacking_effect_updates_max_health() {
		runAt(10, () -> addEffect("Bonus Stamina"));
		runAt(20, () -> addEffect("Bonus Stamina"));

		updateUntil(60);

		assertThat(maxHealthAt(11)).isEqualTo(maxHealthAt(9) + 100);
		assertThat(maxHealthAt(21)).isEqualTo(maxHealthAt(11) + 100);
	}

	@Test
	void removing_effect_updates_max_health() {
		runAt(10, () -> addEffect("Bonus Stamina"));
		runAt(20, () -> getEffect("Bonus Stamina").removeSelf());

		updateUntil(60);

		assertThat(maxHealthAt(11)).isEqualTo(maxHealthAt(9) + 100);
		assertThat(maxHealthAt(21)).isEqualTo(maxHealthAt(9));
	}

	@Test
	void increasing_effect_stacks_updates_max_health() {
		addEffect("Bonus Stamina");

		runAt(10, () -> getEffect("Bonus Stamina").addStack());

		updateUntil(60);

		assertThat(maxHealthAt(11)).isEqualTo(maxHealthAt(9) + 10);
	}

	@Test
	void decreasing_effect_stacks_updates_max_health() {
		addEffect("Bonus Stamina");

		runAt(10, () -> getEffect("Bonus Stamina").removeStack());

		updateUntil(60);

		assertThat(maxHealthAt(11)).isEqualTo(maxHealthAt(9) - 10);
	}
}
