package wow.commons.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wow.commons.WowCommonsSpringTest;
import wow.commons.model.pve.*;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.pve.GameVersionId.TBC;
import static wow.commons.model.pve.Side.HORDE;
import static wow.commons.model.pve.ZoneType.RAID;

/**
 * User: POlszewski
 * Date: 2022-11-10
 */
class PveRepositoryTest extends WowCommonsSpringTest {
	@Test
	@DisplayName("Zone is read correctly")
	void zoneIsCorrect() {
		Optional<Zone> optionalZone = pveRepository.getZone(4075, PhaseId.TBC_P5);

		assertThat(optionalZone).isPresent();

		Zone zone = optionalZone.orElseThrow();

		assertThat(zone.getId()).isEqualTo(4075);
		assertThat(zone.getName()).isEqualTo("Sunwell Plateau");
		assertThat(zone.getShortName()).isEqualTo("SWP");
		assertThat(zone.getVersion()).isEqualTo(TBC);
		assertThat(zone.getZoneType()).isEqualTo(RAID);
		assertThat(zone.getPartySize()).isEqualTo(25);
		assertThat(zone.getTimeRestriction().getUniqueVersion()).isEqualTo(TBC);

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
	@DisplayName("Npc is read correctly")
	void npcIsCorrect() {
		Optional<Npc> optionalNpc = pveRepository.getNpc(25840, PhaseId.TBC_P5);

		assertThat(optionalNpc).isPresent();

		Npc npc = optionalNpc.orElseThrow();

		assertThat(npc.getId()).isEqualTo(25840);
		assertThat(npc.getName()).isEqualTo("Entropius");
		assertThat(npc.getZones()).hasSize(1);

		assertThat(npc.getZones().get(0).getId()).isEqualTo(4075);
		assertThat(npc.getZones().get(0).getName()).isEqualTo("Sunwell Plateau");

		assertThat(npc.getTimeRestriction().getUniqueVersion()).isEqualTo(TBC);
	}

	@Test
	@DisplayName("Faction is read correctly")
	void factionIsCorrect() {
		Optional<Faction> optionalFaction = pveRepository.getFaction("Thrallmar", PhaseId.TBC_P5);

		assertThat(optionalFaction).isPresent();

		Faction faction = optionalFaction.orElseThrow();

		assertThat(faction.getId()).isEqualTo(947);
		assertThat(faction.getName()).isEqualTo("Thrallmar");
		assertThat(faction.getSide()).isEqualTo(HORDE);
		assertThat(faction.getVersion()).isEqualTo(TBC);
		assertThat(faction.getTimeRestriction().getUniqueVersion()).isEqualTo(TBC);
	}

	@Test
	@DisplayName("Npcs and instances have matching required versions")
	void matchingNpcsAndInstanceVersions() {
		for (GameVersionId gameVersionId : GameVersionId.values()) {
			for (Zone instance : pveRepository.getAllInstances(gameVersionId.getLastPhase())) {
				for (Npc npc : instance.getNpcs()) {
					assertThat(npc.getTimeRestriction().getUniqueVersion()).isEqualTo(instance.getTimeRestriction().getUniqueVersion());
				}
			}
		}
	}
}