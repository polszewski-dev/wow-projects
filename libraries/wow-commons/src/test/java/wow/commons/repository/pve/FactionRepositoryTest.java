package wow.commons.repository.pve;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import wow.commons.WowCommonsSpringTest;
import wow.commons.model.config.TimeRestriction;
import wow.commons.model.pve.Faction;
import wow.commons.model.pve.FactionExclusionGroupId;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.pve.GameVersionId.TBC;
import static wow.commons.model.pve.PhaseId.TBC_P5;
import static wow.commons.model.pve.Side.HORDE;
import static wow.test.commons.ExclusiveFactionNames.ALDOR;
import static wow.test.commons.ExclusiveFactionNames.SCRYERS;

/**
 * User: POlszewski
 * Date: 2022-11-10
 */
class FactionRepositoryTest extends WowCommonsSpringTest {
	@Autowired
	FactionRepository factionRepository;

	@Test
	void factionIsCorrect() {
		var faction = factionRepository.getFaction("Thrallmar", TBC_P5).orElseThrow();

		assertThat(faction.getId()).isEqualTo(947);
		assertThat(faction.getName()).isEqualTo("Thrallmar");
		assertThat(faction.getSide()).isEqualTo(HORDE);
		assertThat(faction.getVersion()).isEqualTo(TBC);
		assertThat(faction.getTimeRestriction()).isEqualTo(TimeRestriction.of(TBC));
	}

	@ParameterizedTest
	@CsvSource({
			"The Aldor,   SCRYERS_ALDOR",
			"The Scryers, SCRYERS_ALDOR"
	})
	void exclusionGroupIsCorrect(String name, FactionExclusionGroupId exclusionGroupId) {
		var faction = factionRepository.getFaction(name, TBC_P5).orElseThrow();

		assertThat(faction.getExclusionGroupId()).isEqualTo(exclusionGroupId);
	}

	@Test
	void getAvailableExclusiveFactions() {
		var result = factionRepository.getAvailableExclusiveFactions(TBC);
		var names = result.stream()
				.map(Faction::getName)
				.toList();

		assertThat(names).hasSameElementsAs(List.of(
				ALDOR,
				SCRYERS
		));
	}
}