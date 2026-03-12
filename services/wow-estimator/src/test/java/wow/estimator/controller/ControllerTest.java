package wow.estimator.controller;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import wow.character.model.character.Raid;
import wow.commons.client.dto.NonPlayerDTO;
import wow.commons.client.dto.RaidDTO;
import wow.estimator.WowEstimatorSpringTest;
import wow.estimator.converter.NonPlayerConverter;
import wow.estimator.converter.RaidConverter;
import wow.estimator.model.NonPlayer;
import wow.estimator.model.Player;

/**
 * User: POlszewski
 * Date: 2025-03-19
 */
abstract class ControllerTest extends WowEstimatorSpringTest {
	@Autowired
	RaidConverter raidConverter;

	@Autowired
	NonPlayerConverter nonPlayerConverter;

	Player player;

	@BeforeEach
	void setup() {
		player = getPlayer();

		equipGearSet(player);
	}

	RaidDTO getRaidDTO(Player player) {
		var raid = Raid.newRaid(player);

		return raidConverter.convert(raid);
	}

	NonPlayerDTO getTargetDTO(Player player) {
		return nonPlayerConverter.convert((NonPlayer) player.getTarget());
	}
}
