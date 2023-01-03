package wow.minmax.converter.persistent;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.character.model.equipment.Equipment;
import wow.commons.model.categorization.ItemSlot;
import wow.minmax.converter.Converter;
import wow.minmax.converter.ParametrizedBackConverter;
import wow.minmax.model.persistent.EquipmentPO;

import java.util.Map;

/**
 * User: POlszewski
 * Date: 2021-12-14
 */
@Component
@AllArgsConstructor
public class EquipmentPOConverter implements Converter<Equipment, EquipmentPO>, ParametrizedBackConverter<Equipment, EquipmentPO> {
	private final EquippableItemPOConverter equippableItemPOConverter;

	@Override
	public EquipmentPO doConvert(Equipment equipment) {
		return new EquipmentPO(
				equippableItemPOConverter.convert(equipment.getHead()),
				equippableItemPOConverter.convert(equipment.getNeck()),
				equippableItemPOConverter.convert(equipment.getShoulder()),
				equippableItemPOConverter.convert(equipment.getBack()),
				equippableItemPOConverter.convert(equipment.getChest()),
				equippableItemPOConverter.convert(equipment.getWrist()),
				equippableItemPOConverter.convert(equipment.getHands()),
				equippableItemPOConverter.convert(equipment.getWaist()),
				equippableItemPOConverter.convert(equipment.getLegs()),
				equippableItemPOConverter.convert(equipment.getFeet()),
				equippableItemPOConverter.convert(equipment.getFinger1()),
				equippableItemPOConverter.convert(equipment.getFinger2()),
				equippableItemPOConverter.convert(equipment.getTrinket1()),
				equippableItemPOConverter.convert(equipment.getTrinket2()),
				equippableItemPOConverter.convert(equipment.getMainHand()),
				equippableItemPOConverter.convert(equipment.getOffHand()),
				equippableItemPOConverter.convert(equipment.getRanged())
		);
	}

	@Override
	public Equipment doConvertBack(EquipmentPO value, Map<String, Object> params) {
		Equipment equipment = new Equipment();

		equipment.equip(equippableItemPOConverter.convertBack(value.getHead(), params));
		equipment.equip(equippableItemPOConverter.convertBack(value.getNeck(), params));
		equipment.equip(equippableItemPOConverter.convertBack(value.getShoulder(), params));
		equipment.equip(equippableItemPOConverter.convertBack(value.getBack(), params));
		equipment.equip(equippableItemPOConverter.convertBack(value.getChest(), params));
		equipment.equip(equippableItemPOConverter.convertBack(value.getWrist(), params));
		equipment.equip(equippableItemPOConverter.convertBack(value.getHands(), params));
		equipment.equip(equippableItemPOConverter.convertBack(value.getWaist(), params));
		equipment.equip(equippableItemPOConverter.convertBack(value.getLegs(), params));
		equipment.equip(equippableItemPOConverter.convertBack(value.getFeet(), params));
		equipment.equip(equippableItemPOConverter.convertBack(value.getFinger1(), params), ItemSlot.FINGER_1);
		equipment.equip(equippableItemPOConverter.convertBack(value.getFinger2(), params), ItemSlot.FINGER_2);
		equipment.equip(equippableItemPOConverter.convertBack(value.getTrinket1(), params), ItemSlot.TRINKET_1);
		equipment.equip(equippableItemPOConverter.convertBack(value.getTrinket2(), params), ItemSlot.TRINKET_2);
		equipment.equip(equippableItemPOConverter.convertBack(value.getMainHand(), params), ItemSlot.MAIN_HAND);
		equipment.equip(equippableItemPOConverter.convertBack(value.getOffHand(), params), ItemSlot.OFF_HAND);
		equipment.equip(equippableItemPOConverter.convertBack(value.getRanged(), params));

		return equipment;
	}
}
