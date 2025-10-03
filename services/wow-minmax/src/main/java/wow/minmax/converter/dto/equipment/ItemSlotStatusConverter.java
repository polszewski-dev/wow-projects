package wow.minmax.converter.dto.equipment;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.commons.client.converter.Converter;
import wow.minmax.client.dto.equipment.ItemSlotStatusDTO;
import wow.minmax.model.equipment.ItemSlotStatus;

/**
 * User: POlszewski
 * Date: 2025-10-04
 */
@Component
@AllArgsConstructor
public class ItemSlotStatusConverter implements Converter<ItemSlotStatus, ItemSlotStatusDTO> {
	private final EquippableItemConverter equippableItemConverter;

	@Override
	public ItemSlotStatusDTO doConvert(ItemSlotStatus source) {
		return new ItemSlotStatusDTO(
				source.itemSlot(),
				equippableItemConverter.convert(source.item())
		);
	}
}
