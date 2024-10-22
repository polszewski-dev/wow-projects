package wow.minmax.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import wow.commons.model.buff.Buff;
import wow.commons.model.buff.BuffCategory;
import wow.minmax.converter.persistent.PlayerProfilePOConverter;
import wow.minmax.model.PlayerProfile;
import wow.minmax.model.PlayerProfileInfo;
import wow.minmax.repository.PlayerProfileRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * User: POlszewski
 * Date: 2022-11-19
 */
class PlayerProfileServiceTest extends ServiceTest {
	@Autowired
	PlayerProfileService underTest;

	@Autowired
	PlayerProfileRepository playerProfileRepository;

	@Autowired
	PlayerProfilePOConverter playerProfilePOConverter;

	@Test
	void getPlayerProfileList() {
		List<PlayerProfileInfo> profiles = underTest.getPlayerProfileInfos();

		assertThat(profiles).hasSize(1);
		assertThat(profiles.get(0).getProfileId()).isEqualTo(profile.getProfileId());

		verify(playerProfileRepository).findAll();
	}

	@Test
	void createPlayerProfile() {
		PlayerProfile newProfile = underTest.createPlayerProfile(profile.getProfileInfo());

		assertThat(newProfile.getProfileName()).isEqualTo(profile.getProfileName());
		assertThat(newProfile.getCharacterClassId()).isEqualTo(profile.getCharacterClassId());
		assertThat(newProfile.getRaceId()).isEqualTo(profile.getRaceId());
		assertThat(newProfile.getProfileId()).isNotNull();
		assertThat(newProfile.getLastModified()).isNotNull();

		verify(playerProfileRepository).save(any());
	}

	@Test
	void getPlayerProfileById() {
		PlayerProfile returnedProfile = underTest.getPlayerProfile(profile.getProfileId());

		assertThat(returnedProfile.getProfileId()).isEqualTo(profile.getProfileId());
	}

	@BeforeEach
	@Override
	void setup() {
		super.setup();

		character.setEquipment(getEquipment());

		var consumes = character.getBuffs().getList().stream()
				.filter(x -> x.getCategories().contains(BuffCategory.CONSUME))
				.map(Buff::getId)
				.toList();
		character.setBuffs(consumes);

		var profilePO = playerProfilePOConverter.convert(profile);

		when(playerProfileRepository.findAll()).thenReturn(List.of(profilePO));
		when(playerProfileRepository.findById(profile.getProfileId().toString())).thenReturn(Optional.of(profilePO));
	}
}