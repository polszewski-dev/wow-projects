package wow.commons.repository.impl.character;

import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import wow.commons.model.character.Race;
import wow.commons.model.character.RaceId;
import wow.commons.model.pve.GameVersionId;
import wow.commons.repository.character.RaceRepository;
import wow.commons.repository.impl.parser.character.RaceExcelParser;
import wow.commons.repository.pve.GameVersionRepository;

import java.io.IOException;
import java.util.Optional;

/**
 * User: POlszewski
 * Date: 27.09.2024
 */
@Component
@DependsOn("characterClassRepositoryImpl")
public class RaceRepositoryImpl implements RaceRepository {
	private final GameVersionRepository gameVersionRepository;

	public RaceRepositoryImpl(GameVersionRepository gameVersionRepository, RaceExcelParser parser) throws IOException {
		this.gameVersionRepository = gameVersionRepository;
		parser.readFromXls();
	}

	@Override
	public Optional<Race> getRace(RaceId raceId, GameVersionId gameVersionId) {
		var gameVersion = gameVersionRepository.getGameVersion(gameVersionId).orElseThrow();

		return gameVersion.getRace(raceId);
	}
}
