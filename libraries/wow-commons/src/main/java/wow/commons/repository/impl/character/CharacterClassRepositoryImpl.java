package wow.commons.repository.impl.character;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import wow.commons.model.character.CharacterClass;
import wow.commons.model.character.CharacterClassId;
import wow.commons.model.pve.GameVersionId;
import wow.commons.repository.character.CharacterClassRepository;
import wow.commons.repository.impl.parser.character.CharacterClassExcelParser;
import wow.commons.repository.pve.GameVersionRepository;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Optional;

/**
 * User: POlszewski
 * Date: 27.09.2024
 */
@Repository
@RequiredArgsConstructor
public class CharacterClassRepositoryImpl implements CharacterClassRepository {
	private final GameVersionRepository gameVersionRepository;

	@Value("${character.classes.xls.file.path}")
	private String xlsFilePath;

	@Override
	public Optional<CharacterClass> getCharacterClass(CharacterClassId characterClassId, GameVersionId gameVersionId) {
		var gameVersion = gameVersionRepository.getGameVersion(gameVersionId).orElseThrow();

		return gameVersion.getCharacterClass(characterClassId);
	}

	@PostConstruct
	public void init() throws IOException {
		var parser = new CharacterClassExcelParser(xlsFilePath, gameVersionRepository);
		parser.readFromXls();
	}
}
