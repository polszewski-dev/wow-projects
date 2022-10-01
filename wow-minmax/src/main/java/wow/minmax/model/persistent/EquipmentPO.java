package wow.minmax.model.persistent;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * User: POlszewski
 * Date: 2021-12-14
 */
@Data
@AllArgsConstructor
public class EquipmentPO implements Serializable {
	private EquippableItemPO head;
	private EquippableItemPO neck;
	private EquippableItemPO shoulder;
	private EquippableItemPO back;
	private EquippableItemPO chest;
	private EquippableItemPO wrist;
	private EquippableItemPO hands;
	private EquippableItemPO waist;
	private EquippableItemPO legs;
	private EquippableItemPO feet;
	private EquippableItemPO finger1;
	private EquippableItemPO finger2;
	private EquippableItemPO trinket1;
	private EquippableItemPO trinket2;
	private EquippableItemPO mainHand;
	private EquippableItemPO offHand;
	private EquippableItemPO ranged;
}
