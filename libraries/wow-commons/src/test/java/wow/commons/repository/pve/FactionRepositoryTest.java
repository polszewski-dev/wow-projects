package wow.commons.repository.pve;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import wow.commons.WowCommonsSpringTest;
import wow.commons.model.config.TimeRestriction;
import wow.commons.model.pve.Faction;
import wow.commons.model.pve.PhaseId;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.pve.GameVersionId.TBC;
import static wow.commons.model.pve.Side.HORDE;

/**
 * User: POlszewski
 * Date: 2022-11-10
 */
class FactionRepositoryTest extends WowCommonsSpringTest {
	@Autowired
	FactionRepository factionRepository;

	@Test
	void factionIsCorrect() {
		Optional<Faction> optionalFaction = factionRepository.getFaction("Thrallmar", PhaseId.TBC_P5);

		assertThat(optionalFaction).isPresent();

		Faction faction = optionalFaction.orElseThrow();

		assertThat(faction.getId()).isEqualTo(947);
		assertThat(faction.getName()).isEqualTo("Thrallmar");
		assertThat(faction.getSide()).isEqualTo(HORDE);
		assertThat(faction.getVersion()).isEqualTo(TBC);
		assertThat(faction.getTimeRestriction()).isEqualTo(TimeRestriction.of(TBC));
	}
}