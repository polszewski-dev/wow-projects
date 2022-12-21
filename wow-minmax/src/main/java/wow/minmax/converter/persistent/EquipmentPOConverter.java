package wow.minmax.converter.persistent;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.character.model.equipment.Equipment;
import wow.commons.model.categorization.ItemSlot;
import wow.minmax.converter.ParametrizedConverter;
import wow.minmax.model.persistent.EquipmentPO;

import java.util.Map;

/**
 * User: POlszewski
 * Date: 2021-12-14
 */
@Component
@AllArgsConstructor
public class EquipmentPOConverter extends ParametrizedConverter<Equipment, EquipmentPO> {
	private final EquippableItemPOConverter equippableItemPOConverter;

	@Override
	protected EquipmentPO doConvert(Equipment equipment, Map<String, Object> params) {
		return new EquipmentPO(
				equippableItemPOConverter.convert(equipment.getHead(), params),
				equippableItemPOConverter.convert(equipment.getNeck(), params),
				equippableItemPOConverter.convert(equipment.getShoulder(), params),
				equippableItemPOConverter.convert(equipment.getBack(), params),
				equippableItemPOConverter.convert(equipment.getChest(), params),
				equippableItemPOConverter.convert(equipment.getWrist(), params),
				equippableItemPOConverter.convert(equipment.getHands(), params),
				equippableItemPOConverter.convert(equipment.getWaist(), params),
				equippableItemPOConverter.convert(equipment.getLegs(), params),
				equippableItemPOConverter.convert(equipment.getFeet(), params),
				equippableItemPOConverter.convert(equipment.getFinger1(), params),
				equippableItemPOConverter.convert(equipment.getFinger2(), params),
				equippableItemPOConverter.convert(equipment.getTrinket1(), params),
				equippableItemPOConverter.convert(equipment.getTrinket2(), params),
				equippableItemPOConverter.convert(equipment.getMainHand(), params),
				equippableItemPOConverter.convert(equipment.getOffHand(), params),
				equippableItemPOConverter.convert(equipment.getRanged(), params)
		);
	}

	@Override
	protected Equipment doConvertBack(EquipmentPO value, Map<String, Object> params) {
		Equipment equipment = new Equipment();

		equipment.set(equippableItemPOConverter.convertBack(value.getHead(), params));
		equipment.set(equippableItemPOConverter.convertBack(value.getNeck(), params));
		equipment.set(equippableItemPOConverter.convertBack(value.getShoulder(), params));
		equipment.set(equippableItemPOConverter.convertBack(value.getBack(), params));
		equipment.set(equippableItemPOConverter.convertBack(value.getChest(), params));
		equipment.set(equippableItemPOConverter.convertBack(value.getWrist(), params));
		equipment.set(equippableItemPOConverter.convertBack(value.getHands(), params));
		equipment.set(equippableItemPOConverter.convertBack(value.getWaist(), params));
		equipment.set(equippableItemPOConverter.convertBack(value.getLegs(), params));
		equipment.set(equippableItemPOConverter.convertBack(value.getFeet(), params));
		equipment.set(equippableItemPOConverter.convertBack(value.getFinger1(), params), ItemSlot.FINGER_1);
		equipment.set(equippableItemPOConverter.convertBack(value.getFinger2(), params), ItemSlot.FINGER_2);
		equipment.set(equippableItemPOConverter.convertBack(value.getTrinket1(), params), ItemSlot.TRINKET_1);
		equipment.set(equippableItemPOConverter.convertBack(value.getTrinket2(), params), ItemSlot.TRINKET_2);
		equipment.set(equippableItemPOConverter.convertBack(value.getMainHand(), params), ItemSlot.MAIN_HAND);
		equipment.set(equippableItemPOConverter.convertBack(value.getOffHand(), params), ItemSlot.OFF_HAND);
		equipment.set(equippableItemPOConverter.convertBack(value.getRanged(), params));

		return equipment;
	}
}
