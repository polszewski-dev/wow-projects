package wow.commons.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
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
class PveRepositoryTest extends RepositoryTest {
	@Autowired
	PveRepository underTest;

	@Test
	@DisplayName("Zone is read correctly")
	void zoneIsCorrect() {
		Optional<Zone> optionalZone = underTest.getZone(4075, PhaseId.TBC_P5);

		assertThat(optionalZone).isPresent();

		Zone zone = optionalZone.orElseThrow();

		assertThat(zone.getId()).isEqualTo(4075);
		assertThat(zone.getName()).isEqualTo("Sunwell Plateau");
		assertThat(zone.getShortName()).isEqualTo("SWP");
		assertThat(zone.getVersion()).isEqualTo(TBC);
		assertThat(zone.getZoneType()).isEqualTo(RAID);
		assertThat(zone.getPartySize()).isEqualTo(25);
		assertThat(zone.getTimeRestriction().getUniqueVersion()).isEqualTo(TBC);

		List<String> bossNames = zone.getBosses().stream()
				.map(Boss::getName)
				.sorted()
				.toList();

		List<String> expectedBossNames = List.of(
				"Kalecgos",
				"Sathrovarr the Corruptor",
				"Brutallus",
				"Felmyst",
				"Grand Warlock Alythess",
				"Lady Sacrolash",
				"M'uru",
				"Entropius",
				"Kil'jaeden"
		);

		assertThat(bossNames).hasSameElementsAs(expectedBossNames);
	}

	@Test
	@DisplayName("Boss is read correctly")
	void bossIsCorrect() {
		Optional<Boss> optionalBoss = underTest.getBoss(25840, PhaseId.TBC_P5);

		assertThat(optionalBoss).isPresent();

		Boss boss = optionalBoss.orElseThrow();

		assertThat(boss.getId()).isEqualTo(25840);
		assertThat(boss.getName()).isEqualTo("Entropius");
		assertThat(boss.getZones()).hasSize(1);

		assertThat(boss.getZones().get(0).getId()).isEqualTo(4075);
		assertThat(boss.getZones().get(0).getName()).isEqualTo("Sunwell Plateau");

		assertThat(boss.getTimeRestriction().getUniqueVersion()).isEqualTo(TBC);
	}

	@Test
	@DisplayName("Faction is read correctly")
	void factionIsCorrect() {
		Optional<Faction> optionalFaction = underTest.getFaction("Thrallmar", PhaseId.TBC_P5);

		assertThat(optionalFaction).isPresent();

		Faction faction = optionalFaction.orElseThrow();

		assertThat(faction.getId()).isEqualTo(947);
		assertThat(faction.getName()).isEqualTo("Thrallmar");
		assertThat(faction.getSide()).isEqualTo(HORDE);
		assertThat(faction.getVersion()).isEqualTo(TBC);
		assertThat(faction.getTimeRestriction().getUniqueVersion()).isEqualTo(TBC);
	}

	@Test
	@DisplayName("Bosses and instances have matching required versions")
	void matchingBossAndInstanceVersions() {
		for (GameVersionId gameVersionId : GameVersionId.values()) {
			for (Zone instance : underTest.getAllInstances(gameVersionId.getLastPhase())) {
				for (Boss boss : instance.getBosses()) {
					assertThat(boss.getTimeRestriction().getUniqueVersion()).isEqualTo(instance.getTimeRestriction().getUniqueVersion());
				}
			}
		}
	}
}