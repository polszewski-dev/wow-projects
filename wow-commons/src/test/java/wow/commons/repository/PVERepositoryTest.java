package wow.commons.repository;

import org.assertj.core.data.Offset;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import wow.commons.TestConfig;
import wow.commons.model.pve.Boss;
import wow.commons.model.pve.Faction;
import wow.commons.model.pve.Phase;
import wow.commons.model.pve.Zone;
import wow.commons.model.unit.BaseStatInfo;
import wow.commons.model.unit.CombatRatingInfo;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.pve.GameVersion.TBC;
import static wow.commons.model.pve.ZoneType.RAID;
import static wow.commons.model.unit.CharacterClass.WARLOCK;
import static wow.commons.model.unit.Race.ORC;

/**
 * User: POlszewski
 * Date: 2022-11-10
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestConfig.class)
class PVERepositoryTest {
	@Autowired
	PVERepository underTest;

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
				.collect(Collectors.toList());

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
		Optional<Boss> optionalBoss = underTest.getBoss("Entropius");

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
		assertThat(faction.getPhase()).isEqualTo(Phase.TBC_P3);
	}

	@Test
	@DisplayName("BaseStatInfo is read correctly")
	void baseStatInfoIsCorrect() {
		Optional<BaseStatInfo> baseStats = underTest.getBaseStats(WARLOCK, ORC, 70);

		assertThat(baseStats).isPresent();

		BaseStatInfo baseStatInfo = baseStats.orElseThrow();

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
		Optional<CombatRatingInfo> combatRatings = underTest.getCombatRatings(70);

		assertThat(combatRatings).isPresent();

		CombatRatingInfo combatRatingInfo = combatRatings.orElseThrow();

		assertThat(combatRatingInfo.getLevel()).isEqualTo(70);
		assertThat(combatRatingInfo.getSpellCrit()).isEqualTo(22.22, PRECISION);
		assertThat(combatRatingInfo.getSpellHit()).isEqualTo(12.62, PRECISION);
		assertThat(combatRatingInfo.getSpellHaste()).isEqualTo(15.76, PRECISION);
	}

	static final Offset<Double> PRECISION = Offset.offset(0.01);
}