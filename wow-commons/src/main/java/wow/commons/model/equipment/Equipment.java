package wow.commons.model.equipment;

import wow.commons.model.Copyable;
import wow.commons.model.attributes.AttributeCollection;
import wow.commons.model.attributes.AttributeCollector;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.categorization.ItemType;
import wow.commons.model.item.SocketType;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static wow.commons.model.categorization.ItemSlot.*;

/**
 * User: POlszewski
 * Date: 2021-09-27
 */
public class Equipment implements Copyable<Equipment>, AttributeCollection {
	private final Map<ItemSlot, EquippableItem> itemsBySlot = new EnumMap<>(ItemSlot.class);

	private final boolean readOnly;

	public Equipment() {
		this(false);
	}

	private Equipment(boolean readOnly) {
		this.readOnly = readOnly;
	}

	@Override
	public Equipment copy(boolean readOnly) {
		if (this.readOnly && readOnly) {
			return this;
		}

		Equipment copy = new Equipment(readOnly);
		copy.itemsBySlot.putAll(itemsBySlot);
		return copy;
	}

	@Override
	public boolean isReadOnly() {
		return readOnly;
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

	public void set(EquippableItem item, ItemSlot slot) {
		if (item != null && !item.canBeEquippedIn(slot)) {
			throw new IllegalArgumentException(item + " can't be equipped into " + slot);
		}

		assertCanBeModified();

		if (slot == MAIN_HAND) {
			putOrRemove(MAIN_HAND, item);
			EquippableItem mainHand = getMainHand();
			if (mainHand != null && mainHand.getItemType() == ItemType.TWO_HAND) {
				putOrRemove(OFF_HAND, null);
			}
		} else if (slot == OFF_HAND) {
			EquippableItem mainHand = getMainHand();
			if (mainHand != null && mainHand.getItemType() == ItemType.TWO_HAND) {
				throw new IllegalArgumentException("Can't equip offhand while having 2-hander");
			}
			putOrRemove(OFF_HAND, item);
		} else {
			putOrRemove(slot, item);
		}
	}

	private void putOrRemove(ItemSlot slot, EquippableItem item) {
		if (item != null) {
			itemsBySlot.put(slot, item);
		} else {
			itemsBySlot.remove(slot);
		}
	}

	public void set(EquippableItem item) {
		if (item == null) {
			return;
		}
		set(item, item.getItemType().getUniqueItemSlot());
	}

	public List<EquippableItem> toList() {
		return new ArrayList<>(itemsBySlot.values());
	}

	public Map<ItemSlot, EquippableItem> toMap() {
		return Collections.unmodifiableMap(itemsBySlot);
	}

	// item stats, gems, socket bonuses, procs, on-use, socket bonuses

	@Override
	public <T extends AttributeCollector<T>> T collectAttributes(T collector) {
		forEach(item -> item.collectAttributes(collector));
		return collector;
	}

	private void forEach(Consumer<EquippableItem> consumer) {
		itemsBySlot.values().forEach(consumer);
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

	public List<EquippableItem> getItemDifference(Equipment equipment) {
		List<EquippableItem> result = new ArrayList<>();

		for (ItemSlot slot : ItemSlot.values()) {
			findItemDifference(slot, equipment, result);
		}

		return result.stream().filter(Objects::nonNull).collect(Collectors.toList());
	}

	private void findItemDifference(ItemSlot slot, Equipment equipment, List<EquippableItem> result) {
		if (slot == MAIN_HAND) {
			if (!pairEquals(this.getMainHand(), this.getOffHand(), equipment.getMainHand(), equipment.getOffHand())) {
				result.add(this.getMainHand());
				result.add(this.getOffHand());
			}
		} else if (slot == FINGER_1) {
			if (!pairEquals(this.getFinger1(), this.getFinger2(), equipment.getFinger1(), equipment.getFinger2())) {
				result.add(this.getFinger1());
				result.add(this.getFinger2());
			}
		} else if (slot == TRINKET_1) {
			if (!pairEquals(this.getTrinket1(), this.getTrinket2(), equipment.getTrinket1(), equipment.getTrinket2())) {
				result.add(this.getTrinket1());
				result.add(this.getTrinket2());
			}
		} else if (slot != FINGER_2 && slot != TRINKET_2) {
			EquippableItem mine = this.get(slot);
			EquippableItem theirs = equipment.get(slot);

			if (!Objects.equals(mine, theirs)) {
				result.add(mine);
			}
		}
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Equipment that = (Equipment) o;

		for (ItemSlot slot : values()) {
			if (slot != FINGER_1 && slot != FINGER_2 && slot != TRINKET_1 && slot != TRINKET_2 && !Objects.equals(this.get(slot), that.get(slot))) {
				return false;
			}
		}

		return pairEquals(this.getFinger1(), this.getFinger2(), that.getFinger1(), that.getFinger2()) &&
				pairEquals(this.getTrinket1(), this.getTrinket2(), that.getTrinket1(), that.getTrinket2());
	}

	@Override
	public int hashCode() {
		int finger1Hash = getHash(getFinger1());
		int finger2Hash = getHash(getFinger2());
		int trinket1Hash = getHash(getTrinket1());
		int trinket2Hash = getHash(getTrinket2());

		int fingerHash = 31 * Math.min(finger1Hash, finger2Hash) + Math.max(finger1Hash, finger2Hash);
		int trinketHash = 31 * Math.min(trinket1Hash, trinket2Hash) + Math.max(trinket1Hash, trinket2Hash);

		return Objects.hash(getHead(), getNeck(), getShoulder(), getBack(), getChest(), getWrist(), getHands(),
							getWaist(), getLegs(), getFeet(), fingerHash, trinketHash, getMainHand(), getOffHand(), getRanged());
	}

	@Override
	public String toString() {
		return itemsBySlot.values().toString();
	}

	private static boolean pairEquals(EquippableItem pair1A, EquippableItem pair1B, EquippableItem pair2A, EquippableItem pair2B) {
		return Objects.equals(pair1A, pair2A) && Objects.equals(pair1B, pair2B) ||
				Objects.equals(pair1A, pair2B) && Objects.equals(pair1B, pair2A);
	}

	private static int getHash(EquippableItem item) {
		return item != null ? item.hashCode() : 0;
	}
}
