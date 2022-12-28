package wow.minmax.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wow.character.model.equipment.Equipment;
import wow.character.model.equipment.EquippableItem;
import wow.character.service.ItemService;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.categorization.ItemType;
import wow.commons.model.item.SocketType;
import wow.minmax.converter.dto.*;
import wow.minmax.model.PlayerProfile;
import wow.minmax.model.dto.*;
import wow.minmax.service.PlayerProfileService;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * User: POlszewski
 * Date: 2022-12-28
 */
@RestController
@RequestMapping("api/v1/equipment")
@AllArgsConstructor
@Slf4j
public class EquipmentController {
	private final PlayerProfileService playerProfileService;
	private final ItemService itemService;
	private final EquipmentConverter equipmentConverter;
	private final EquippableItemConverter equippableItemConverter;
	private final ItemConverter itemConverter;
	private final EnchantConverter enchantConverter;
	private final GemConverter gemConverter;

	@GetMapping("{profileId}")
	public EquipmentDTO getEquipment(
			@PathVariable("profileId") UUID profileId
	) {
		PlayerProfile playerProfile = playerProfileService.getPlayerProfile(profileId);
		return equipmentConverter.convert(playerProfile.getEquipment());
	}

	@GetMapping("{profileId}/options")
	public EquipmentOptionsDTO getEquipmentOptions(
			@PathVariable("profileId") UUID profileId
	) {
		PlayerProfile playerProfile = playerProfileService.getPlayerProfile(profileId);

		var itemsByItemSlot = ItemSlot.getDpsSlots().stream()
				.collect(Collectors.toMap(x -> x, x -> itemConverter.convertList(itemService.getItemsBySlot(playerProfile.getCharacter(), x))));
		var enchantsByItemType = Stream.of(ItemType.values())
				.collect(Collectors.toMap(x -> x, x -> enchantConverter.convertList(itemService.getEnchants(playerProfile.getCharacter(), x))));
		var gemsBySocketType = Stream.of(SocketType.values())
				.collect(Collectors.toMap(x -> x, x -> gemConverter.convertList(itemService.getGems(playerProfile.getCharacter(), x, false))));

		return new EquipmentOptionsDTO(itemsByItemSlot, enchantsByItemType, gemsBySocketType);
	}

	@GetMapping("{profileId}/change/item/{slot}/{itemId}")
	public EquippableItemDTO changeItem(
			@PathVariable("profileId") UUID profileId,
			@PathVariable("slot") ItemSlot slot,
			@PathVariable("itemId") int itemId
	) {
		PlayerProfile playerProfile = playerProfileService.changeItem(profileId, slot, itemId);
		log.info("Changed item profile id: {}, slot: {}, itemId: {}", profileId, slot, itemId);
		return equippableItemConverter.convert(playerProfile.getEquippedItem(slot));
	}

	@GetMapping("{profileId}/change/enchant/{slot}/{enchantId}")
	public EquippableItemDTO changeEnchant(
			@PathVariable("profileId") UUID profileId,
			@PathVariable("slot") ItemSlot slot,
			@PathVariable("enchantId") int enchantId
	) {
		PlayerProfile playerProfile = playerProfileService.changeEnchant(profileId, slot, enchantId);
		log.info("Changed enchant profile id: {}, slot: {}, itemId: {}", profileId, slot, enchantId);
		return equippableItemConverter.convert(playerProfile.getEquippedItem(slot));
	}

	@GetMapping("{profileId}/change/gem/{slot}/{socketNo}/{gemId}")
	public EquippableItemDTO changeGem(
			@PathVariable("profileId") UUID profileId,
			@PathVariable("slot") ItemSlot slot,
			@PathVariable("socketNo") int socketNo,
			@PathVariable("gemId") int gemId) {
		PlayerProfile playerProfile = playerProfileService.changeGem(profileId, slot, socketNo, gemId);
		log.info("Changed gem profile id: {}, slot: {}, socketNo: {}, gemId: {}", profileId, slot, socketNo, gemId);
		return equippableItemConverter.convert(playerProfile.getEquippedItem(slot));
	}

	@GetMapping("{profileId}/reset")
	public EquipmentDTO resetEquipment(
			@PathVariable("profileId") UUID profileId
	) {
		PlayerProfile playerProfile = playerProfileService.resetEquipment(profileId);
		log.info("Reset profile id: {}", profileId);
		return equipmentConverter.convert(playerProfile.getEquipment());
	}

	@GetMapping("{profileId}/socket/status")
	public EquipmentSocketStatusDTO getEquipmentSocketStatus(
			@PathVariable("profileId") UUID profileId
	) {
		PlayerProfile playerProfile = playerProfileService.getPlayerProfile(profileId);
		return new EquipmentSocketStatusDTO(ItemSlot.getDpsSlots().stream()
				.collect(Collectors.toMap(x -> x, x -> getItemSocketStatus(playerProfile, x)))
		);
	}

	private ItemSocketStatusDTO getItemSocketStatus(PlayerProfile playerProfile, ItemSlot itemSlot) {
		EquippableItem item = playerProfile.getEquippedItem(itemSlot);

		if (item == null) {
			return new ItemSocketStatusDTO(List.of(), new SocketBonusStatusDTO("", false));
		}

		Equipment equipment = playerProfile.getEquipment();

		var socketStatuses = Stream.iterate(0, i -> i < item.getSocketCount(), i -> i + 1)
				.map(socketNo -> new SocketStatusDTO(
						item.getSocketType(socketNo),
						equipment.hasMatchingGem(item, socketNo)
				)).collect(Collectors.toList());

		var socketBonusStatus = new SocketBonusStatusDTO(
				item.getSocketBonus().statString(),
				equipment.allSocketsHaveMatchingGems(item)
		);

		return new ItemSocketStatusDTO(socketStatuses, socketBonusStatus);
	}
}
