package wow.commons.repository.character;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import wow.commons.WowCommonsSpringTest;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.character.RaceId.ORC;
import static wow.commons.model.pve.GameVersionId.VANILLA;
import static wow.commons.model.pve.Side.HORDE;

/**
 * User: POlszewski
 * Date: 28.09.2024
 */
class RaceRepositoryTest extends WowCommonsSpringTest {
	@Autowired
	RaceRepository underTest;

	@Test
	void racesAreCorrect() {
		var orc = underTest.getRace(ORC, VANILLA).orElseThrow();

		assertThat(orc.getRaceId()).isEqualTo(ORC);
		assertThat(orc.getName()).isEqualTo("Orc");
		assertThat(orc.getIcon()).isEqualTo("race_orc_male");
		assertThat(orc.getSide()).isEqualTo(HORDE);

//		assertThat(orc.getRacials().stream().map(Described::getName).toList()).hasSameElementsAs(List.of(
//				"Axe Specialization",
//				"Blood Fury",
//				"Command",
//				"Hardiness"
//		));
	}
}