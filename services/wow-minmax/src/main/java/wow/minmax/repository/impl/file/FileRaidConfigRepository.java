package wow.minmax.repository.impl.file;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import wow.minmax.config.FileRepoConfig;
import wow.minmax.model.db.RaidConfig;
import wow.minmax.repository.RaidConfigRepository;

import java.util.Optional;

/**
 * User: POlszewski
 * Date: 02.07.2026
 */
@Component
@Profile("dev")
public class FileRaidConfigRepository extends AbstractFileRepository<RaidConfig> implements RaidConfigRepository {
	public FileRaidConfigRepository(FileRepoConfig config) {
		super(config.getDirectory() + "/raid-config", RaidConfig.class);
	}

	@Override
	public synchronized Optional<RaidConfig> findById(String id) {
		return readFromJsonFile(id);
	}

	@Override
	public synchronized RaidConfig save(RaidConfig raidConfig) {
		return saveToJsonFile(raidConfig.getPlayerId(), raidConfig);
	}
}
