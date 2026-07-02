package wow.minmax.repository.impl.file;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import wow.minmax.config.FileRepoConfig;
import wow.minmax.model.PlayerProfile;
import wow.minmax.repository.PlayerProfileRepository;

import java.util.List;
import java.util.Optional;

/**
 * User: POlszewski
 * Date: 02.07.2026
 */
@Component
@Profile("dev")
public class FilePlayerProfileRepository extends AbstractFileRepository<PlayerProfile> implements PlayerProfileRepository {
	public FilePlayerProfileRepository(FileRepoConfig config) {
		super(config.getDirectory() + "/player-profile", PlayerProfile.class);
	}

	@Override
	public synchronized Optional<PlayerProfile> findById(String id) {
		return readFromJsonFile(id);
	}

	@Override
	public synchronized List<PlayerProfile> findAll() {
		return readAllFromJsonFiles();
	}

	@Override
	public synchronized PlayerProfile save(PlayerProfile playerProfile) {
		return saveToJsonFile(playerProfile.getProfileId(), playerProfile);
	}
}
