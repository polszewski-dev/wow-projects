package wow.commons.repository.impl.character;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import wow.commons.model.character.Race;
import wow.commons.model.character.RaceId;
import wow.commons.model.pve.GameVersionId;
import wow.commons.repository.character.RaceRepository;
import wow.commons.repository.impl.parser.character.RaceExcelParser;
import wow.commons.repository.pve.GameVersionRepository;
import wow.commons.repository.spell.SpellRepository;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Optional;

/**
 * User: POlszewski
 * Date: 27.09.2024
 */
@Component
@DependsOn("characterClassRepositoryImpl")
@RequiredArgsConstructor
public class RaceRepositoryImpl implements RaceRepository {
	private final GameVersionRepository gameVersionRepository;
	private final SpellRepository spellRepository;

	@Value("${races.xls.file.path}")
	private String xlsFilePath;

	@Override
	public Optional<Race> getRace(RaceId raceId, GameVersionId gameVersionId) {
		var gameVersion = gameVersionRepository.getGameVersion(gameVersionId).orElseThrow();

		return gameVersion.getRace(raceId);
	}

	@PostConstruct
	public void init() throws IOException {
		var parser = new RaceExcelParser(xlsFilePath, gameVersionRepository, spellRepository);
		parser.readFromXls();
	}
}
