package wow.simulator.simulation;

import wow.commons.model.Duration;
import wow.simulator.model.effect.EffectInstance;
import wow.simulator.simulation.spell.PriestSpellSimulationTest;

/**
 * User: POlszewski
 * Date: 2025-10-18
 */
abstract class ResourceTest extends PriestSpellSimulationTest {
	void addEffect(String effectName) {
		player.addHiddenEffect(effectName, 10, Duration.seconds(30));
	}

	EffectInstance getEffect(String effectName) {
		return player.getEffect(effectName).orElseThrow();
	}

	private record HealthManaStatus(
			int currentHealth,
			int maxHealth,
			int currentMana,
			int maxMana,
			int regeneratedMana
	) {}

	private final TestSnapshots<HealthManaStatus> healtManaStatusSnapshots = new TestSnapshots<>();

	int currentHealthAt(double time) {
		return healtManaStatusSnapshots.get(time).currentHealth;
	}

	int maxHealthAt(double time) {
		return healtManaStatusSnapshots.get(time).maxHealth;
	}

	int currentManaAt(double time) {
		return healtManaStatusSnapshots.get(time).currentMana;
	}

	int maxManaAt(double time) {
		return healtManaStatusSnapshots.get(time).maxMana;
	}

	int regeneratedManaAt(double time) {
		return healtManaStatusSnapshots.get(time).regeneratedMana;
	}

	@Override
	protected void makeSnapshotsUntil(double timeUntil) {
		super.makeSnapshotsUntil(timeUntil);

		for (var time = 0; time <= timeUntil; ++time) {
			snapshotAt(time);
		}
	}

	void snapshotAt(int... times) {
		for (int time : times) {
			healtManaStatusSnapshots.makeSnapshotsAt(
					() -> new HealthManaStatus(
							player.getCurrentHealth(),
							player.getMaxHealth(),
							player.getCurrentMana(),
							player.getMaxMana(),
							regeneratedMana
					),
					time
			);
		}
	}
}
