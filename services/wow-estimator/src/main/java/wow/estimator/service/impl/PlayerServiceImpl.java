package wow.estimator.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.character.model.character.Raid;
import wow.character.service.CharacterService;
import wow.estimator.model.NonPlayer;
import wow.estimator.model.Player;
import wow.estimator.service.PlayerService;

/**
 * User: POlszewski
 * Date: 2026-03-12
 */
@Component
@AllArgsConstructor
public class PlayerServiceImpl implements PlayerService {
	private final CharacterService characterService;

	@Override
	public Player getPlayer(Raid<Player> raid, NonPlayer target) {
		var player = raid.getFirstMember();

		player.setTarget(target);

		characterService.updateAfterRestrictionChange(player);

		// todo missing target buffs

		return player;
	}
}
