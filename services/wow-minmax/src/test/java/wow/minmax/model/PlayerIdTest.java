package wow.minmax.model;

import org.junit.jupiter.api.Test;
import wow.commons.model.character.CreatureType;
import wow.commons.model.pve.PhaseId;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * User: POlszewski
 * Date: 2023-04-02
 */
class PlayerIdTest {
	@Test
	void test() {
		PlayerId playerId = new PlayerId(UUID.fromString("f76e6339-332b-4d57-a2c6-bafc70d818d4"), PhaseId.TBC_P5, 70, CreatureType.UNDEAD, 3);

		String string = "f76e6339-332b-4d57-a2c6-bafc70d818d4,tbc_p5,70,undead,3";

		assertThat(playerId).hasToString(string);

		PlayerId parsedPlayerId = PlayerId.parse(string);

		assertThat(parsedPlayerId).isEqualTo(playerId);

		assertThat(parsedPlayerId.profileId()).isEqualTo(playerId.profileId());
		assertThat(parsedPlayerId.phaseId()).isEqualTo(playerId.phaseId());
		assertThat(parsedPlayerId.level()).isEqualTo(playerId.level());
		assertThat(parsedPlayerId.enemyType()).isEqualTo(playerId.enemyType());
		assertThat(parsedPlayerId.enemyLevelDiff()).isEqualTo(playerId.enemyLevelDiff());
	}
}