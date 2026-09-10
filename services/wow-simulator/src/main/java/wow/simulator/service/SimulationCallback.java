package wow.simulator.service;

import wow.character.model.character.Raid;
import wow.simulator.model.unit.Player;
import wow.simulator.model.unit.Unit;

/**
 * User: POlszewski
 * Date: 2026-09-10
 */
public interface SimulationCallback {
	void beforePreparationPhaseStarts(Raid<Player> raid, Unit target);

	void afterPreparationPhaseEnds(Raid<Player> raid);
}
