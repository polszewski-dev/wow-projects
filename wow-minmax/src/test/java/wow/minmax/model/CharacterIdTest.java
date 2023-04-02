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
class CharacterIdTest {
	@Test
	void test() {
		CharacterId characterId = new CharacterId(UUID.fromString("f76e6339-332b-4d57-a2c6-bafc70d818d4"), PhaseId.TBC_P5, 70, CreatureType.UNDEAD, 3);

		String string = "f76e6339-332b-4d57-a2c6-bafc70d818d4,tbc_p5,70,undead,3";

		assertThat(characterId).hasToString(string);

		CharacterId parsedCharacterId = CharacterId.parse(string);

		assertThat(parsedCharacterId).isEqualTo(characterId);

		assertThat(parsedCharacterId.getProfileId()).isEqualTo(characterId.getProfileId());
		assertThat(parsedCharacterId.getPhaseId()).isEqualTo(characterId.getPhaseId());
		assertThat(parsedCharacterId.getLevel()).isEqualTo(characterId.getLevel());
		assertThat(parsedCharacterId.getEnemyType()).isEqualTo(characterId.getEnemyType());
		assertThat(parsedCharacterId.getEnemyLevelDiff()).isEqualTo(characterId.getEnemyLevelDiff());
	}
}