package wow.commons.repository.pve;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import wow.commons.WowCommonsSpringTest;
import wow.commons.model.config.TimeRestriction;
import wow.commons.model.pve.Npc;
import wow.commons.model.pve.PhaseId;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.pve.GameVersionId.TBC;

/**
 * User: POlszewski
 * Date: 2022-11-10
 */
class NpcRepositoryTest extends WowCommonsSpringTest {
	@Autowired
	NpcRepository npcRepository;

	@Test
	void npcIsCorrect() {
		Optional<Npc> optionalNpc = npcRepository.getNpc(25840, PhaseId.TBC_P5);

		assertThat(optionalNpc).isPresent();

		Npc npc = optionalNpc.orElseThrow();

		assertThat(npc.getId()).isEqualTo(25840);
		assertThat(npc.getName()).isEqualTo("Entropius");
		assertThat(npc.getZones()).hasSize(1);

		assertThat(npc.getZones().getFirst().getId()).isEqualTo(4075);
		assertThat(npc.getZones().getFirst().getName()).isEqualTo("Sunwell Plateau");

		assertThat(npc.getTimeRestriction()).isEqualTo(TimeRestriction.of(TBC));
	}
}