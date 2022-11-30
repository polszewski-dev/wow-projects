package wow.commons.repository;

import org.assertj.core.data.Offset;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import wow.commons.WowCommonsTestConfig;
import wow.commons.model.character.BaseStatInfo;
import wow.commons.model.character.CombatRatingInfo;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.character.CharacterClass.WARLOCK;
import static wow.commons.model.character.Race.ORC;

/**
 * User: POlszewski
 * Date: 2022-11-30
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = WowCommonsTestConfig.class)
class CharacterRepositoryTest {
	@Autowired
	CharacterRepository underTest;

	@Test
	@DisplayName("BaseStatInfo is read correctly")
	void baseStatInfoIsCorrect() {
		Optional<BaseStatInfo> optionalBaseStatInfo = underTest.getBaseStats(WARLOCK, ORC, 70);

		assertThat(optionalBaseStatInfo).isPresent();

		BaseStatInfo baseStatInfo = optionalBaseStatInfo.orElseThrow();

		assertThat(baseStatInfo.getLevel()).isEqualTo(70);
		assertThat(baseStatInfo.getCharacterClass()).isEqualTo(WARLOCK);
		assertThat(baseStatInfo.getRace()).isEqualTo(ORC);
		assertThat(baseStatInfo.getBaseStrength()).isEqualTo(48);
		assertThat(baseStatInfo.getBaseAgility()).isEqualTo(47);
		assertThat(baseStatInfo.getBaseStamina()).isEqualTo(78);
		assertThat(baseStatInfo.getBaseIntellect()).isEqualTo(130);
		assertThat(baseStatInfo.getBaseSpirit()).isEqualTo(114);
		assertThat(baseStatInfo.getBaseHP()).isEqualTo(1964);
		assertThat(baseStatInfo.getBaseMana()).isEqualTo(2698);
		assertThat(baseStatInfo.getBaseSpellCritPct().getValue()).isEqualTo(3.29, PRECISION);
		assertThat(baseStatInfo.getIntellectPerCritPct()).isEqualTo(80, PRECISION);
	}

	@Test
	@DisplayName("CombatRatingInfo is read correctly")
	void combatRatingInfoIsCorrect() {
		Optional<CombatRatingInfo> optionalCombatRatingInfo = underTest.getCombatRatings(70);

		assertThat(optionalCombatRatingInfo).isPresent();

		CombatRatingInfo combatRatingInfo = optionalCombatRatingInfo.orElseThrow();

		assertThat(combatRatingInfo.getLevel()).isEqualTo(70);
		assertThat(combatRatingInfo.getSpellCrit()).isEqualTo(22.22, PRECISION);
		assertThat(combatRatingInfo.getSpellHit()).isEqualTo(12.62, PRECISION);
		assertThat(combatRatingInfo.getSpellHaste()).isEqualTo(15.76, PRECISION);
	}

	static final Offset<Double> PRECISION = Offset.offset(0.01);
}
