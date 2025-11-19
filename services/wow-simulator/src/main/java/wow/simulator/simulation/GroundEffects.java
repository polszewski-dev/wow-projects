package wow.simulator.simulation;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import wow.simulator.model.effect.Effects;

/**
 * User: POlszewski
 * Date: 2025-11-19
 */
@RequiredArgsConstructor
@Getter
public class GroundEffects extends Effects {
	private final SimulationContext simulationContext;
}
