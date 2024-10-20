package wow.commons.repository.impl.pve;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import wow.commons.model.pve.GameVersion;
import wow.commons.model.pve.GameVersionId;
import wow.commons.repository.impl.parser.pve.GameVersionExcelParser;
import wow.commons.repository.pve.GameVersionRepository;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

/**
 * User: POlszewski
 * Date: 2022-11-30
 */
@Component
@RequiredArgsConstructor
public class GameVersionRepositoryImpl implements GameVersionRepository {
	private final Map<GameVersionId, GameVersion> gameVersionById = new TreeMap<>();

	@Value("${game.versions.xls.file.path}")
	private String xlsFilePath;

	@Override
	public Optional<GameVersion> getGameVersion(GameVersionId gameVersionId) {
		return Optional.ofNullable(gameVersionById.get(gameVersionId));
	}

	@PostConstruct
	public void init() throws IOException {
		var parser = new GameVersionExcelParser(xlsFilePath, this);
		parser.readFromXls();
	}

	public void addGameVersion(GameVersion gameVersion) {
		gameVersionById.put(gameVersion.getGameVersionId(), gameVersion);
	}
}
