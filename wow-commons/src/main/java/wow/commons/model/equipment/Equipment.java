package wow.commons.model.equipment;

import wow.commons.model.Copyable;
import wow.commons.model.attributes.AttributeCollection;
import wow.commons.model.attributes.AttributeCollector;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.categorization.ItemType;
import wow.commons.model.item.ItemSocket;
import wow.commons.model.item.SocketType;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * User: POlszewski
 * Date: 2021-09-27
 */
public class Equipment implements Copyable<Equipment>, AttributeCollection {
	private EquippableItem head;
	private EquippableItem neck;
	private EquippableItem shoulder;
	private EquippableItem back;
	private EquippableItem chest;
	private EquippableItem wrist;
	private EquippableItem hands;
	private EquippableItem waist;
	private EquippableItem legs;
	private EquippableItem feet;
	private EquippableItem finger1;
	private EquippableItem finger2;
	private EquippableItem trinket1;
	private EquippableItem trinket2;
	private EquippableItem mainHand;
	private EquippableItem offHand;
	private EquippableItem ranged;

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

		copy.head = Copyable.copyNullable(head, readOnly);
		copy.neck = Copyable.copyNullable(neck, readOnly);
		copy.shoulder = Copyable.copyNullable(shoulder, readOnly);
		copy.back = Copyable.copyNullable(back, readOnly);
		copy.chest = Copyable.copyNullable(chest, readOnly);
		copy.wrist = Copyable.copyNullable(wrist, readOnly);
		copy.hands = Copyable.copyNullable(hands, readOnly);
		copy.waist = Copyable.copyNullable(waist, readOnly);
		copy.legs = Copyable.copyNullable(legs, readOnly);
		copy.feet = Copyable.copyNullable(feet, readOnly);
		copy.finger1 = Copyable.copyNullable(finger1, readOnly);
		copy.finger2 = Copyable.copyNullable(finger2, readOnly);
		copy.trinket1 = Copyable.copyNullable(trinket1, readOnly);
		copy.trinket2 = Copyable.copyNullable(trinket2, readOnly);
		copy.mainHand = Copyable.copyNullable(mainHand, readOnly);
		copy.offHand = Copyable.copyNullable(offHand, readOnly);
		copy.ranged = Copyable.copyNullable(ranged, readOnly);

		return copy;
	}

	@Override
	public boolean isReadOnly() {
		return readOnly;
	}

	public EquippableItem getHead() {
		return head;
	}

	public EquippableItem getNeck() {
		return neck;
	}

	public EquippableItem getShoulder() {
		return shoulder;
	}

	public EquippableItem getBack() {
		return back;
	}

	public EquippableItem getChest() {
		return chest;
	}

	public EquippableItem getWrist() {
		return wrist;
	}

	public EquippableItem getHands() {
		return hands;
	}

	public EquippableItem getWaist() {
		return waist;
	}

	public EquippableItem getLegs() {
		return legs;
	}

	public EquippableItem getFeet() {
		return feet;
	}

	public EquippableItem getFinger1() {
		return finger1;
	}

	public EquippableItem getFinger2() {
		return finger2;
	}

	public EquippableItem getTrinket1() {
		return trinket1;
	}

	public EquippableItem getTrinket2() {
		return trinket2;
	}

	public EquippableItem getMainHand() {
		return mainHand;
	}

	public EquippableItem getOffHand() {
		return offHand;
	}

	public EquippableItem getRanged() {
		return ranged;
	}

	public EquippableItem get(ItemSlot slot) {
		switch (slot) {
			case HEAD: return head;
			case NECK: return neck;
			case SHOULDER: return shoulder;
			case BACK: return back;
			case CHEST: return chest;
			case WRIST: return wrist;
			case HANDS: return hands;
			case WAIST: return waist;
			case LEGS: return legs;
			case FEET: return feet;
			case FINGER_1: return finger1;
			case FINGER_2: return finger2;
			case TRINKET_1: return trinket1;
			case TRINKET_2: return trinket2;
			case MAIN_HAND: return mainHand;
			case OFF_HAND: return offHand;
			case RANGED: return ranged;
			case SHIRT:
			case TABARD:
				return null;
			default:
				throw new IllegalArgumentException("slot: " + slot);
		}
	}

	public void set(EquippableItem item, ItemSlot slot) {
		if (item != null && !item.canBeEquippedIn(slot)) {
			throw new IllegalArgumentException(item + " can't be equipped into " + slot);
		}
		assertCanBeModified();
		switch (slot) {
			case HEAD:
				head = item;
				return;
			case NECK:
				neck = item;
				return;
			case SHOULDER:
				shoulder = item;
				return;
			case BACK:
				back = item;
				return;
			case CHEST:
				chest = item;
				return;
			case WRIST:
				wrist = item;
				return;
			case HANDS:
				hands = item;
				return;
			case WAIST:
				waist = item;
				return;
			case LEGS:
				legs = item;
				return;
			case FEET:
				feet = item;
				return;
			case FINGER_1:
				finger1 = item;
				return;
			case FINGER_2:
				finger2 = item;
				return;
			case TRINKET_1:
				trinket1 = item;
				return;
			case TRINKET_2:
				trinket2 = item;
				return;
			case MAIN_HAND:
				mainHand = item;
				if (mainHand != null && mainHand.getItemType() == ItemType.TWO_HAND) {
					offHand = null;
				}
				return;
			case OFF_HAND:
				if (mainHand != null && mainHand.getItemType() == ItemType.TWO_HAND) {
					throw new IllegalArgumentException("Can't equip offhand while having 2-hander");
				}
				offHand = item;
				return;
			case RANGED:
				ranged = item;
				return;
			case SHIRT:
			case TABARD:
				return;
			default:
				throw new IllegalArgumentException("slot: " + slot);
		}
	}

	public void set(EquippableItem item) {
		if (item == null) {
			return;
		}
		set(item, item.getItemType().getUniqueItemSlot());
	}

	public List<EquippableItem> toList() {
		return getItemStream().collect(Collectors.toList());
	}

	private Stream<EquippableItem> getItemStream() {
		return Stream.of(getItemArray()).filter(Objects::nonNull);
	}

	private EquippableItem[] getItemArray() {
		return new EquippableItem[]{ head, neck, shoulder, back, chest, wrist, hands, waist, legs, feet, finger1, finger2, trinket1, trinket2, mainHand, offHand, ranged};
	}

	public Map<ItemSlot, EquippableItem> toMap() {
		Map<ItemSlot, EquippableItem> result = new EnumMap<>(ItemSlot.class);
		result.put(ItemSlot.HEAD, head);
		result.put(ItemSlot.NECK, neck);
		result.put(ItemSlot.SHOULDER, shoulder);
		result.put(ItemSlot.BACK, back);
		result.put(ItemSlot.CHEST, chest);
		result.put(ItemSlot.WRIST, wrist);
		result.put(ItemSlot.HANDS, hands);
		result.put(ItemSlot.WAIST, waist);
		result.put(ItemSlot.LEGS, legs);
		result.put(ItemSlot.FEET, feet);
		result.put(ItemSlot.FINGER_1, finger1);
		result.put(ItemSlot.FINGER_2, finger2);
		result.put(ItemSlot.TRINKET_1, trinket1);
		result.put(ItemSlot.TRINKET_2, trinket2);
		result.put(ItemSlot.MAIN_HAND, mainHand);
		result.put(ItemSlot.OFF_HAND, offHand);
		result.put(ItemSlot.RANGED, ranged);
		result.entrySet().removeIf(e -> e.getValue() == null);
		return result;
	}

	public boolean allSlotsEquipped() {
		if (
				head == null || neck == null || shoulder == null || back == null || chest == null || wrist == null || hands == null ||
				waist == null || legs == null || feet == null || finger1 == null || finger2 == null || trinket1 == null || trinket2 == null ||
				mainHand == null || ranged == null
		) {
			return false;
		}
		return offHand != null || mainHand.getItemType() == ItemType.TWO_HAND;
	}

	// item stats, gems, socket bonuses, procs, on-use, socket bonuses

	@Override
	public <T extends AttributeCollector> T collectAttributes(T collector) {
		forEach(item -> item.collectAttributes(collector));
		return collector;
	}

	private void forEach(Consumer<EquippableItem> consumer) {
		if (head != null) { consumer.accept(head); }
		if (neck != null) { consumer.accept(neck); }
		if (shoulder != null) { consumer.accept(shoulder); }
		if (back != null) { consumer.accept(back); }
		if (chest != null) { consumer.accept(chest); }
		if (wrist != null) { consumer.accept(wrist); }
		if (hands != null) { consumer.accept(hands); }
		if (waist != null) { consumer.accept(waist); }
		if (legs != null) { consumer.accept(legs); }
		if (feet != null) { consumer.accept(feet); }
		if (finger1 != null) { consumer.accept(finger1); }
		if (finger2 != null) { consumer.accept(finger2); }
		if (trinket1 != null) { consumer.accept(trinket1); }
		if (trinket2 != null) { consumer.accept(trinket2); }
		if (mainHand != null) { consumer.accept(mainHand); }
		if (offHand != null) { consumer.accept(offHand); }
		if (ranged != null) { consumer.accept(ranged); }
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
		if (head != null) { result += head.getGemCount(socketType); }
		if (neck != null) { result += neck.getGemCount(socketType); }
		if (shoulder != null) { result += shoulder.getGemCount(socketType); }
		if (back != null) { result += back.getGemCount(socketType); }
		if (chest != null) { result += chest.getGemCount(socketType); }
		if (wrist != null) { result += wrist.getGemCount(socketType); }
		if (hands != null) { result += hands.getGemCount(socketType); }
		if (waist != null) { result += waist.getGemCount(socketType); }
		if (legs != null) { result += legs.getGemCount(socketType); }
		if (feet != null) { result += feet.getGemCount(socketType); }
		if (finger1 != null) { result += finger1.getGemCount(socketType); }
		if (finger2 != null) { result += finger2.getGemCount(socketType); }
		if (trinket1 != null) { result += trinket1.getGemCount(socketType); }
		if (trinket2 != null) { result += trinket2.getGemCount(socketType); }
		if (mainHand != null) { result += mainHand.getGemCount(socketType); }
		if (offHand != null) { result += offHand.getGemCount(socketType); }
		if (ranged != null) { result += ranged.getGemCount(socketType); }
		return result;
	}

	public boolean isMetaEnabled() {
		return !hasMetaSocket() || allGemsMatch(head);
	}

	private boolean hasMetaSocket() {
		return head.getSockets().stream().anyMatch(ItemSocket::isMetaSocket);
	}

	public boolean isCompleteMatch(EquippableItem item, int socketNo) {
		return item.isCompleteMatch(socketNo, numRed(), numYellow(), numBlue());
	}

	public boolean allGemsMatch(EquippableItem item) {
		return item.allGemsMatch(numRed(), numYellow(), numBlue());
	}

	public List<EquippableItem> getItemDifference(Equipment equipment) {
		List<EquippableItem> result = new ArrayList<>();

		if (!Objects.equals(this.head, equipment.head)) {
			result.add(this.head);
		}
		if (!Objects.equals(this.neck, equipment.neck)) {
			result.add(this.neck);
		}
		if (!Objects.equals(this.shoulder, equipment.shoulder)) {
			result.add(this.shoulder);
		}
		if (!Objects.equals(this.back, equipment.back)) {
			result.add(this.back);
		}
		if (!Objects.equals(this.chest, equipment.chest)) {
			result.add(this.chest);
		}
		if (!Objects.equals(this.wrist, equipment.wrist)) {
			result.add(this.wrist);
		}
		if (!Objects.equals(this.hands, equipment.hands)) {
			result.add(this.hands);
		}
		if (!Objects.equals(this.waist, equipment.waist)) {
			result.add(this.waist);
		}
		if (!Objects.equals(this.legs, equipment.legs)) {
			result.add(this.legs);
		}
		if (!Objects.equals(this.feet, equipment.feet)) {
			result.add(this.feet);
		}
		if (!pairEquals(this.finger1, this.finger2, equipment.finger1, equipment.finger2)) {
			result.add(this.finger1);
			result.add(this.finger2);
		}
		if (!pairEquals(this.trinket1, this.trinket2, equipment.trinket1, equipment.trinket2)) {
			result.add(this.trinket1);
			result.add(this.trinket2);
		}
		if (!pairEquals(this.mainHand, this.offHand, equipment.mainHand, equipment.offHand)) {
			result.add(this.mainHand);
			result.add(this.offHand);
		}
		if (!Objects.equals(this.ranged, equipment.ranged)) {
			result.add(this.ranged);
		}

		return result.stream().filter(Objects::nonNull).collect(Collectors.toList());
	}

	private static boolean pairEquals(EquippableItem pair1El1, EquippableItem pair1El2, EquippableItem pair2El1, EquippableItem pair2El2) {
		return Objects.equals(pair1El1, pair2El1) && Objects.equals(pair1El2, pair2El2) ||
				Objects.equals(pair1El1, pair2El2) && Objects.equals(pair1El2, pair2El1);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Equipment outfit = (Equipment) o;
		return Objects.equals(head, outfit.head) &&
				Objects.equals(neck, outfit.neck) &&
				Objects.equals(shoulder, outfit.shoulder) &&
				Objects.equals(back, outfit.back) &&
				Objects.equals(chest, outfit.chest) &&
				Objects.equals(wrist, outfit.wrist) &&
				Objects.equals(hands, outfit.hands) &&
				Objects.equals(waist, outfit.waist) &&
				Objects.equals(legs, outfit.legs) &&
				Objects.equals(feet, outfit.feet) &&
				pairEquals(finger1, finger2, outfit.finger1, outfit.finger2) &&
				pairEquals(trinket1, trinket2, outfit.trinket1, outfit.trinket2) &&
				Objects.equals(mainHand, outfit.mainHand) &&
				Objects.equals(offHand, outfit.offHand) &&
				Objects.equals(ranged, outfit.ranged);
	}

	@Override
	public int hashCode() {
		var rings = new HashSet<>(2);
		rings.add(finger1);
		rings.add(finger2);
		var trinkets = new HashSet<>(2);
		trinkets.add(trinket1);
		trinkets.add(trinket2);
		return Objects.hash(head, neck, shoulder, back, chest, wrist, hands, waist, legs, feet, rings, trinkets, mainHand, offHand, ranged);
	}

	@Override
	public String toString() {
		return toList().toString();
	}
}
