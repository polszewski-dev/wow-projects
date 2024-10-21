package wow.character.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import wow.character.WowCharacterSpringTest;
import wow.character.model.character.BaseStatInfo;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.character.CharacterClassId.WARLOCK;
import static wow.commons.model.character.RaceId.ORC;
import static wow.commons.model.pve.GameVersionId.TBC;

/**
 * User: POlszewski
 * Date: 27.09.2024
 */
class BaseStatInfoRepositoryTest extends WowCharacterSpringTest {
	@Autowired
	BaseStatInfoRepository underTest;

	@Test
	void getBaseStatInfo() {
		BaseStatInfo baseStatInfo = underTest.getBaseStatInfo(TBC, WARLOCK, ORC, 70).orElseThrow();

		assertThat(baseStatInfo.getLevel()).isEqualTo(70);
		assertThat(baseStatInfo.getCharacterClassId()).isEqualTo(WARLOCK);
		assertThat(baseStatInfo.getRaceId()).isEqualTo(ORC);
		assertThat(baseStatInfo.getBaseStrength()).isEqualTo(54);
		assertThat(baseStatInfo.getBaseAgility()).isEqualTo(55);
		assertThat(baseStatInfo.getBaseStamina()).isEqualTo(78);
		assertThat(baseStatInfo.getBaseIntellect()).isEqualTo(130);
		assertThat(baseStatInfo.getBaseSpirit()).isEqualTo(142);
		assertThat(baseStatInfo.getBaseHealth()).isEqualTo(4090);
		assertThat(baseStatInfo.getBaseMana()).isEqualTo(4285);
		assertThat(baseStatInfo.getBaseSpellCritPct().value()).isEqualTo(3.29, PRECISION);
		assertThat(baseStatInfo.getIntellectPerCritPct()).isEqualTo(80, PRECISION);
	}
}