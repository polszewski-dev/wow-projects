package wow.commons.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import wow.commons.model.pve.Boss;
import wow.commons.model.pve.Faction;
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
class PveRepositoryTest extends RepositoryTest {
	@Autowired
	PveRepository underTest;

	@Test
	@DisplayName("Zone is read correctly")
	void zoneIsCorrect() {
		Optional<Zone> optionalZone = underTest.getZone(4075);

		assertThat(optionalZone).isPresent();

		Zone zone = optionalZone.orElseThrow();

		assertThat(zone.getId()).isEqualTo(4075);
		assertThat(zone.getName()).isEqualTo("Sunwell Plateau");
		assertThat(zone.getShortName()).isEqualTo("SWP");
		assertThat(zone.getVersion()).isEqualTo(TBC);
		assertThat(zone.getZoneType()).isEqualTo(RAID);
		assertThat(zone.getPartySize()).isEqualTo(25);

		List<String> bossNames = zone.getBosses().stream()
				.map(Boss::getName)
				.sorted()
				.toList();

		List<String> expectedBossNames = List.of(
				"Entropius",
				"Felmyst",
				"Grand Warlock Alythess",
				"Kalecgos",
				"Kil'jaeden",
				"Lady Sacrolash",
				"M'uru",
				"Sathrovarr the Corruptor"
		);

		assertThat(bossNames).isEqualTo(expectedBossNames);
	}

	@Test
	@DisplayName("Boss is read correctly")
	void bossIsCorrect() {
		Optional<Boss> optionalBoss = underTest.getBoss(25840);

		assertThat(optionalBoss).isPresent();

		Boss boss = optionalBoss.orElseThrow();

		assertThat(boss.getId()).isEqualTo(25840);
		assertThat(boss.getName()).isEqualTo("Entropius");
		assertThat(boss.getZones()).hasSize(1);

		assertThat(boss.getZones().get(0).getId()).isEqualTo(4075);
		assertThat(boss.getZones().get(0).getName()).isEqualTo("Sunwell Plateau");
	}

	@Test
	@DisplayName("Faction is read correctly")
	void factionIsCorrect() {
		Optional<Faction> optionalFaction = underTest.getFaction("Netherwing");

		assertThat(optionalFaction).isPresent();

		Faction faction = optionalFaction.orElseThrow();

		assertThat(faction.getNo()).isEqualTo(18);
		assertThat(faction.getName()).isEqualTo("Netherwing");
		assertThat(faction.getPhaseId()).isEqualTo(PhaseId.TBC_P3);
	}
}