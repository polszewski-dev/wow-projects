package wow.minmax.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import wow.commons.model.character.CharacterClass;
import wow.commons.model.pve.Phase;
import wow.minmax.repository.PlayerProfileRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static wow.commons.model.character.CharacterClassId.PRIEST;
import static wow.commons.model.character.CharacterClassId.WARLOCK;
import static wow.commons.model.character.CreatureType.*;
import static wow.commons.model.pve.PhaseId.*;

/**
 * User: POlszewski
 * Date: 2022-11-19
 */
class PlayerProfileServiceTest extends ServiceTest {
	@Autowired
	PlayerProfileService underTest;

	@Autowired
	PlayerProfileRepository playerProfileRepository;

	@Test
	void getPlayerProfileList() {
		var profiles = underTest.getPlayerProfileInfos();

		assertThat(profiles).hasSize(1);
		assertThat(profiles.getFirst().getProfileId()).isEqualTo(profile.getProfileIdAsUUID());

		verify(playerProfileRepository).findAll();
	}

	@Test
	void createPlayerProfile() {
		var newProfile = underTest.createPlayerProfile(profile.getProfileInfo());

		assertThat(newProfile.getProfileName()).isEqualTo(profile.getProfileName());
		assertThat(newProfile.getCharacterClassId()).isEqualTo(profile.getCharacterClassId());
		assertThat(newProfile.getRaceId()).isEqualTo(profile.getRaceId());
		assertThat(newProfile.getProfileId()).isNotNull();
		assertThat(newProfile.getLastModified()).isNotNull();

		verify(playerProfileRepository).save(any());
	}

	@Test
	void getPlayerProfileById() {
		var returnedProfile = underTest.getPlayerProfile(profile.getProfileIdAsUUID());

		assertThat(returnedProfile.getProfileId()).isEqualTo(profile.getProfileId());
	}

	@Test
	void getNewProfileOptions() {
		var options = underTest.getNewProfileOptions();

		var classOptions = options.classOptions().stream()
				.map(CharacterClass::getCharacterClassId)
				.toList();

		assertThat(classOptions).hasSameElementsAs(List.of(
				WARLOCK,
				PRIEST
		));
	}

	@Test
	void getCharacterSelectionOptions() {
		var options = underTest.getCharacterSelectionOptions(profile.getProfileIdAsUUID());

		var phaseIds = options.phases().stream()
				.map(Phase::getPhaseId)
				.toList();
		var enemyTypes = options.enemyTypes();
		var enemyLevelDiffs = options.enemyLevelDiffs();

		assertThat(phaseIds).hasSameElementsAs(List.of(
				VANILLA_P1,
				VANILLA_P2,
				VANILLA_P2_5,
				VANILLA_P3,
				VANILLA_P4,
				VANILLA_P5,
				VANILLA_P6,
				TBC_P0,
				TBC_P1,
				TBC_P2,
				TBC_P3,
				TBC_P4,
				TBC_P5
		));

		assertThat(enemyTypes).hasSameElementsAs(List.of(
				HUMANOID,
				UNDEAD,
				DEMON,
				BEAST,
				ELEMENTAL,
				DRAGON
		));

		assertThat(enemyLevelDiffs).hasSameElementsAs(List.of(0, 1, 2, 3));
	}
}