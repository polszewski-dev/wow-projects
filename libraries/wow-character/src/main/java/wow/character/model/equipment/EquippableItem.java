package wow.character.model.equipment;

import wow.character.model.Copyable;
import wow.character.model.effect.EffectCollection;
import wow.character.model.effect.EffectCollector;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.categorization.ItemType;
import wow.commons.model.effect.Effect;
import wow.commons.model.item.*;
import wow.commons.model.source.Source;
import wow.commons.model.spell.ActivatedAbility;

import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

/**
 * User: POlszewski
 * Date: 2021-09-27
 */
public class EquippableItem implements EffectCollection, Copyable<EquippableItem> {
	private final Item item;
	private final ItemSockets sockets;
	private Enchant enchant;

	public EquippableItem(Item item) {
		this(item, ItemSockets.create(item.getSocketSpecification()));
	}

	private EquippableItem(Item item, ItemSockets sockets) {
		this.item = item;
		this.sockets = sockets;
	}

	@Override
	public EquippableItem copy() {
		EquippableItem copy = new EquippableItem(item, sockets.copy());
		copy.enchant = enchant;
		return copy;
	}

	public ItemLink getItemLink() {
		var enchantId = enchant != null ? enchant.getId().value() : null;
		var gem1Id = getSocketCount() >= 1 && getGem(0) != null ? getGem(0).getId().value() : null;
		var gem2Id = getSocketCount() >= 2 && getGem(1) != null ? getGem(1).getId().value() : null;
		var gem3Id = getSocketCount() >= 3 && getGem(2) != null ? getGem(2).getId().value() : null;
		var gem4Id = getSocketCount() >= 4 && getGem(3) != null ? getGem(3).getId().value() : null;

		return item.getItemLink().setEnchantAndGems(enchantId, gem1Id, gem2Id, gem3Id, gem4Id);
	}

	public Item getItem() {
		return item;
	}

	public ItemSockets getSockets() {
		return sockets;
	}

	public Enchant getEnchant() {
		return enchant;
	}

	public ItemId getId() {
		return item.getId();
	}

	public String getName() {
		return item.getName();
	}

	public ItemType getItemType() {
		return item.getItemType();
	}

	public ItemSet getItemSet() {
		return item.getItemSet();
	}

	public Set<Source> getSources() {
		return item.getSources();
	}

	public boolean isUnique() {
		return item.isUnique();
	}

	public boolean isEffectivelyUnique() {
		return item.isEffectivelyUnique();
	}

	public int getSocketCount() {
		return item.getSocketCount();
	}

	public boolean hasSockets() {
		return item.hasSockets();
	}

	public SocketType getSocketType(int i) {
		return sockets.getSocketType(i);
	}

	public Effect getSocketBonus() {
		return sockets.getSocketBonus();
	}

	public boolean canBeEquippedIn(ItemSlot itemSlot) {
		return item.canBeEquippedIn(itemSlot);
	}

	public EquippableItem enchant(Enchant enchant) {
		if (enchant == null) {
			this.enchant = null;
			return this;
		}
		if (!enchant.matches(item.getItemType(), item.getItemSubType())) {
			throw new IllegalArgumentException("Enchanting wrong item type");
		}
		this.enchant = enchant;
		return this;
	}

	public EquippableItem gem(Gem... gems) {
		requireTheSameSize(gems.length);

		for (int i = 0; i < gems.length; i++) {
			var gem = gems[i];
			insertGem(i, gem);
		}

		return this;
	}

	public EquippableItem gem(List<Gem> gems) {
		requireTheSameSize(gems.size());

		for (int i = 0; i < gems.size(); i++) {
			var gem = gems.get(i);
			insertGem(i, gem);
		}

		return this;
	}

	private void requireTheSameSize(int size) {
		if (size != getSocketCount()) {
			throw new IllegalArgumentException("Wrong #sockets");
		}
	}

	public void insertGem(int socketNo, Gem gem) {
		sockets.insertGem(socketNo, gem);
	}

	public Gem getGem(int socketNo) {
		return sockets.getGem(socketNo);
	}

	public List<Gem> getGems() {
		return sockets.getGems();
	}

	public boolean hasMatchingGem(int socketNo, int numRed, int numYellow, int numBlue) {
		ItemSocket socket = sockets.getSocket(socketNo);
		return socket.hasMatchingGem(numRed, numYellow, numBlue);
	}

	public boolean allSocketsHaveMatchingGems(int numRed, int numYellow, int numBlue) {
		return sockets.allSocketsHaveMatchingGems(numRed, numYellow, numBlue);
	}

	public boolean hasGemMatchingSocketColor(int socketNo) {
		ItemSocket socket = sockets.getSocket(socketNo);
		return socket.hasGemMatchingSocketColor();
	}

	@Override
	public void collectEffects(EffectCollector collector) {
		collector.addEffects(item.getEffects());
		if (enchant != null) {
			collector.addEffect(enchant.getEffect());
		}
		if (sockets.getSocketCount() > 0) {
			collector.addItemSockets(sockets);
		}
		if (item.getItemSet() != null) {
			collector.addItemSet(item.getItemSet());
		}
		if (item.getActivatedAbility() != null) {
			collector.addActivatedAbility(item.getActivatedAbility());
		}
	}

	public int getGemCount(SocketType socketType) {
		return sockets.getGemCount(socketType);
	}

	public boolean hasActivatedAbility() {
		return item.getActivatedAbility() != null;
	}

	public ActivatedAbility getActivatedAbility() {
		return item.getActivatedAbility();
	}

	public WeaponStats getWeaponStats() {
		return item.getWeaponStats();
	}

	public boolean isTheSameAs(EquippableItem other) {
		if (other == null) {
			return false;
		}

		return item == other.item
				&& enchant == other.enchant
				&& IntStream.range(0, sockets.getSocketCount())
						.allMatch(socketNo -> sockets.getGem(socketNo) == other.sockets.getGem(socketNo));
	}

	@Override
	public String toString() {
		return item.toString();
	}
}
