package wow.simulator.model.unit;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import wow.simulator.model.effect.Effects;
import wow.simulator.simulation.SimulationContext;

/**
 * User: POlszewski
 * Date: 2025-11-19
 */
@RequiredArgsConstructor
@Getter
public class AuraEffects extends Effects {
	private final SimulationContext simulationContext;
}
