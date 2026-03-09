package wow.minmax.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wow.commons.model.categorization.ItemSlot;
import wow.minmax.client.dto.options.EnchantOptionsDTO;
import wow.minmax.client.dto.options.EquipmentOptionsDTO;
import wow.minmax.client.dto.options.GemOptionsDTO;
import wow.minmax.client.dto.options.ItemOptionsDTO;
import wow.minmax.converter.dto.options.EnchantOptionsConverter;
import wow.minmax.converter.dto.options.EquipmentOptionsConverter;
import wow.minmax.converter.dto.options.GemOptionsConverter;
import wow.minmax.converter.dto.options.ItemOptionsConverter;
import wow.minmax.model.PlayerId;
import wow.minmax.service.EquipmentOptionsService;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2024-04-17
 */
@RestController
@RequestMapping("api/v1/equipment-options")
@AllArgsConstructor
@Slf4j
public class EquipmentOptionsController {
	private final EquipmentOptionsService equipmentOptionsService;
	private final EquipmentOptionsConverter equipmentOptionsConverter;
	private final ItemOptionsConverter itemOptionsConverter;
	private final EnchantOptionsConverter enchantOptionsConverter;
	private final GemOptionsConverter gemOptionsConverter;

	@GetMapping("{playerId}")
	public EquipmentOptionsDTO getEquipmentOptions(
			@PathVariable("playerId") PlayerId playerId
	) {
		var equipmentOptions = equipmentOptionsService.getEquipmentOptions(playerId);

		return equipmentOptionsConverter.convert(equipmentOptions);
	}

	@GetMapping("{playerId}/item/{slot}")
	public ItemOptionsDTO getItemOptions(
			@PathVariable("playerId") PlayerId playerId,
			@PathVariable("slot") ItemSlot slot
	) {
		var itemOptions = equipmentOptionsService.getItemOptions(playerId, slot);

		return itemOptionsConverter.convert(itemOptions);
	}

	@GetMapping("{playerId}/enchant")
	public List<EnchantOptionsDTO> getEnchantOptions(
			@PathVariable("playerId") PlayerId playerId
	) {
		var enchantOptions = equipmentOptionsService.getEnchantOptions(playerId);

		return enchantOptionsConverter.convertList(enchantOptions);
	}

	@GetMapping("{playerId}/gem")
	public List<GemOptionsDTO> getGemOptions(
			@PathVariable("playerId") PlayerId playerId
	) {
		var gemOptions = equipmentOptionsService.getGemOptions(playerId);

		return gemOptionsConverter.convertList(gemOptions);
	}
}
