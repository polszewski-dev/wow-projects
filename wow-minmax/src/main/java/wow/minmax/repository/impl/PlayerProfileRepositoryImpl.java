package wow.minmax.repository.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.equipment.Equipment;
import wow.commons.model.equipment.EquippableItem;
import wow.commons.model.item.Enchant;
import wow.commons.model.item.Gem;
import wow.commons.model.unit.CharacterClass;
import wow.commons.model.unit.CreatureType;
import wow.commons.model.unit.Race;
import wow.commons.repository.ItemDataRepository;
import wow.minmax.converter.persistent.PlayerPOProfileConverter;
import wow.minmax.model.BuildIds;
import wow.minmax.model.PlayerProfile;
import wow.minmax.repository.BuildRepository;
import wow.minmax.repository.PlayerProfileRepository;

import javax.annotation.PostConstruct;
import java.io.*;
import java.time.LocalDateTime;
import java.util.*;

import static wow.minmax.model.Build.BuffSet.*;

/**
 * User: POlszewski
 * Date: 2021-12-14
 */
@Repository
@AllArgsConstructor
public class PlayerProfileRepositoryImpl implements PlayerProfileRepository {
	private final ItemDataRepository itemDataRepository;
	private final BuildRepository buildRepository;
	private final PlayerPOProfileConverter playerPOProfileConverter;

	@Override
	public List<PlayerProfile> getPlayerProfileList() {
		return new ArrayList<>(profiles.values());
	}

	@Override
	public PlayerProfile createPlayerProfile(String profileName, int phase) {
		PlayerProfile playerProfile = createTemporaryPlayerProfile(profileName, phase);
		saveProfile(playerProfile);
		return playerProfile;
	}

	@Override
	public PlayerProfile createTemporaryPlayerProfile(String profileName, int phase) {
		PlayerProfile playerProfile = new PlayerProfile(
				UUID.randomUUID(),
				profileName,
				CharacterClass.Warlock,
				Race.Orc,
				70,
				CreatureType.Undead,
				phase,
				buildRepository.getBuild(BuildIds.DESTRO_SHADOW_BUILD)
		);

		playerProfile.setBuffs(playerProfile.getBuild().getBuffs(SelfBuff, PartyBuff, RaidBuff, Consumes));
		playerProfile.setEquipment(new Equipment());
		return playerProfile;
	}

	@Override
	public PlayerProfile copyPlayerProfile(UUID copiedProfileId, String profileName, int phase) {
		PlayerProfile copiedProfile = getPlayerProfile(copiedProfileId);
		PlayerProfile copy = copiedProfile.copy(UUID.randomUUID(), profileName, phase);

		saveProfile(copy);
		return copy;
	}

	@Override
	public PlayerProfile getPlayerProfile(UUID profileId) {
		return profiles.get(profileId);
	}

	@Override
	public PlayerProfile changeItem(UUID profileId, ItemSlot slot, int itemId) {
		PlayerProfile playerProfile = getPlayerProfile(profileId);
		EquippableItem equippableItem = new EquippableItem(itemDataRepository.getItem(itemId));

		playerProfile.getEquipment().set(equippableItem, slot);
		saveProfile(playerProfile);
		return playerProfile;
	}

	@Override
	public PlayerProfile changeEnchant(UUID profileId, ItemSlot slot, int enchantId) {
		PlayerProfile playerProfile = getPlayerProfile(profileId);
		Enchant enchant = itemDataRepository.getEnchant(enchantId);

		playerProfile.getEquipment().get(slot).enchant(enchant);
		saveProfile(playerProfile);
		return playerProfile;
	}

	@Override
	public PlayerProfile changeGem(UUID profileId, ItemSlot slot, int socketNo, int gemId) {
		PlayerProfile playerProfile = getPlayerProfile(profileId);
		Gem gem = itemDataRepository.getGem(gemId);

		playerProfile.getEquipment().get(slot).insertGem(socketNo, gem);
		saveProfile(playerProfile);
		return playerProfile;
	}

	@Override
	public void saveProfile(PlayerProfile playerProfile) {
		playerProfile.setLastModified(LocalDateTime.now());
		profiles.put(playerProfile.getProfileId(), playerProfile);
		write(KEY, new TreeMap<>(playerPOProfileConverter.convertMap(profiles)));
	}

	@Override
	public PlayerProfile resetEquipment(UUID profileId) {
		PlayerProfile playerProfile = getPlayerProfile(profileId);
		playerProfile.setEquipment(new Equipment());
		saveProfile(playerProfile);
		return playerProfile;
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
