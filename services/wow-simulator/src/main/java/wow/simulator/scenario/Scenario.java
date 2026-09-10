package wow.simulator.scenario;

import wow.character.model.character.Raid;
import wow.simulator.log.handler.GameLogHandler;
import wow.simulator.model.unit.Player;
import wow.simulator.model.unit.Unit;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2026-09-10
 */
public interface Scenario {
	void execute(Raid<Player> raid, Unit target, List<GameLogHandler> handlers);
}
