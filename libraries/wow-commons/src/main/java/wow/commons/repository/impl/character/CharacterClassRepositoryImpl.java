package wow.commons.repository.impl.character;

import org.springframework.stereotype.Component;
import wow.commons.model.character.CharacterClass;
import wow.commons.model.character.CharacterClassId;
import wow.commons.model.pve.GameVersionId;
import wow.commons.repository.character.CharacterClassRepository;
import wow.commons.repository.impl.parser.character.CharacterClassExcelParser;
import wow.commons.repository.pve.GameVersionRepository;

import java.io.IOException;
import java.util.Optional;

/**
 * User: POlszewski
 * Date: 27.09.2024
 */
@Component
public class CharacterClassRepositoryImpl implements CharacterClassRepository {
	private final GameVersionRepository gameVersionRepository;

	public CharacterClassRepositoryImpl(GameVersionRepository gameVersionRepository, CharacterClassExcelParser parser) throws IOException {
		this.gameVersionRepository = gameVersionRepository;
		parser.readFromXls();
	}

	@Override
	public Optional<CharacterClass> getCharacterClass(CharacterClassId characterClassId, GameVersionId gameVersionId) {
		var gameVersion = gameVersionRepository.getGameVersion(gameVersionId).orElseThrow();

		return gameVersion.getCharacterClass(characterClassId);
	}
}
