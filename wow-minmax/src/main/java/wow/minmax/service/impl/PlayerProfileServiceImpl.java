package wow.minmax.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import wow.commons.model.buffs.Buff;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.equipment.Equipment;
import wow.commons.model.equipment.EquippableItem;
import wow.commons.model.item.Enchant;
import wow.commons.model.item.Gem;
import wow.commons.model.item.Item;
import wow.commons.model.pve.Phase;
import wow.commons.model.unit.CharacterClass;
import wow.commons.model.unit.CharacterInfo;
import wow.commons.model.unit.CreatureType;
import wow.commons.model.unit.Race;
import wow.commons.repository.ItemDataRepository;
import wow.commons.repository.SpellDataRepository;
import wow.minmax.model.BuildIds;
import wow.minmax.model.PlayerProfile;
import wow.minmax.repository.BuildRepository;
import wow.minmax.repository.PlayerProfileRepository;
import wow.minmax.service.PlayerProfileService;
import wow.minmax.service.UpgradeService;

import java.util.List;
import java.util.UUID;

import static wow.minmax.model.Build.BuffSet.*;

/**
 * User: POlszewski
 * Date: 2021-12-14
 */
@Service
@AllArgsConstructor
public class PlayerProfileServiceImpl implements PlayerProfileService {
	private final PlayerProfileRepository playerProfileRepository;
	private final ItemDataRepository itemDataRepository;
	private final SpellDataRepository spellDataRepository;
	private final BuildRepository buildRepository;

	private final UpgradeService upgradeService;

	@Override
	public List<PlayerProfile> getPlayerProfileList() {
		return playerProfileRepository.getPlayerProfileList();
	}

	@Override
	public PlayerProfile createPlayerProfile(String profileName, Phase phase) {
		CharacterInfo characterInfo = new CharacterInfo(
				CharacterClass.WARLOCK,
				Race.ORC,
				phase.getGameVersion().getMaxLevel(),
				List.of()
		);

		PlayerProfile playerProfile = new PlayerProfile(
				UUID.randomUUID(),
				profileName,
				characterInfo,
				CreatureType.UNDEAD,
				phase,
				buildRepository.getBuild(BuildIds.DESTRO_SHADOW_BUILD).orElseThrow()
		);

		playerProfile.setBuffs(playerProfile.getBuild().getBuffs(SELF_BUFF, PARTY_BUFF, RAID_BUFF, CONSUMES));
		playerProfile.setEquipment(new Equipment());
		playerProfileRepository.saveProfile(playerProfile);
		return playerProfile;
	}

	@Override
	public PlayerProfile copyPlayerProfile(UUID copiedProfileId, String profileName, Phase phase) {
		PlayerProfile copiedProfile = playerProfileRepository.getPlayerProfile(copiedProfileId).orElseThrow();
		PlayerProfile copy = copiedProfile.copy(UUID.randomUUID(), profileName, phase);

		playerProfileRepository.saveProfile(copy);
		return copy;
	}

	@Override
	public PlayerProfile getPlayerProfile(UUID profileId) {
		return playerProfileRepository.getPlayerProfile(profileId).orElseThrow();
	}

	@Override
	public PlayerProfile changeItem(UUID profileId, ItemSlot slot, int itemId) {
		PlayerProfile playerProfile = playerProfileRepository.getPlayerProfile(profileId).orElseThrow();
		Item item = itemDataRepository.getItem(itemId).orElseThrow();
		EquippableItem bestItemVariant = upgradeService.getBestItemVariant(playerProfile.copy(), item, slot, playerProfile.getDamagingSpellId());

		playerProfile.getEquipment().set(bestItemVariant, slot);
		playerProfileRepository.saveProfile(playerProfile);

		return playerProfile;
	}

	@Override
	public PlayerProfile changeEnchant(UUID profileId, ItemSlot slot, int enchantId) {
		PlayerProfile playerProfile = playerProfileRepository.getPlayerProfile(profileId).orElseThrow();
		Enchant enchant = itemDataRepository.getEnchant(enchantId).orElseThrow();

		playerProfile.getEquipment().get(slot).enchant(enchant);
		playerProfileRepository.saveProfile(playerProfile);
		return playerProfile;
	}

	@Override
	public PlayerProfile changeGem(UUID profileId, ItemSlot slot, int socketNo, int gemId) {
		PlayerProfile playerProfile = playerProfileRepository.getPlayerProfile(profileId).orElseThrow();
		Gem gem = itemDataRepository.getGem(gemId).orElseThrow();

		playerProfile.getEquipment().get(slot).insertGem(socketNo, gem);
		playerProfileRepository.saveProfile(playerProfile);
		return playerProfile;
	}

	@Override
	public PlayerProfile resetEquipment(UUID profileId) {
		PlayerProfile playerProfile = playerProfileRepository.getPlayerProfile(profileId).orElseThrow();

		playerProfile.setEquipment(new Equipment());
		playerProfileRepository.saveProfile(playerProfile);
		return playerProfile;
	}

	@Override
	public PlayerProfile enableBuff(UUID profileId, int buffId, boolean enabled) {
		PlayerProfile playerProfile = playerProfileRepository.getPlayerProfile(profileId).orElseThrow();
		Buff buff = spellDataRepository.getBuff(buffId).orElseThrow();

		playerProfile.enableBuff(buff, enabled);
		playerProfileRepository.saveProfile(playerProfile);
		return playerProfile;
	}
}
