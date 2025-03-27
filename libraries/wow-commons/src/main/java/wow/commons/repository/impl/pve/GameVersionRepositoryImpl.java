package wow.commons.repository.impl.pve;

import org.springframework.stereotype.Component;
import wow.commons.model.pve.GameVersion;
import wow.commons.model.pve.GameVersionId;
import wow.commons.repository.impl.parser.pve.GameVersionExcelParser;
import wow.commons.repository.pve.GameVersionRepository;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

/**
 * User: POlszewski
 * Date: 2022-11-30
 */
@Component
public class GameVersionRepositoryImpl implements GameVersionRepository {
	private final Map<GameVersionId, GameVersion> gameVersionById = new TreeMap<>();

	public GameVersionRepositoryImpl(GameVersionExcelParser parser) throws IOException {
		parser.readFromXls();
		parser.getGameVersions().forEach(this::addGameVersion);
	}

	@Override
	public Optional<GameVersion> getGameVersion(GameVersionId gameVersionId) {
		return Optional.ofNullable(gameVersionById.get(gameVersionId));
	}

	private void addGameVersion(GameVersion gameVersion) {
		gameVersionById.put(gameVersion.getGameVersionId(), gameVersion);
	}
}
