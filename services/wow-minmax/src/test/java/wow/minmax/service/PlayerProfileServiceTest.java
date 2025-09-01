package wow.minmax.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import wow.minmax.repository.PlayerProfileRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

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
}