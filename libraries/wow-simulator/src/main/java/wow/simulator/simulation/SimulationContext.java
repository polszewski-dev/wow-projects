package wow.simulator.simulation;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import wow.character.service.CharacterCalculationService;
import wow.simulator.log.GameLog;
import wow.simulator.model.rng.RngFactory;
import wow.simulator.model.time.Clock;

/**
 * User: POlszewski
 * Date: 2023-08-09
 */
@RequiredArgsConstructor
@Getter
public class SimulationContext implements SimulationContextSource {
	private final Clock clock;
	private final GameLog gameLog;
	private final RngFactory rngFactory;
	private final CharacterCalculationService characterCalculationService;
	private Simulation simulation;

	@Override
	public SimulationContext getSimulationContext() {
		return this;
	}

	void setSimulation(Simulation simulation) {
		this.simulation = simulation;
	}
}
