package wow.simulator.simulation;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * User: POlszewski
 * Date: 2025-10-18
 */
class MaxManaTest extends ResourceTest {
	@Test
	void after_effect_is_applied_current_mana_remains_the_same() {
		runAt(10, () -> addEffect("Bonus Intellect"));

		updateUntil(60);

		var currentManaBefore = currentManaAt(9);
		var currentManaAfter = currentManaAt(11);
		var regeneratedMana = regeneratedManaAt(11);

		assertThat(currentManaAfter).isEqualTo(currentManaBefore + regeneratedMana);
	}

	@Test
	void after_effect_is_applied_max_mana_is_increased() {
		runAt(10, () -> addEffect("Bonus Intellect"));

		updateUntil(60);

		var maxManaBefore = maxManaAt(9);
		var maxManaAfter = maxManaAt(11);

		assertThat(maxManaAfter).isEqualTo(maxManaBefore + 150);
	}

	@Test
	void after_effect_expires_current_mana_below_max_remains_the_same() {
		runAt(10, () -> addEffect("Bonus Intellect"));
		runAt(38.5, () -> player.setCurrentMana(2000));

		updateUntil(60);

		var currentManaBefore = currentManaAt(39);
		var currentManaAfter = currentManaAt(41);
		var regeneratedMana = regeneratedManaAt(41) - regeneratedManaAt(39);

		assertThat(currentManaAfter).isEqualTo(currentManaBefore + regeneratedMana);
	}

	@Test
	void after_effect_expires_maxed_current_mana_is_decreased() {
		runAt(10, () -> addEffect("Bonus Intellect"));
		runAt(11, () -> player.setManaToMax());

		updateUntil(60);

		var currentManaBefore = currentManaAt(39);
		var currentManaAfter = currentManaAt(41);

		assertThat(currentManaAfter).isEqualTo(currentManaBefore - 150);
	}

	@Test
	void after_effect_expires_max_mana_is_decreased() {
		runAt(10, () -> addEffect("Bonus Intellect"));

		updateUntil(60);

		var maxManaBefore = maxManaAt(39);
		var maxManaAfter = maxManaAt(41);

		assertThat(maxManaAfter).isEqualTo(maxManaBefore - 150);
	}

	@Test
	void stacking_effect_updates_max_mana() {
		runAt(10, () -> addEffect("Bonus Intellect"));
		runAt(20, () -> addEffect("Bonus Intellect"));

		updateUntil(60);

		assertThat(maxManaAt(11)).isEqualTo(maxManaAt(9) + 150);
		assertThat(maxManaAt(21)).isEqualTo(maxManaAt(11) + 150);
	}

	@Test
	void removing_effect_updates_max_mana() {
		runAt(10, () -> addEffect("Bonus Intellect"));
		runAt(20, () -> getEffect("Bonus Intellect").removeSelf());

		updateUntil(60);

		assertThat(maxManaAt(11)).isEqualTo(maxManaAt(9) + 150);
		assertThat(maxManaAt(21)).isEqualTo(maxManaAt(9));
	}

	@Test
	void increasing_effect_stacks_updates_max_mana() {
		addEffect("Bonus Intellect");

		runAt(10, () -> getEffect("Bonus Intellect").addStack());

		updateUntil(60);

		assertThat(maxManaAt(11)).isEqualTo(maxManaAt(9) + 15);
	}

	@Test
	void decreasing_effect_stacks_updates_max_mana() {
		addEffect("Bonus Intellect");

		runAt(10, () -> getEffect("Bonus Intellect").removeStack());

		updateUntil(60);

		assertThat(maxManaAt(11)).isEqualTo(maxManaAt(9) - 15);
	}
}
