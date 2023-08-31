package wow.simulator.script;

import wow.simulator.model.unit.Player;

/**
 * User: POlszewski
 * Date: 2023-08-07
 */
public interface AIScript {
	void setupPlayer(Player player);

	void execute(Player player);
}
