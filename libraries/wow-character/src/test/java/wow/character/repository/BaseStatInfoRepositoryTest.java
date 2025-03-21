package wow.character.repository;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import wow.character.WowCharacterSpringTest;
import wow.commons.model.character.CharacterClassId;
import wow.commons.model.character.RaceId;
import wow.commons.model.pve.GameVersionId;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * User: POlszewski
 * Date: 27.09.2024
 */
class BaseStatInfoRepositoryTest extends WowCharacterSpringTest {
	@Autowired
	BaseStatInfoRepository underTest;

	@ParameterizedTest
	@CsvSource({
			"TBC, WARLOCK, ORC,    10, 26, 20, 28,  28,  35,  208,  338, 1.70,  9.39",
			"TBC, WARLOCK, ORC,    20, 29, 24, 34,  40,  48,  364,  703, 1.70, 18.48",
			"TBC, WARLOCK, ORC,    30, 33, 29, 40,  54,  62,  599, 1168, 1.70, 28.18",
			"TBC, WARLOCK, ORC,    40, 37, 34, 48,  69,  78,  934, 1678, 1.70, 38.18",
			"TBC, WARLOCK, ORC,    50, 42, 40, 57,  87,  97, 1369, 2188, 1.70, 49.09",
			"TBC, WARLOCK, ORC,    60, 48, 47, 67, 107, 118, 1904, 2698, 1.70, 60.60",
			"TBC, WARLOCK, ORC,    70, 54, 55, 78, 130, 142, 4090, 4285, 1.70, 80.00",
			"TBC, PRIEST,  UNDEAD, 10, 21, 20, 24,  31,  39,  197,  397, 1.24,  8.77",
			"TBC, PRIEST,  UNDEAD, 20, 23, 23, 28,  44,  53,  347,  757, 1.24, 19.13",
			"TBC, PRIEST,  UNDEAD, 30, 25, 26, 33,  59,  69,  542, 1232, 1.24, 30.03",
			"TBC, PRIEST,  UNDEAD, 40, 28, 29, 38,  76,  86,  837, 1771, 1.24, 41.46",
			"TBC, PRIEST,  UNDEAD, 50, 31, 33, 44,  96, 107, 1232, 2326, 1.24, 53.68",
			"TBC, PRIEST,  UNDEAD, 60, 34, 38, 51, 118, 130, 1727, 2866, 1.24, 66.44",
			"TBC, PRIEST,  UNDEAD, 70, 38, 43, 59, 143, 156, 3801, 4485, 1.24, 80.00"
	})
	void getBaseStatInfo(
			GameVersionId gameVersionId,
			CharacterClassId characterClassId,
			RaceId raceId,
			int level,
			int baseStrength,
			int baseAgility,
			int baseStamina,
			int baseIntellect,
			int baseSpirit,
			int baseHealth,
			int baseMana,
			double baseSpellCrit,
			double intellectPerCritPct
	) {
		var baseStatInfo = underTest.getBaseStatInfo(gameVersionId, characterClassId, raceId, level).orElseThrow();

		assertThat(baseStatInfo.getBaseStrength()).isEqualTo(baseStrength);
		assertThat(baseStatInfo.getBaseAgility()).isEqualTo(baseAgility);
		assertThat(baseStatInfo.getBaseStamina()).isEqualTo(baseStamina);
		assertThat(baseStatInfo.getBaseIntellect()).isEqualTo(baseIntellect);
		assertThat(baseStatInfo.getBaseSpirit()).isEqualTo(baseSpirit);
		assertThat(baseStatInfo.getBaseHealth()).isEqualTo(baseHealth);
		assertThat(baseStatInfo.getBaseMana()).isEqualTo(baseMana);
		assertThat(baseStatInfo.getBaseSpellCritPct().value()).isEqualTo(baseSpellCrit, PRECISION);
		assertThat(baseStatInfo.getIntellectPerCritPct()).isEqualTo(intellectPerCritPct, PRECISION);
	}
}