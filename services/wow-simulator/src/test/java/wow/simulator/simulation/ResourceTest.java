package wow.simulator.simulation;

import wow.commons.model.Duration;
import wow.simulator.model.effect.EffectInstance;
import wow.simulator.model.unit.Unit;
import wow.simulator.simulation.spell.tbc.TbcPriestSpellSimulationTest;

/**
 * User: POlszewski
 * Date: 2025-10-18
 */
abstract class ResourceTest extends TbcPriestSpellSimulationTest {
	void addEffect(String effectName) {
		player.addHiddenEffect(effectName, 10, Duration.seconds(30));
	}

	EffectInstance getEffect(String effectName) {
		return player.getEffect(effectName).orElseThrow();
	}

	private record ResourceStatus(
			int currentHealth,
			int maxHealth,
			int currentMana,
			int maxMana,
			int regeneratedMana
	) {}

	private final TestSnapshots<ResourceStatus> resourceStatusSnapshots = new TestSnapshots<>();

	int currentHealthAt(double time) {
		return resourceStatusSnapshots.get(time).currentHealth;
	}

	int maxHealthAt(double time) {
		return resourceStatusSnapshots.get(time).maxHealth;
	}

	int currentManaAt(double time) {
		return resourceStatusSnapshots.get(time).currentMana;
	}

	int maxManaAt(double time) {
		return resourceStatusSnapshots.get(time).maxMana;
	}

	int regeneratedManaAt(double time) {
		return resourceStatusSnapshots.get(time).regeneratedMana;
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
			resourceStatusSnapshots.makeSnapshotsAt(
					() -> resourceStatus(player),
					time
			);
		}
	}

	private ResourceStatus resourceStatus(Unit unit) {
		return new ResourceStatus(
				unit.getCurrentHealth(),
				unit.getMaxHealth(),
				unit.getCurrentMana(),
				unit.getMaxMana(),
				getRegeneratedMana(unit)
		);
	}
}
