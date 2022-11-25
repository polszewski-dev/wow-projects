package wow.minmax.converter.persistent;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.equipment.Equipment;
import wow.minmax.converter.Converter;
import wow.minmax.model.persistent.EquipmentPO;

/**
 * User: POlszewski
 * Date: 2021-12-14
 */
@Component
@AllArgsConstructor
public class EquipmentPOConverter extends Converter<Equipment, EquipmentPO> {
	private final EquippableItemPOConverter equippableItemPOConverter;

	@Override
	protected EquipmentPO doConvert(Equipment equipment) {
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
	protected Equipment doConvertBack(EquipmentPO value) {
		Equipment equipment = new Equipment();

		equipment.set(equippableItemPOConverter.convertBack(value.getHead()));
		equipment.set(equippableItemPOConverter.convertBack(value.getNeck()));
		equipment.set(equippableItemPOConverter.convertBack(value.getShoulder()));
		equipment.set(equippableItemPOConverter.convertBack(value.getBack()));
		equipment.set(equippableItemPOConverter.convertBack(value.getChest()));
		equipment.set(equippableItemPOConverter.convertBack(value.getWrist()));
		equipment.set(equippableItemPOConverter.convertBack(value.getHands()));
		equipment.set(equippableItemPOConverter.convertBack(value.getWaist()));
		equipment.set(equippableItemPOConverter.convertBack(value.getLegs()));
		equipment.set(equippableItemPOConverter.convertBack(value.getFeet()));
		equipment.set(equippableItemPOConverter.convertBack(value.getFinger1()), ItemSlot.FINGER_1);
		equipment.set(equippableItemPOConverter.convertBack(value.getFinger2()), ItemSlot.FINGER_2);
		equipment.set(equippableItemPOConverter.convertBack(value.getTrinket1()), ItemSlot.TRINKET_1);
		equipment.set(equippableItemPOConverter.convertBack(value.getTrinket2()), ItemSlot.TRINKET_2);
		equipment.set(equippableItemPOConverter.convertBack(value.getMainHand()), ItemSlot.MAIN_HAND);
		equipment.set(equippableItemPOConverter.convertBack(value.getOffHand()), ItemSlot.OFF_HAND);
		equipment.set(equippableItemPOConverter.convertBack(value.getRanged()));

		return equipment;
	}
}
