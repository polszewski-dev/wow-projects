package wow.commons.repository.pve;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import wow.commons.WowCommonsSpringTest;
import wow.commons.model.character.CharacterClass;
import wow.commons.model.character.Race;
import wow.commons.model.profession.Profession;
import wow.commons.model.pve.Phase;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.character.CharacterClassId.*;
import static wow.commons.model.character.RaceId.*;
import static wow.commons.model.profession.ProfessionId.*;
import static wow.commons.model.pve.GameVersionId.*;
import static wow.commons.model.pve.PhaseId.*;

/**
 * User: POlszewski
 * Date: 27.09.2024
 */
class GameVersionRepositoryTest extends WowCommonsSpringTest {
	@Autowired
	GameVersionRepository underTest;

	@Test
	void vanillaGameVersionIsCorrect() {
		var gameVersion = underTest.getGameVersion(VANILLA).orElseThrow();

		assertThat(gameVersion.getGameVersionId()).isEqualTo(VANILLA);
		assertThat(gameVersion.getName()).isEqualTo("Vanilla");
		assertThat(gameVersion.getBasePveSpellHitChancesPct()).isEqualTo(List.of(96.0, 95.0, 94.0, 83.0));
		assertThat(gameVersion.getMaxPveSpellHitChancePct()).isEqualTo(99.0);
	}

	@Test
	void tbcGameVersionIsCorrect() {
		var gameVersion = underTest.getGameVersion(TBC).orElseThrow();

		assertThat(gameVersion.getGameVersionId()).isEqualTo(TBC);
		assertThat(gameVersion.getName()).isEqualTo("TBC");
		assertThat(gameVersion.getBasePveSpellHitChancesPct()).isEqualTo(List.of(96.0, 95.0, 94.0, 83.0));
		assertThat(gameVersion.getMaxPveSpellHitChancePct()).isEqualTo(99.0);
	}

	@Test
	void wotlkGameVersionIsCorrect() {
		var gameVersion = underTest.getGameVersion(WOTLK).orElseThrow();

		assertThat(gameVersion.getGameVersionId()).isEqualTo(WOTLK);
		assertThat(gameVersion.getName()).isEqualTo("WotLK");
		assertThat(gameVersion.getBasePveSpellHitChancesPct()).isEqualTo(List.of(96.0, 95.0, 94.0, 83.0));
		assertThat(gameVersion.getMaxPveSpellHitChancePct()).isEqualTo(100.0);
	}

	@Test
	void gameVersionPhasesAreCorrect() {
		var vanilla = underTest.getGameVersion(VANILLA).orElseThrow();
		var tbc = underTest.getGameVersion(TBC).orElseThrow();

		assertThat(vanilla.getPhases().stream().map(Phase::getPhaseId).toList()).hasSameElementsAs(List.of(
				VANILLA_P1,
				VANILLA_P2,
				VANILLA_P2_5,
				VANILLA_P3,
				VANILLA_P4,
				VANILLA_P5,
				VANILLA_P6
		));

		assertThat(tbc.getPhases().stream().map(Phase::getPhaseId).toList()).hasSameElementsAs(List.of(
				TBC_P0,
				TBC_P1,
				TBC_P2,
				TBC_P3,
				TBC_P4,
				TBC_P5
		));
	}

	@Test
	void gameVersionClassesAreCorrect() {
		var vanilla = underTest.getGameVersion(VANILLA).orElseThrow();
		var tbc = underTest.getGameVersion(TBC).orElseThrow();

		assertThat(vanilla.getCharacterClasses().stream().map(CharacterClass::getCharacterClassId).toList()).hasSameElementsAs(List.of(
				MAGE,
				WARLOCK,
				PRIEST,
				DRUID,
				ROGUE,
				HUNTER,
				SHAMAN,
				PALADIN,
				WARRIOR
		));

		assertThat(tbc.getCharacterClasses().stream().map(CharacterClass::getCharacterClassId).toList()).hasSameElementsAs(List.of(
				MAGE,
				WARLOCK,
				PRIEST,
				DRUID,
				ROGUE,
				HUNTER,
				SHAMAN,
				PALADIN,
				WARRIOR
		));
	}

	@Test
	void gameVersionRacesAreCorrect() {
		var vanilla = underTest.getGameVersion(VANILLA).orElseThrow();
		var tbc = underTest.getGameVersion(TBC).orElseThrow();

		assertThat(vanilla.getRaces().stream().map(Race::getRaceId).toList()).hasSameElementsAs(List.of(
				UNDEAD,
				ORC,
				TROLL,
				TAUREN,
				HUMAN,
				DWARF,
				GNOME,
				NIGHT_ELF
		));

		assertThat(tbc.getRaces().stream().map(Race::getRaceId).toList()).hasSameElementsAs(List.of(
				UNDEAD,
				ORC,
				TROLL,
				TAUREN,
				BLOOD_ELF,
				HUMAN,
				DWARF,
				GNOME,
				NIGHT_ELF,
				DRANEI
		));
	}

	@Test
	void gameVersionProfessionsAreCorrect() {
		var vanilla = underTest.getGameVersion(VANILLA).orElseThrow();
		var tbc = underTest.getGameVersion(TBC).orElseThrow();

		assertThat(vanilla.getProfessions().stream().map(Profession::getProfessionId).toList()).hasSameElementsAs(List.of(
				ENCHANTING,
				ALCHEMY,
				TAILORING,
				LEATHERWORKING,
				BLACKSMITHING,
				ENGINEERING,
				HERBALISM,
				MINING,
				SKINNING,
				COOKING,
				FISHING,
				FIRST_AID
		));

		assertThat(tbc.getProfessions().stream().map(Profession::getProfessionId).toList()).hasSameElementsAs(List.of(
				ENCHANTING,
				JEWELCRAFTING,
				ALCHEMY,
				TAILORING,
				LEATHERWORKING,
				BLACKSMITHING,
				ENGINEERING,
				HERBALISM,
				MINING,
				SKINNING,
				COOKING,
				FISHING,
				FIRST_AID
		));
	}
}