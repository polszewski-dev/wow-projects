package wow.character.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import wow.character.WowCharacterSpringTest;
import wow.character.model.character.CombatRatingInfo;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.pve.GameVersionId.TBC;
import static wow.test.commons.TestConstants.PRECISION;

/**
 * User: POlszewski
 * Date: 27.09.2024
 */
class CombatRatingInfoRepositoryTest extends WowCharacterSpringTest {
	@Autowired
	CombatRatingInfoRepository underTest;

	@Test
	void getCombatRatingInfo() {
		CombatRatingInfo combatRatingInfo = underTest.getCombatRatingInfo(TBC, 70).orElseThrow();

		assertThat(combatRatingInfo.getLevel()).isEqualTo(70);
		assertThat(combatRatingInfo.getSpellCrit()).isEqualTo(22.07, PRECISION);
		assertThat(combatRatingInfo.getSpellHit()).isEqualTo(12.62, PRECISION);
		assertThat(combatRatingInfo.getSpellHaste()).isEqualTo(15.76, PRECISION);
	}
}