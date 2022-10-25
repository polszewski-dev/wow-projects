package wow.minmax.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.equipment.EquippableItem;
import wow.commons.model.item.Enchant;
import wow.commons.model.item.Gem;
import wow.commons.model.item.Item;
import wow.commons.model.pve.Phase;
import wow.minmax.converter.dto.*;
import wow.minmax.model.PlayerProfile;
import wow.minmax.model.dto.*;
import wow.minmax.service.ItemService;
import wow.minmax.service.PlayerProfileService;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

/**
 * User: POlszewski
 * Date: 2021-12-14
 */
@RestController
@RequestMapping("api/v1/profile")
@AllArgsConstructor
public class PlayerProfileController {
	private final PlayerProfileService playerProfileService;
	private final ItemService itemService;
	private final PlayerProfileConverter playerProfileConverter;
	private final EquipmentConverter equipmentConverter;
	private final EnchantConverter enchantConverter;
	private final GemConverter gemConverter;
	private final BuffConverter buffConverter;

	@GetMapping(path = "list")
	public List<PlayerProfileDTO> getPlayerProfileList() {
		List<PlayerProfile> playerProfiles = playerProfileService.getPlayerProfileList();
		List<PlayerProfileDTO> result = playerProfileConverter.convertList(playerProfiles);

		for (PlayerProfileDTO profile : result) {
			profile.setEquipment(null);
			profile.setBuffs(null);
			profile.setTalents(null);
		}

		return result;
	}

	@GetMapping(path = "create/name/{profileName}/phase/{phase}")
	public PlayerProfileDTO createPlayerProfile(
			@PathVariable("profileName") String profileName,
			@PathVariable("phase") Phase phase
	) {
		PlayerProfile createdProfile = playerProfileService.createPlayerProfile(profileName, phase);
		return playerProfileConverter.convert(createdProfile);
	}

	@GetMapping(path = "copy/{copiedProfileId}/name/{profileName}/phase/{phase}")
	public PlayerProfileDTO createPlayerProfile(
			@PathVariable("copiedProfileId") UUID copiedProfileId,
			@PathVariable("profileName") String profileName,
			@PathVariable("phase") Phase phase
	) {
		PlayerProfile createdProfile = playerProfileService.copyPlayerProfile(copiedProfileId, profileName, phase);
		return playerProfileConverter.convert(createdProfile);
	}

	@GetMapping(path = "{profileId}")
	public PlayerProfileDTO getPlayerProfile(
			@PathVariable("profileId") UUID profileId
	) {
		return getPlayerProfile(profileId, false);
	}

	@GetMapping(path = "{profileId}/add/options/{addOptions}")
	public PlayerProfileDTO getPlayerProfile(
			@PathVariable("profileId") UUID profileId,
			@PathVariable("addOptions") boolean addOptions
	) {
		PlayerProfile playerProfile = playerProfileService.getPlayerProfile(profileId);
		PlayerProfileDTO playerProfileDTO = playerProfileConverter.convert(playerProfile);

		if (addOptions) {
			for (EquippableItemDTO item : playerProfileDTO.getEquipment().toList()) {
				addItemOptions(item, playerProfileDTO.getPhase());
			}
		}

		return playerProfileDTO;
	}

	@GetMapping(path = "{profileId}/change/item/{slot}/{itemId}")
	public EquippableItemDTO changeItem(
			@PathVariable("profileId") UUID profileId,
			@PathVariable("slot") ItemSlot slot,
			@PathVariable("itemId") int itemId
	) {
		PlayerProfile playerProfile = playerProfileService.changeItem(profileId, slot, itemId);

		EquippableItemDTO dto = convertEquippableItem(playerProfile, slot);
		addItemOptions(dto, playerProfile.getPhase());
		return dto;
	}

	@GetMapping(path = "{profileId}/change/enchant/{slot}/{enchantId}")
	public EquippableItemDTO changeEnchant(
			@PathVariable("profileId") UUID profileId,
			@PathVariable("slot") ItemSlot slot,
			@PathVariable("enchantId") int enchantId
	) {
		PlayerProfile playerProfile = playerProfileService.changeEnchant(profileId, slot, enchantId);

		return convertEquippableItem(playerProfile, slot);
	}

	@GetMapping(path = "{profileId}/change/gem/{slot}/{socketNo}/{gemId}")
	public EquippableItemDTO changeGem(
			@PathVariable("profileId") UUID profileId,
			@PathVariable("slot") ItemSlot slot,
			@PathVariable("socketNo") int socketNo,
			@PathVariable("gemId") int gemId) {
		PlayerProfile playerProfile = playerProfileService.changeGem(profileId, slot, socketNo, gemId);

		return convertEquippableItem(playerProfile, slot);
	}

	@GetMapping(path = "{profileId}/enable/buff/{buffId}/{enabled}")
	public List<BuffDTO> enableBuff(
			@PathVariable("profileId") UUID profileId,
			@PathVariable("buffId") int buffId,
			@PathVariable("enabled") boolean enabled) {
		PlayerProfile playerProfile = playerProfileService.enableBuff(profileId, buffId, enabled);

		return buffConverter.convertList(playerProfile.getBuffs());
	}

	@GetMapping(path = "{profileId}/reset/equipment")
	public PlayerProfileDTO resetProfile(
			@PathVariable("profileId") UUID profileId
	) {
		PlayerProfile playerProfile = playerProfileService.resetEquipment(profileId);
		PlayerProfileDTO playerProfileDTO = playerProfileConverter.convert(playerProfile);

		for (EquippableItemDTO item : playerProfileDTO.getEquipment().toList()) {
			addItemOptions(item, playerProfileDTO.getPhase());
		}

		return playerProfileDTO;
	}

	private EquippableItemDTO convertEquippableItem(PlayerProfile playerProfile, ItemSlot slot) {
		EquippableItem equippableItem = playerProfile.getEquipment().get(slot);
		return equipmentConverter.convertItem(playerProfile.getEquipment(), equippableItem);
	}

	private void addItemOptions(EquippableItemDTO dto, Phase phase) {
		Item item = itemService.getItem(dto.getItem().getId());
		dto.setItemOptions(new ItemOptionsDTO(
				getEnchants(item, phase),
				getAvailableGems(item, 1, phase),
				getAvailableGems(item, 2, phase),
				getAvailableGems(item, 3, phase)
		));
	}

	private List<EnchantDTO> getEnchants(Item item, Phase phase) {
		List<Enchant> enchants = itemService.getAvailableEnchants(item, phase);
		enchants.sort(Comparator.comparing(Enchant::getName));
		return enchantConverter.convertList(enchants);
	}

	private List<GemDTO> getAvailableGems(Item item, int socketNo, Phase phase) {
		List<Gem> gems = itemService.getAvailableGems(item, socketNo, phase, false);
		gems.sort(Comparator.comparing(this::sourceBasedOrder)
							.thenComparing(Comparator.comparing(Gem::getRarity).reversed())
							.thenComparing(Gem::getColor)
							.thenComparing(Gem::getName));
		return gemConverter.convertList(gems);
	}

	private int sourceBasedOrder(Gem gem) {
		if (gem.isCrafted()) {
			return 1;
		}
		if(gem.isPvPReward()) {
			return 2;
		}
		return 3;
	}
}
