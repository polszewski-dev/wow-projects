package wow.minmax.converter.model.equipment;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.character.model.equipment.Equipment;
import wow.commons.client.converter.Converter;
import wow.commons.client.converter.ParametrizedBackConverter;
import wow.commons.model.pve.PhaseId;
import wow.minmax.model.equipment.EquipmentConfig;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * User: POlszewski
 * Date: 2021-12-14
 */
@Component
@AllArgsConstructor
public class EquipmentConfigConverter implements Converter<Equipment, EquipmentConfig>, ParametrizedBackConverter<Equipment, EquipmentConfig, PhaseId> {
	private final EquippableItemConfigConverter equippableItemConfigConverter;

	@Override
	public EquipmentConfig doConvert(Equipment source) {
		var itemsBySlot = source.toMap().entrySet().stream()
				.collect(Collectors.toMap(
						Map.Entry::getKey, e -> equippableItemConfigConverter.convert(e.getValue()))
				);

		return new EquipmentConfig(itemsBySlot);
	}

	@Override
	public Equipment doConvertBack(EquipmentConfig source, PhaseId phaseId) {
		var itemsBySlot = source.getItemsBySlot().entrySet().stream()
				.collect(Collectors.toMap(
						Map.Entry::getKey, e -> equippableItemConfigConverter.convertBack(e.getValue(), phaseId))
				);

		Equipment equipment = new Equipment();

		for (var entry : itemsBySlot.entrySet()) {
			equipment.equip(entry.getValue(), entry.getKey());
		}

		return equipment;
	}
}
