package wow.simulator.simulation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import wow.character.service.CharacterCalculationService;
import wow.simulator.log.GameLog;
import wow.simulator.model.time.Clock;

/**
 * User: POlszewski
 * Date: 2023-08-09
 */
@AllArgsConstructor
@Getter
public class SimulationContext implements SimulationContextSource {
	private final Clock clock;
	private final GameLog gameLog;
	private final CharacterCalculationService characterCalculationService;

	@Override
	public SimulationContext getSimulationContext() {
		return this;
	}
}
