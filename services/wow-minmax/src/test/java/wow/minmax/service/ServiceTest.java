package wow.minmax.service;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import wow.minmax.WowMinMaxSpringTest;
import wow.minmax.converter.db.PlayerConfigConverter;
import wow.minmax.model.Player;
import wow.minmax.model.PlayerProfile;
import wow.minmax.model.db.PlayerConfig;
import wow.minmax.repository.PlayerConfigRepository;
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
	Player player;
	PlayerConfig savedCharacter;

	@Autowired
	PlayerConfigRepository playerConfigRepository;

	@Autowired
	PlayerProfileRepository playerProfileRepository;

	@Autowired
	PlayerConfigConverter playerConfigConverter;

	@BeforeEach
	void setup() {
		profile = getPlayerProfile();
		player = getPlayer();

		equipGearSet(player);

		prepareCharacter();

		savedCharacter = playerConfigConverter.convert(player, CHARACTER_KEY);

		when(playerConfigRepository.findAll()).thenAnswer(input -> List.of(savedCharacter));
		when(playerConfigRepository.findById(CHARACTER_KEY.toString())).thenAnswer(input -> Optional.of(savedCharacter));

		when(playerConfigRepository.save(any())).thenAnswer(input -> {
			var player = input.getArgument(0, PlayerConfig.class);

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
