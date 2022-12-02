package wow.minmax.repository.impl;

import org.springframework.stereotype.Repository;
import wow.minmax.model.persistent.PlayerProfilePO;
import wow.minmax.repository.PlayerProfileRepository;

import java.io.*;
import java.time.LocalDateTime;
import java.util.*;

/**
 * User: POlszewski
 * Date: 2021-12-14
 */
@Repository
public class PlayerProfileRepositoryImpl implements PlayerProfileRepository {
	@Override
	public List<PlayerProfilePO> getPlayerProfileList() {
		return new ArrayList<>(getProfiles().values());
	}

	@Override
	public Optional<PlayerProfilePO> getPlayerProfile(UUID profileId) {
		return Optional.ofNullable(getProfiles().get(profileId));
	}

	@Override
	public void saveProfile(PlayerProfilePO playerProfile) {
		playerProfile.setLastModified(LocalDateTime.now());
		getProfiles().put(playerProfile.getProfileId(), playerProfile);
		write(KEY, new TreeMap<>(getProfiles()));
	}

	private Map<UUID, PlayerProfilePO> getProfiles() {
		if (profiles == null) {
			profiles = Collections.synchronizedMap(has(KEY) ? read(KEY) : new TreeMap<>());
		}
		return profiles;
	}

	// TODO store in a real database

	private static final String KEY = "profiles";
	private Map<UUID, PlayerProfilePO> profiles;

	private <T extends Serializable> void write(String id, T contents) {
		String filePath = getFilePath(id);
		new File(filePath).getParentFile().mkdirs();
		try (var out = new ObjectOutputStream(new FileOutputStream(filePath))) {
			out.writeObject(contents);
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	private <T extends Serializable> T read(String id) {
		try (var in = new ObjectInputStream(new FileInputStream(getFilePath(id)))) {
			return (T)in.readObject();
		} catch (ClassNotFoundException | IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	private boolean has(String id) {
		return new File(getFilePath(id)).exists();
	}

	private static String getFilePath(String id) {
		return "simple_store/" + id;
	}
}
