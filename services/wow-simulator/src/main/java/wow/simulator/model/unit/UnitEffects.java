package wow.simulator.model.unit;

import lombok.RequiredArgsConstructor;
import wow.simulator.model.effect.Effects;
import wow.simulator.simulation.SimulationContext;

/**
 * User: POlszewski
 * Date: 2025-11-19
 */
@RequiredArgsConstructor
public class UnitEffects extends Effects {
	private final Unit owner;

	@Override
	public SimulationContext getSimulationContext() {
		return owner.getSimulationContext();
	}
}
