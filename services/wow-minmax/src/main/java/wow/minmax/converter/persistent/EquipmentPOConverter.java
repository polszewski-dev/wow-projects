package wow.minmax.converter.persistent;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.character.model.equipment.Equipment;
import wow.minmax.converter.Converter;
import wow.minmax.converter.ParametrizedBackConverter;
import wow.minmax.model.persistent.EquipmentPO;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * User: POlszewski
 * Date: 2021-12-14
 */
@Component
@AllArgsConstructor
public class EquipmentPOConverter implements Converter<Equipment, EquipmentPO>, ParametrizedBackConverter<Equipment, EquipmentPO> {
	private final EquippableItemPOConverter equippableItemPOConverter;

	@Override
	public EquipmentPO doConvert(Equipment source) {
		var itemsBySlot = source.toMap().entrySet().stream()
				.collect(Collectors.toMap(
						Map.Entry::getKey, e -> equippableItemPOConverter.convert(e.getValue()))
				);

		return new EquipmentPO(itemsBySlot);
	}

	@Override
	public Equipment doConvertBack(EquipmentPO source, Map<String, Object> params) {
		var itemsBySlot = source.getItemsBySlot().entrySet().stream()
				.collect(Collectors.toMap(
						Map.Entry::getKey, e -> equippableItemPOConverter.convertBack(e.getValue(), params))
				);

		Equipment equipment = new Equipment();

		for (var entry : itemsBySlot.entrySet()) {
			equipment.equip(entry.getValue(), entry.getKey());
		}

		return equipment;
	}
}
