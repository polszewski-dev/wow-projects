package wow.minmax.repository.impl.file;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import wow.minmax.config.FileRepoConfig;
import wow.minmax.model.db.PlayerConfig;
import wow.minmax.repository.PlayerConfigRepository;

import java.util.List;
import java.util.Optional;

/**
 * User: POlszewski
 * Date: 02.07.2026
 */
@Component
@Profile("dev")
public class FilePlayerConfigRepository extends AbstractFileRepository<PlayerConfig> implements PlayerConfigRepository {
	public FilePlayerConfigRepository(FileRepoConfig config) {
		super(config.getDirectory() + "/player-config", PlayerConfig.class);
	}

	@Override
	public synchronized Optional<PlayerConfig> findById(String id) {
		return readFromJsonFile(id);
	}

	@Override
	public synchronized List<PlayerConfig> findAll() {
		return readAllFromJsonFiles();
	}

	@Override
	public synchronized PlayerConfig save(PlayerConfig playerConfig) {
		return saveToJsonFile(playerConfig.getPlayerId(), playerConfig);
	}
}
