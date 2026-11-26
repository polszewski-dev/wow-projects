package wow.minmax.converter.db.equipment;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.character.model.equipment.Equipment;
import wow.commons.client.converter.Converter;
import wow.commons.client.converter.ParametrizedBackConverter;
import wow.minmax.model.Player;
import wow.minmax.model.db.equipment.EquipmentConfig;

/**
 * User: POlszewski
 * Date: 2021-12-14
 */
@Component
@AllArgsConstructor
public class EquipmentConfigConverter implements Converter<Equipment, EquipmentConfig>, ParametrizedBackConverter<Void, EquipmentConfig, Player> {
	private final EquippableItemConfigConverter equippableItemConfigConverter;

	@Override
	public EquipmentConfig doConvert(Equipment source) {
		var itemsBySlot = equippableItemConfigConverter.convertMap(source.toMap());

		return new EquipmentConfig(itemsBySlot);
	}

	@Override
	public Void doConvertBack(EquipmentConfig source, Player player) {
		var itemsBySlot = equippableItemConfigConverter.convertBackMap(source.getItemsBySlot(), player.getPhaseId());

		for (var entry : itemsBySlot.entrySet()) {
			player.equip(entry.getValue(), entry.getKey());
		}

		return null;
	}
}
