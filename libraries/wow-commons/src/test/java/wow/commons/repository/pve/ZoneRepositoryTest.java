package wow.commons.repository.pve;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import wow.commons.WowCommonsSpringTest;
import wow.commons.model.config.TimeRestriction;
import wow.commons.model.pve.GameVersionId;
import wow.commons.model.pve.Npc;
import wow.commons.model.pve.PhaseId;
import wow.commons.model.pve.Zone;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.pve.GameVersionId.TBC;
import static wow.commons.model.pve.ZoneType.RAID;

/**
 * User: POlszewski
 * Date: 2022-11-10
 */
class ZoneRepositoryTest extends WowCommonsSpringTest {
	@Autowired
	ZoneRepository zoneRepository;

	@Test
	void zoneIsCorrect() {
		Optional<Zone> optionalZone = zoneRepository.getZone(4075, PhaseId.TBC_P5);

		assertThat(optionalZone).isPresent();

		Zone zone = optionalZone.orElseThrow();

		assertThat(zone.getId()).isEqualTo(4075);
		assertThat(zone.getName()).isEqualTo("Sunwell Plateau");
		assertThat(zone.getShortName()).isEqualTo("SWP");
		assertThat(zone.getVersion()).isEqualTo(TBC);
		assertThat(zone.getZoneType()).isEqualTo(RAID);
		assertThat(zone.getPartySize()).isEqualTo(25);
		assertThat(zone.getTimeRestriction()).isEqualTo(TimeRestriction.of(TBC));

		List<String> npcNames = zone.getNpcs().stream()
				.map(Npc::getName)
				.sorted()
				.toList();

		List<String> expectedNpcNames = List.of(
				"Kalecgos",
				"Sathrovarr the Corruptor",
				"Brutallus",
				"Felmyst",
				"Grand Warlock Alythess",
				"Lady Sacrolash",
				"Eredar Twins",
				"M'uru",
				"Entropius",
				"Kil'jaeden"
		);

		assertThat(npcNames).hasSameElementsAs(expectedNpcNames);
	}

	@Test
	void matchingNpcsAndInstanceVersions() {
		for (GameVersionId gameVersionId : GameVersionId.values()) {
			for (Zone instance : zoneRepository.getAllInstances(gameVersionId.getLastPhase())) {
				for (Npc npc : instance.getNpcs()) {
					assertThat(npc.getTimeRestriction()).isEqualTo(instance.getTimeRestriction());
				}
			}
		}
	}
}