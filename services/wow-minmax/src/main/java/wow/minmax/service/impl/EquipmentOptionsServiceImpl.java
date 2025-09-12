package wow.minmax.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import wow.character.model.character.PlayerCharacter;
import wow.character.model.equipment.ItemFilter;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.categorization.ItemSubType;
import wow.commons.model.categorization.ItemType;
import wow.commons.model.item.Item;
import wow.commons.model.item.SocketType;
import wow.minmax.model.CharacterId;
import wow.minmax.model.config.CharacterFeature;
import wow.minmax.model.options.EnchantOptions;
import wow.minmax.model.options.EquipmentOptions;
import wow.minmax.model.options.GemOptions;
import wow.minmax.model.options.ItemOptions;
import wow.minmax.repository.MinmaxConfigRepository;
import wow.minmax.service.EquipmentOptionsService;
import wow.minmax.service.ItemService;
import wow.minmax.service.PlayerCharacterService;

import java.util.List;
import java.util.stream.Stream;

/**
 * User: POlszewski
 * Date: 2025-08-30
 */
@Service
@AllArgsConstructor
public class EquipmentOptionsServiceImpl implements EquipmentOptionsService {
	private final PlayerCharacterService playerCharacterService;
	private final ItemService itemService;
	private final MinmaxConfigRepository minmaxConfigRepository;

	@Override
	public EquipmentOptions getEquipmentOptions(CharacterId characterId) {
		var player = playerCharacterService.getPlayer(characterId);
		var gems = minmaxConfigRepository.hasFeature(player, CharacterFeature.GEMS);
		var heroics = minmaxConfigRepository.hasFeature(player, CharacterFeature.HEROICS);

		return new EquipmentOptions(gems, heroics);
	}

	@Override
	public ItemOptions getItemOptions(CharacterId characterId, ItemSlot itemSlot) {
		var player = playerCharacterService.getPlayer(characterId);

		return getItemOptions(player, itemSlot);
	}

	private ItemOptions getItemOptions(PlayerCharacter player, ItemSlot itemSlot) {
		var itemFilter = ItemFilter.everything();
		var items = itemService.getItemsBySlot(player, itemSlot, itemFilter);

		return new ItemOptions(itemSlot, items);
	}

	private List<ItemOptions> getItemOptions(PlayerCharacter player) {
		return ItemSlot.getDpsSlots().stream()
				.map(itemSlot -> getItemOptions(player, itemSlot))
				.toList();
	}

	@Override
	public List<EnchantOptions> getEnchantOptions(CharacterId characterId) {
		var player = playerCharacterService.getPlayer(characterId);
		var itemOptions = getItemOptions(player);

		return itemOptions.stream()
				.flatMap(x -> x.items().stream())
				.map(ItemTypeAndSubtype::new)
				.distinct()
				.map(x -> getEnchantOptions(player, x))
				.toList();
	}

	private EnchantOptions getEnchantOptions(PlayerCharacter player, ItemTypeAndSubtype x) {
		var enchants = itemService.getEnchants(player, x.itemType(), x.itemSubType());

		return new EnchantOptions(
				x.itemType,
				x.itemSubType,
				enchants
		);
	}

	private record ItemTypeAndSubtype(
			ItemType itemType,
			ItemSubType itemSubType
	) {
		ItemTypeAndSubtype(Item item) {
			this(item.getItemType(), item.getItemSubType());
		}
	}

	@Override
	public List<GemOptions> getGemOptions(CharacterId characterId) {
		var player = playerCharacterService.getPlayer(characterId);

		return Stream.of(SocketType.values())
				.map(socketType -> getGemOptions(player, socketType))
				.toList();
	}

	private GemOptions getGemOptions(PlayerCharacter player, SocketType socketType) {
		var gems = itemService.getGems(player, socketType);

		return new GemOptions(socketType, gems);
	}
}
