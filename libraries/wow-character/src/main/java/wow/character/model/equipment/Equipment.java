package wow.character.model.equipment;

import wow.character.model.character.Character;
import wow.character.model.effect.EffectCollection;
import wow.character.model.effect.EffectCollector;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.categorization.ItemSlotGroup;
import wow.commons.model.categorization.ItemType;
import wow.commons.model.item.*;
import wow.commons.model.spell.AbilityId;
import wow.commons.model.spell.ActivatedAbility;

import java.util.*;

import static wow.commons.model.categorization.ItemSlot.*;

/**
 * User: POlszewski
 * Date: 2021-09-27
 */
public class Equipment implements EffectCollection {
	private final Character owner;
	private final Map<ItemSlot, EquippableItem> itemsBySlot = new EnumMap<>(ItemSlot.class);

	private EquippableItem metaSocketItem;
	private int metaSocketNo;
	private Gem metaGem;

	private int numRed;
	private int numYellow;
	private int numBlue;

	private final Map<ItemSet, Integer> itemSetPieces = new LinkedHashMap<>();

	public Equipment(Character owner) {
		this.owner = owner;
	}

	public EquipmentSnapshot getSnapshot() {
		var itemsBySlot = new EnumMap<ItemSlot, EquippableItem>(ItemSlot.class);

		for (var entry : this.itemsBySlot.entrySet()) {
			var slot = entry.getKey();
			var item = entry.getValue();

			itemsBySlot.put(slot, item.copy());
		}

		return new EquipmentSnapshot(itemsBySlot);
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
				var mainHand = getMainHand();
				if (mainHand != null && mainHand.getItemType() == ItemType.TWO_HAND) {
					putOrRemove(OFF_HAND, null);
				}
			}
			case OFF_HAND -> {
				var mainHand = getMainHand();
				if (mainHand != null && mainHand.getItemType() == ItemType.TWO_HAND && item != null) {
					throw new IllegalArgumentException("Can't equip offhand while having 2-hander");
				}
				putOrRemove(OFF_HAND, item);
			}
			default -> putOrRemove(slot, item);
		}
	}

	private void putOrRemove(ItemSlot slot, EquippableItem item) {
		var unequipped = get(slot);

		if (unequipped != null) {
			unequipped.unlinkFromEquipment();
			itemUnequipped(unequipped);
		}

		if (item != null) {
			itemsBySlot.put(slot, item);
			item.linkToEquipment(this);
			itemEquipped(item);
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
		for (var itemSlot : values()) {
			equip(equipment.get(itemSlot), itemSlot);
		}
	}

	public void reset() {
		var equippedSlots = List.copyOf(itemsBySlot.keySet());

		for (var slot : equippedSlots) {
			putOrRemove(slot, null);
		}
	}

	public List<EquippableItem> toList() {
		return List.copyOf(itemsBySlot.values());
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
		for (var equippableItem : itemsBySlot.values()) {
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

	public Optional<ActivatedAbility> getAbility(AbilityId abilityId) {
		for (var item : itemsBySlot.values()) {
			var ability = item.getItem().getActivatedAbility();

			if (ability != null && ability.getAbilityId().equals(abilityId)) {
				return Optional.of(ability);
			}
		}

		return Optional.empty();
	}

	void itemEquipped(EquippableItem equippableItem) {
		owner.itemStatsAdded(equippableItem.getItem());

		var itemSet = equippableItem.getItemSet();

		if (itemSet != null) {
			itemSetPieceEquipped(itemSet);
		}

		var enchant = equippableItem.getEnchant();

		if (enchant != null) {
			enchantEquipped(enchant);
		}

		var sockets = equippableItem.getSockets();

		for (var socket : sockets) {
			var gem = socket.getGem();

			if (gem != null) {
				gemEquipped(equippableItem, socket.getSocketNo(), gem);
			}
		}
	}

	void itemUnequipped(EquippableItem equippableItem) {
		owner.itemStatsRemoved(equippableItem.getItem());

		var itemSet = equippableItem.getItemSet();

		if (itemSet != null) {
			itemSetPieceUnequipped(itemSet);
		}

		var enchant = equippableItem.getEnchant();

		if (enchant != null) {
			enchantUnequipped(enchant);
		}

		var sockets = equippableItem.getSockets();

		for (var socket : sockets) {
			var gem = socket.getGem();

			if (gem != null) {
				gemUnequipped(equippableItem, socket.getSocketNo(), gem);
			}
		}
	}

	private void itemSetPieceEquipped(ItemSet itemSet) {
		int numPiecesAfterEquip = itemSetPieces.getOrDefault(itemSet, 0) + 1;

		itemSetPieces.put(itemSet, numPiecesAfterEquip);

		var equippedBonuses = itemSet.getItemSetBonuses(numPiecesAfterEquip);

		for (var equippedBonus : equippedBonuses) {
			owner.itemSetBonusAdded(equippedBonus);
		}
	}

	private void itemSetPieceUnequipped(ItemSet itemSet) {
		int numPiecesBeforeUnequip = itemSetPieces.get(itemSet);

		itemSetPieces.put(itemSet, numPiecesBeforeUnequip - 1);

		var unequippedBonuses = itemSet.getItemSetBonuses(numPiecesBeforeUnequip);

		for (var unequippedBonus : unequippedBonuses) {
			owner.itemSetBonusRemoved(unequippedBonus);
		}
	}

	void enchantEquipped(Enchant enchant) {
		owner.enchantStatsAdded(enchant);
	}

	void enchantUnequipped(Enchant enchant) {
		owner.enchantStatsRemoved(enchant);
	}

	void gemEquipped(EquippableItem equippableItem, int socketNo, Gem gem) {
		if (gem.getColor() == GemColor.META) {
			metaGemEquipped(equippableItem, socketNo, gem);

		} else {
			coloredGemEquipped(equippableItem, socketNo, gem);
		}
	}

	void gemUnequipped(EquippableItem equippableItem, int socketNo, Gem gem) {
		if (gem.getColor() == GemColor.META) {
			metaGemUnequipped();
		} else {
			coloredGemUnequipped(equippableItem, socketNo, gem);
		}
	}

	private void metaGemEquipped(EquippableItem equippableItem, int socketNo, Gem gem) {
		this.metaSocketItem = equippableItem;
		this.metaSocketNo = socketNo;
		this.metaGem = gem;

		if (isMetaConditionTrue()) {
			enableMetaGem();
		}
	}

	private void metaGemUnequipped() {
		if (isMetaConditionTrue()) {
			disableMetaGem();
		}

		this.metaSocketItem = null;
		this.metaSocketNo = -1;
		this.metaGem = null;
	}

	private void coloredGemEquipped(EquippableItem equippableItem, int socketNo, Gem gem) {
		owner.gemStatsAdded(gem);

		if (equippableItem.equippedGemMatchesSocketColor(socketNo, gem)) {
			equippableItem.setGemMatches(socketNo, true);

			if (equippableItem.allSocketsHaveMatchingGems()) {
				owner.socketBonusAdded(equippableItem.getSocketBonus());
			}
		}

		var metaEnabledBefore = metaGem != null && isMetaConditionTrue();

		if (gem.getColor().matchesSocket(SocketType.RED)) {
			++numRed;
		}

		if (gem.getColor().matchesSocket(SocketType.YELLOW)) {
			++numYellow;
		}

		if (gem.getColor().matchesSocket(SocketType.BLUE)) {
			++numBlue;
		}

		updateMetaGemEnabledStatus(metaEnabledBefore);
	}

	private void coloredGemUnequipped(EquippableItem equippableItem, int socketNo, Gem gem) {
		var metaEnabledBefore = metaGem != null && isMetaConditionTrue();

		removeEffectAndSocketBonus(equippableItem, socketNo, gem);

		if (gem.getColor().matchesSocket(SocketType.RED)) {
			--numRed;
		}

		if (gem.getColor().matchesSocket(SocketType.YELLOW)) {
			--numYellow;
		}

		if (gem.getColor().matchesSocket(SocketType.BLUE)) {
			--numBlue;
		}

		updateMetaGemEnabledStatus(metaEnabledBefore);
	}

	private void updateMetaGemEnabledStatus(boolean metaEnabledBefore) {
		if (metaGem == null) {
			return;
		}

		var metaEnabledAfter = isMetaConditionTrue();

		if (!metaEnabledBefore && metaEnabledAfter) {
			enableMetaGem();
		} else if (metaEnabledBefore && !metaEnabledAfter) {
			disableMetaGem();
		}
	}

	private boolean isMetaConditionTrue() {
		return metaGem.isMetaConditionTrue(numRed, numYellow, numBlue);
	}

	private void enableMetaGem() {
		metaSocketItem.setGemMatches(metaSocketNo, true);

		if (metaSocketItem.allSocketsHaveMatchingGems()) {
			owner.gemStatsAdded(metaGem);
			owner.socketBonusAdded(metaSocketItem.getSocketBonus());
		}
	}

	private void disableMetaGem() {
		removeEffectAndSocketBonus(metaSocketItem, metaSocketNo, metaGem);
	}

	private void removeEffectAndSocketBonus(EquippableItem equippableItem, int socketNo, Gem gem) {
		var allSocketsMatchedBefore = equippableItem.allSocketsHaveMatchingGems();

		equippableItem.setGemMatches(socketNo, false);
		owner.gemStatsRemoved(gem);

		if (allSocketsMatchedBefore) {
			owner.socketBonusRemoved(equippableItem.getSocketBonus());
		}
	}

	@Override
	public String toString() {
		return itemsBySlot.values().toString();
	}
}
