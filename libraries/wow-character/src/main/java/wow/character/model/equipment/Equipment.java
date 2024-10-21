package wow.character.model.equipment;

import wow.character.model.Copyable;
import wow.character.model.effect.EffectCollection;
import wow.character.model.effect.EffectCollector;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.categorization.ItemSlotGroup;
import wow.commons.model.categorization.ItemType;
import wow.commons.model.item.SocketType;

import java.util.*;

import static wow.commons.model.categorization.ItemSlot.*;

/**
 * User: POlszewski
 * Date: 2021-09-27
 */
public class Equipment implements EffectCollection, Copyable<Equipment> {
	private final Map<ItemSlot, EquippableItem> itemsBySlot = new EnumMap<>(ItemSlot.class);

	@Override
	public Equipment copy() {
		Equipment copy = new Equipment();
		copy.itemsBySlot.putAll(itemsBySlot);
		return copy;
	}

	@Override
	public void collectEffects(EffectCollector collector) {
		itemsBySlot.values().forEach(item -> item.collectEffects(collector));
	}

	public EquippableItem getHead() {
		return get(HEAD);
	}

	public EquippableItem getNeck() {
		return get(NECK);
	}

	public EquippableItem getShoulder() {
		return get(SHOULDER);
	}

	public EquippableItem getBack() {
		return get(BACK);
	}

	public EquippableItem getChest() {
		return get(CHEST);
	}

	public EquippableItem getWrist() {
		return get(WRIST);
	}

	public EquippableItem getHands() {
		return get(HANDS);
	}

	public EquippableItem getWaist() {
		return get(WAIST);
	}

	public EquippableItem getLegs() {
		return get(LEGS);
	}

	public EquippableItem getFeet() {
		return get(FEET);
	}

	public EquippableItem getFinger1() {
		return get(FINGER_1);
	}

	public EquippableItem getFinger2() {
		return get(FINGER_2);
	}

	public EquippableItem getTrinket1() {
		return get(TRINKET_1);
	}

	public EquippableItem getTrinket2() {
		return get(TRINKET_2);
	}

	public EquippableItem getMainHand() {
		return get(MAIN_HAND);
	}

	public EquippableItem getOffHand() {
		return get(OFF_HAND);
	}

	public EquippableItem getRanged() {
		return get(RANGED);
	}

	public EquippableItem get(ItemSlot slot) {
		return itemsBySlot.get(slot);
	}

	public void equip(EquippableItem item, ItemSlot slot) {
		if (item != null && !item.canBeEquippedIn(slot)) {
			throw new IllegalArgumentException(item + " can't be equipped into " + slot);
		}

		switch (slot) {
			case MAIN_HAND -> {
				putOrRemove(MAIN_HAND, item);
				EquippableItem mainHand = getMainHand();
				if (mainHand != null && mainHand.getItemType() == ItemType.TWO_HAND) {
					putOrRemove(OFF_HAND, null);
				}
			}
			case OFF_HAND -> {
				EquippableItem mainHand = getMainHand();
				if (mainHand != null && mainHand.getItemType() == ItemType.TWO_HAND && item != null) {
					throw new IllegalArgumentException("Can't equip offhand while having 2-hander");
				}
				putOrRemove(OFF_HAND, item);
			}
			default -> putOrRemove(slot, item);
		}
	}

	private void putOrRemove(ItemSlot slot, EquippableItem item) {
		if (item != null) {
			itemsBySlot.put(slot, item);
		} else {
			itemsBySlot.remove(slot);
		}
	}

	public void equip(EquippableItem item) {
		if (item == null) {
			return;
		}
		equip(item, item.getItemType().getUniqueItemSlot());
	}

	public void equip(ItemSlotGroup slotGroup, EquippableItem[] items) {
		for (int i = 0; i < items.length; i++) {
			var item = items[i];
			var slot = slotGroup.getSlots().get(i);
			equip(item, slot);
		}
	}

	public void unequip(ItemSlotGroup slotGroup) {
		for (var slot : slotGroup.getSlots()) {
			equip(null, slot);
		}
	}

	public void setEquipment(Equipment equipment) {
		for (ItemSlot itemSlot : values()) {
			this.equip(equipment.get(itemSlot), itemSlot);
		}
	}

	public void reset() {
		itemsBySlot.clear();
	}

	public List<EquippableItem> toList() {
		return new ArrayList<>(itemsBySlot.values());
	}

	public Map<ItemSlot, EquippableItem> toMap() {
		return Collections.unmodifiableMap(itemsBySlot);
	}

	private int numRed() {
		return getGemCount(SocketType.RED);
	}

	private int numYellow() {
		return getGemCount(SocketType.YELLOW);
	}

	private int numBlue() {
		return getGemCount(SocketType.BLUE);
	}

	private int getGemCount(SocketType socketType) {
		int result = 0;
		for (EquippableItem equippableItem : itemsBySlot.values()) {
			result += equippableItem.getGemCount(socketType);
		}
		return result;
	}

	public boolean hasMatchingGem(EquippableItem item, int socketNo) {
		return item.hasMatchingGem(socketNo, numRed(), numYellow(), numBlue());
	}

	public boolean allSocketsHaveMatchingGems(EquippableItem item) {
		return item.allSocketsHaveMatchingGems(numRed(), numYellow(), numBlue());
	}

	@Override
	public String toString() {
		return itemsBySlot.values().toString();
	}
}
