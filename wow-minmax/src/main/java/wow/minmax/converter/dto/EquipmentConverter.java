package wow.minmax.converter.dto;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.commons.model.equipment.Equipment;
import wow.commons.model.equipment.EquippableItem;
import wow.minmax.converter.Converter;
import wow.minmax.model.dto.EquipmentDTO;
import wow.minmax.model.dto.EquippableItemDTO;

/**
 * User: POlszewski
 * Date: 2021-12-14
 */
@Component
@AllArgsConstructor
public class EquipmentConverter extends Converter<Equipment, EquipmentDTO> {
	private final EquippableItemConverter equippableItemConverter;

	@Override
	protected EquipmentDTO doConvert(Equipment equipment) {
		return new EquipmentDTO(
				convertItem(equipment, equipment.getHead()),
				convertItem(equipment, equipment.getNeck()),
				convertItem(equipment, equipment.getShoulder()),
				convertItem(equipment, equipment.getBack()),
				convertItem(equipment, equipment.getChest()),
				convertItem(equipment, equipment.getWrist()),
				convertItem(equipment, equipment.getHand()),
				convertItem(equipment, equipment.getWaist()),
				convertItem(equipment, equipment.getLegs()),
				convertItem(equipment, equipment.getFeet()),
				convertItem(equipment, equipment.getRing1()),
				convertItem(equipment, equipment.getRing2()),
				convertItem(equipment, equipment.getTrinket1()),
				convertItem(equipment, equipment.getTrinket2()),
				convertItem(equipment, equipment.getMain()),
				convertItem(equipment, equipment.getOff()),
				convertItem(equipment, equipment.getWand())
		);
	}

	public EquippableItemDTO convertItem(Equipment equipment, EquippableItem item) {
		EquippableItemDTO dto = equippableItemConverter.convert(item);
		if (dto != null) {
			dto.setSocket1Matching(item.insertedGemMatchesSocketColor(1));
			dto.setSocket2Matching(item.insertedGemMatchesSocketColor(2));
			dto.setSocket3Matching(item.insertedGemMatchesSocketColor(3));
			dto.setSocketBonusEnabled(equipment.allGemsMatch(item));
		}
		return dto;
	}
}
