package wow.commons.model.equipment;

import wow.commons.model.Copyable;
import wow.commons.model.attributes.AttributeCollection;
import wow.commons.model.attributes.AttributeCollector;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.categorization.ItemType;
import wow.commons.model.item.ItemSocket;
import wow.commons.model.item.SocketType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
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
	private EquippableItem hand;
	private EquippableItem waist;
	private EquippableItem legs;
	private EquippableItem feet;
	private EquippableItem ring1;
	private EquippableItem ring2;
	private EquippableItem trinket1;
	private EquippableItem trinket2;
	private EquippableItem main;
	private EquippableItem off;
	private EquippableItem wand;

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
		copy.hand = Copyable.copyNullable(hand, readOnly);
		copy.waist = Copyable.copyNullable(waist, readOnly);
		copy.legs = Copyable.copyNullable(legs, readOnly);
		copy.feet = Copyable.copyNullable(feet, readOnly);
		copy.ring1 = Copyable.copyNullable(ring1, readOnly);
		copy.ring2 = Copyable.copyNullable(ring2, readOnly);
		copy.trinket1 = Copyable.copyNullable(trinket1, readOnly);
		copy.trinket2 = Copyable.copyNullable(trinket2, readOnly);
		copy.main = Copyable.copyNullable(main, readOnly);
		copy.off = Copyable.copyNullable(off, readOnly);
		copy.wand = Copyable.copyNullable(wand, readOnly);

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

	public EquippableItem getHand() {
		return hand;
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

	public EquippableItem getRing1() {
		return ring1;
	}

	public EquippableItem getRing2() {
		return ring2;
	}

	public EquippableItem getTrinket1() {
		return trinket1;
	}

	public EquippableItem getTrinket2() {
		return trinket2;
	}

	public EquippableItem getMain() {
		return main;
	}

	public EquippableItem getOff() {
		return off;
	}

	public EquippableItem getWand() {
		return wand;
	}

	public EquippableItem get(ItemSlot slot) {
		switch (slot) {
			case Head: return head;
			case Neck: return neck;
			case Shoulder: return shoulder;
			case Back: return back;
			case Chest: return chest;
			case Wrist: return wrist;
			case Hands: return hand;
			case Waist: return waist;
			case Legs: return legs;
			case Feet: return feet;
			case Finger1: return ring1;
			case Finger2: return ring2;
			case Trinket1: return trinket1;
			case Trinket2: return trinket2;
			case MainHand: return main;
			case OffHand: return off;
			case Ranged: return wand;
			case Shirt:
			case Tabard:
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
			case Head: head = item; return;
			case Neck: neck = item; return;
			case Shoulder: shoulder = item; return;
			case Back: back = item; return;
			case Chest: chest = item; return;
			case Wrist: wrist = item; return;
			case Hands: hand = item; return;
			case Waist: waist = item; return;
			case Legs: legs = item; return;
			case Feet: feet = item; return;
			case Finger1: ring1 = item; return;
			case Finger2: ring2 = item; return;
			case Trinket1: trinket1 = item; return;
			case Trinket2: trinket2 = item; return;
			case MainHand:
				main = item;
				if (main != null && main.getItemType() == ItemType.TwoHand) {
					off = null;
				}
				return;
			case OffHand:
				if (main != null && main.getItemType() == ItemType.TwoHand) {
					throw new IllegalArgumentException("Can't equip offhand while having 2-hander");
				}
				off = item; return;
			case Ranged: wand = item; return;
			case Shirt:
			case Tabard:
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
		return new EquippableItem[]{ head, neck, shoulder, back, chest, wrist, hand, waist, legs, feet, ring1, ring2, trinket1, trinket2, main, off, wand };
	}

	public boolean allSlotsEquipped() {
		if (
				head == null || neck == null || shoulder == null || back == null || chest == null || wrist == null || hand == null ||
				waist == null || legs == null || feet == null || ring1 == null || ring2 == null || trinket1 == null || trinket2 == null ||
				main == null || wand == null
		) {
			return false;
		}
		return off != null || main.getItemType() == ItemType.TwoHand;
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
		if (hand != null) { consumer.accept(hand); }
		if (waist != null) { consumer.accept(waist); }
		if (legs != null) { consumer.accept(legs); }
		if (feet != null) { consumer.accept(feet); }
		if (ring1 != null) { consumer.accept(ring1); }
		if (ring2 != null) { consumer.accept(ring2); }
		if (trinket1 != null) { consumer.accept(trinket1); }
		if (trinket2 != null) { consumer.accept(trinket2); }
		if (main != null) { consumer.accept(main); }
		if (off != null) { consumer.accept(off); }
		if (wand != null) { consumer.accept(wand); }
	}

	private int numRed() {
		return getGemCount(SocketType.Red);
	}

	private int numYellow() {
		return getGemCount(SocketType.Yellow);
	}

	private int numBlue() {
		return getGemCount(SocketType.Blue);
	}

	private int getGemCount(SocketType socketType) {
		int result = 0;
		if (head != null) { result += head.getGemCount(socketType); }
		if (neck != null) { result += neck.getGemCount(socketType); }
		if (shoulder != null) { result += shoulder.getGemCount(socketType); }
		if (back != null) { result += back.getGemCount(socketType); }
		if (chest != null) { result += chest.getGemCount(socketType); }
		if (wrist != null) { result += wrist.getGemCount(socketType); }
		if (hand != null) { result += hand.getGemCount(socketType); }
		if (waist != null) { result += waist.getGemCount(socketType); }
		if (legs != null) { result += legs.getGemCount(socketType); }
		if (feet != null) { result += feet.getGemCount(socketType); }
		if (ring1 != null) { result += ring1.getGemCount(socketType); }
		if (ring2 != null) { result += ring2.getGemCount(socketType); }
		if (trinket1 != null) { result += trinket1.getGemCount(socketType); }
		if (trinket2 != null) { result += trinket2.getGemCount(socketType); }
		if (main != null) { result += main.getGemCount(socketType); }
		if (off != null) { result += off.getGemCount(socketType); }
		if (wand != null) { result += wand.getGemCount(socketType); }
		return result;
	}

	public boolean isMetaEnabled() {
		return !hasMetaSocket() || allGemsMatch(head);
	}

	private boolean hasMetaSocket() {
		return head.getSockets().stream().anyMatch(ItemSocket::isMetaSocket);
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
		if (!Objects.equals(this.hand, equipment.hand)) {
			result.add(this.hand);
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
		if (!pairEquals(this.ring1, this.ring2, equipment.ring1, equipment.ring2)) {
			result.add(this.ring1);
			result.add(this.ring2);
		}
		if (!pairEquals(this.trinket1, this.trinket2, equipment.trinket1, equipment.trinket2)) {
			result.add(this.trinket1);
			result.add(this.trinket2);
		}
		if (!pairEquals(this.main, this.off, equipment.main, equipment.off)) {
			result.add(this.main);
			result.add(this.off);
		}
		if (!Objects.equals(this.wand, equipment.wand)) {
			result.add(this.wand);
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
				Objects.equals(hand, outfit.hand) &&
				Objects.equals(waist, outfit.waist) &&
				Objects.equals(legs, outfit.legs) &&
				Objects.equals(feet, outfit.feet) &&
				pairEquals(ring1, ring2, outfit.ring1, outfit.ring2) &&
				pairEquals(trinket1, trinket2, outfit.trinket1, outfit.trinket2) &&
				Objects.equals(main, outfit.main) &&
				Objects.equals(off, outfit.off) &&
				Objects.equals(wand, outfit.wand);
	}

	@Override
	public int hashCode() {
		var rings = new HashSet<>(2);
		rings.add(ring1);
		rings.add(ring2);
		var trinkets = new HashSet<>(2);
		trinkets.add(trinket1);
		trinkets.add(trinket2);
		return Objects.hash(head, neck, shoulder, back, chest, wrist, hand, waist, legs, feet, rings, trinkets, main, off, wand);
	}

	@Override
	public String toString() {
		return toList().toString();
	}
}
