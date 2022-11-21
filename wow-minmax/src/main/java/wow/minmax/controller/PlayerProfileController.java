package wow.minmax.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * User: POlszewski
 * Date: 2021-12-14
 */
@RestController
@RequestMapping("api/v1/profile")
@AllArgsConstructor
@Slf4j
public class PlayerProfileController {
	private final PlayerProfileService playerProfileService;
	private final ItemService itemService;
	private final PlayerProfileConverter playerProfileConverter;
	private final EquipmentConverter equipmentConverter;
	private final ItemConverter itemConverter;
	private final EnchantConverter enchantConverter;
	private final GemConverter gemConverter;
	private final BuffConverter buffConverter;

	@GetMapping("list")
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

	@GetMapping("{profileId}")
	public PlayerProfileDTO getPlayerProfile(
			@PathVariable("profileId") UUID profileId,
			@RequestParam(value = "addOptions", required = false, defaultValue = "false") boolean addOptions
	) {
		PlayerProfile playerProfile = playerProfileService.getPlayerProfile(profileId);
		PlayerProfileDTO playerProfileDTO = playerProfileConverter.convert(playerProfile);
		addItemOptions(addOptions, playerProfileDTO, playerProfile);
		return playerProfileDTO;
	}

	@GetMapping("create/name/{profileName}/phase/{phase}")
	public PlayerProfileDTO createPlayerProfile(
			@PathVariable("profileName") String profileName,
			@PathVariable("phase") Phase phase,
			@RequestParam(value = "addOptions", required = false, defaultValue = "false") boolean addOptions
	) {
		PlayerProfile createdProfile = playerProfileService.createPlayerProfile(profileName, phase);
		PlayerProfileDTO createdProfileDTO = playerProfileConverter.convert(createdProfile);
		addItemOptions(addOptions, createdProfileDTO, createdProfile);
		log.info("Created profile id: {}, name: {}", createdProfile.getProfileId(), createdProfile.getProfileName());
		return createdProfileDTO;
	}

	@GetMapping("copy/{copiedProfileId}/name/{profileName}/phase/{phase}")
	public PlayerProfileDTO createPlayerProfile(
			@PathVariable("copiedProfileId") UUID copiedProfileId,
			@PathVariable("profileName") String profileName,
			@PathVariable("phase") Phase phase,
			@RequestParam(value = "addOptions", required = false, defaultValue = "false") boolean addOptions
	) {
		PlayerProfile createdProfile = playerProfileService.copyPlayerProfile(copiedProfileId, profileName, phase);
		PlayerProfileDTO createdProfileDTO = playerProfileConverter.convert(createdProfile);
		addItemOptions(addOptions, createdProfileDTO, createdProfile);
		log.info("Copied profile id: {}, name: {}, sourceId: {}", createdProfile.getProfileId(), createdProfile.getProfileName(), copiedProfileId);
		return createdProfileDTO;
	}

	@GetMapping("{profileId}/change/item/{slot}/{itemId}")
	public EquippableItemDTO changeItem(
			@PathVariable("profileId") UUID profileId,
			@PathVariable("slot") ItemSlot slot,
			@PathVariable("itemId") int itemId
	) {
		PlayerProfile playerProfile = playerProfileService.changeItem(profileId, slot, itemId);
		EquippableItemDTO dto = convertEquippableItem(playerProfile, slot);
		addItemOptions(dto, playerProfile);
		log.info("Changed item profile id: {}, slot: {}, itemId: {}", profileId, slot, itemId);
		return dto;
	}

	@GetMapping("{profileId}/change/enchant/{slot}/{enchantId}")
	public EquippableItemDTO changeEnchant(
			@PathVariable("profileId") UUID profileId,
			@PathVariable("slot") ItemSlot slot,
			@PathVariable("enchantId") int enchantId
	) {
		PlayerProfile playerProfile = playerProfileService.changeEnchant(profileId, slot, enchantId);
		log.info("Changed enchant profile id: {}, slot: {}, itemId: {}", profileId, slot, enchantId);
		return convertEquippableItem(playerProfile, slot);
	}

	@GetMapping("{profileId}/change/gem/{slot}/{socketNo}/{gemId}")
	public EquippableItemDTO changeGem(
			@PathVariable("profileId") UUID profileId,
			@PathVariable("slot") ItemSlot slot,
			@PathVariable("socketNo") int socketNo,
			@PathVariable("gemId") int gemId) {
		PlayerProfile playerProfile = playerProfileService.changeGem(profileId, slot, socketNo, gemId);
		log.info("Changed gem profile id: {}, slot: {}, socketNo: {}, gemId: {}", profileId, slot, socketNo, gemId);
		return convertEquippableItem(playerProfile, slot);
	}

	@GetMapping("{profileId}/enable/buff/{buffId}/{enabled}")
	public List<BuffDTO> enableBuff(
			@PathVariable("profileId") UUID profileId,
			@PathVariable("buffId") int buffId,
			@PathVariable("enabled") boolean enabled) {
		PlayerProfile playerProfile = playerProfileService.enableBuff(profileId, buffId, enabled);
		log.info("Changed buff profile id: {}, buffId: {}, enabled: {}", profileId, buffId, enabled);
		return buffConverter.convertList(new ArrayList<>(playerProfile.getBuffs()));
	}

	@GetMapping("{profileId}/reset/equipment")
	public PlayerProfileDTO resetProfile(
			@PathVariable("profileId") UUID profileId,
			@RequestParam(value = "addOptions", required = false, defaultValue = "false") boolean addOptions
	) {
		PlayerProfile playerProfile = playerProfileService.resetEquipment(profileId);
		PlayerProfileDTO playerProfileDTO = playerProfileConverter.convert(playerProfile);
		addItemOptions(addOptions, playerProfileDTO, playerProfile);
		log.info("Reset profile id: {}", profileId);
		return playerProfileDTO;
	}

	private EquippableItemDTO convertEquippableItem(PlayerProfile playerProfile, ItemSlot slot) {
		EquippableItem equippableItem = playerProfile.getEquipment().get(slot);
		return equipmentConverter.convertItem(playerProfile.getEquipment(), equippableItem);
	}

	private void addItemOptions(boolean addOptions, PlayerProfileDTO playerProfileDTO, PlayerProfile playerProfile) {
		if (!addOptions) {
			return;
		}

		for (ItemSlot slot : ItemSlot.getDpsSlots()) {
			EquipmentSlotDTO slotDTO = playerProfileDTO.getEquipment().getEquipmentSlotsByType().get(slot);

			List<Item> itemsBySlot = itemService.getItemsBySlot(playerProfile, slot);
			slotDTO.setAvailableItems(getItemsDTOsOrderedByScore(itemsBySlot));

			if (slotDTO.getEquippableItem() != null) {
				addItemOptions(slotDTO.getEquippableItem(), playerProfile);
			}
		}
	}

	private void addItemOptions(EquippableItemDTO dto, PlayerProfile playerProfile) {
		Item item = itemService.getItem(dto.getItem().getId());
		dto.setItemOptions(new ItemOptionsDTO(
				getEnchants(item, playerProfile),
				getAvailableGems(playerProfile, item)
		));
	}

	private List<List<GemDTO>> getAvailableGems(PlayerProfile playerProfile, Item item) {
		return Stream.iterate(0, i -> i < item.getSocketCount(), i -> i + 1)
				.map(i -> getAvailableGems(item, i, playerProfile))
				.collect(Collectors.toList());
	}

	private List<EnchantDTO> getEnchants(Item item, PlayerProfile playerProfile) {
		List<Enchant> enchants = itemService.getEnchants(playerProfile, item.getItemType()).stream()
				.sorted(Comparator.comparing(Enchant::getName))
				.collect(Collectors.toList());
		return enchantConverter.convertList(enchants);
	}

	private List<GemDTO> getAvailableGems(Item item, int socketNo, PlayerProfile playerProfile) {
		List<Gem> gems = itemService.getGems(playerProfile, item, socketNo, false).stream()
				.sorted(getGemComparator())
				.collect(Collectors.toList());
		return gemConverter.convertList(gems);
	}

	private Comparator<Gem> getGemComparator() {
		return Comparator.comparingInt(this::sourceBasedOrder)
				.thenComparing(Comparator.comparing(Gem::getRarity).reversed())
				.thenComparing(Gem::getColor)
				.thenComparing(Gem::getName);
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

	private List<ItemDTO> getItemsDTOsOrderedByScore(List<Item> items) {
		return items.stream()
				.map(this::getItemDTO)
				.sorted(Comparator.comparing(ItemDTO::getScore).reversed().thenComparing(ItemDTO::getName))
				.collect(Collectors.toList());
	}

	private ItemDTO getItemDTO(Item item) {
		ItemDTO itemDTO = itemConverter.convert(item);
		itemDTO.setScore(item.getItemLevel());
		return itemDTO;
	}
}
