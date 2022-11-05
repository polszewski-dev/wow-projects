package wow.minmax.repository.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import wow.minmax.converter.persistent.PlayerPOProfileConverter;
import wow.minmax.model.PlayerProfile;
import wow.minmax.repository.PlayerProfileRepository;

import javax.annotation.PostConstruct;
import java.io.*;
import java.time.LocalDateTime;
import java.util.*;

/**
 * User: POlszewski
 * Date: 2021-12-14
 */
@Repository
@AllArgsConstructor
public class PlayerProfileRepositoryImpl implements PlayerProfileRepository {
	private final PlayerPOProfileConverter playerPOProfileConverter;

	@Override
	public List<PlayerProfile> getPlayerProfileList() {
		return new ArrayList<>(profiles.values());
	}

	@Override
	public Optional<PlayerProfile> getPlayerProfile(UUID profileId) {
		return Optional.ofNullable(profiles.get(profileId));
	}

	@Override
	public void saveProfile(PlayerProfile playerProfile) {
		playerProfile.setLastModified(LocalDateTime.now());
		profiles.put(playerProfile.getProfileId(), playerProfile);
		write(KEY, new TreeMap<>(playerPOProfileConverter.convertMap(profiles)));
	}

	@PostConstruct
	public void init() {
		if (has(KEY)) {
			profiles = playerPOProfileConverter.convertBackMap(read(KEY));
		} else {
			profiles = new TreeMap<>();
		}
		profiles = Collections.synchronizedMap(profiles);
	}

	// TODO store in a real database

	private static final String KEY = "profiles";
	private Map<UUID, PlayerProfile> profiles;

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
