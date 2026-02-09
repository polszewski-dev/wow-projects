package wow.minmax.service;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import wow.minmax.WowMinMaxSpringTest;
import wow.minmax.converter.model.PlayerCharacterConfigConverter;
import wow.minmax.model.Player;
import wow.minmax.model.PlayerCharacterConfig;
import wow.minmax.model.PlayerProfile;
import wow.minmax.repository.PlayerCharacterConfigRepository;
import wow.minmax.repository.PlayerProfileRepository;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * User: POlszewski
 * Date: 2022-11-20
 */
abstract class ServiceTest extends WowMinMaxSpringTest {
	PlayerProfile profile;
	Player character;
	PlayerCharacterConfig savedCharacter;

	@Autowired
	PlayerCharacterConfigRepository playerCharacterConfigRepository;

	@Autowired
	PlayerProfileRepository playerProfileRepository;

	@Autowired
	PlayerCharacterConfigConverter playerCharacterConfigConverter;

	@BeforeEach
	void setup() {
		profile = getPlayerProfile();
		character = getCharacter();

		equipGearSet(character);

		prepareCharacter();

		savedCharacter = playerCharacterConfigConverter.convert(character, CHARACTER_KEY);

		when(playerCharacterConfigRepository.findAll()).thenAnswer(input -> List.of(savedCharacter));
		when(playerCharacterConfigRepository.findById(CHARACTER_KEY.toString())).thenAnswer(input -> Optional.of(savedCharacter));

		when(playerCharacterConfigRepository.save(any())).thenAnswer(input -> {
			var player = input.getArgument(0, PlayerCharacterConfig.class);

			if (!savedCharacter.getCharacterIdAsRecord().equals(CHARACTER_KEY)) {
				throw new IllegalArgumentException("Only one character can be used");
			}

			savedCharacter = player;

			return player;
		});

		when(playerProfileRepository.findAll()).thenReturn(List.of(profile));
		when(playerProfileRepository.findById(profile.getProfileId())).thenReturn(Optional.of(profile));
	}

	void prepareCharacter() {}
}
