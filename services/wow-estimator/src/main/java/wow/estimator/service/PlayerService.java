package wow.estimator.service;

import wow.character.model.character.Raid;
import wow.estimator.model.NonPlayer;
import wow.estimator.model.Player;

/**
 * User: POlszewski
 * Date: 2026-03-12
 */
public interface PlayerService {
	Player getPlayer(Raid<Player> raid, NonPlayer target);
}
