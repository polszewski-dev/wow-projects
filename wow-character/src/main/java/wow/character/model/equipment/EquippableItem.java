package wow.character.model.equipment;

import wow.character.model.Copyable;
import wow.commons.model.attributes.AttributeCollection;
import wow.commons.model.attributes.AttributeCollector;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.categorization.ItemType;
import wow.commons.model.item.*;
import wow.commons.model.sources.Source;

import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * User: POlszewski
 * Date: 2021-09-27
 */
public class EquippableItem implements AttributeCollection, Copyable<EquippableItem> {
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
		Integer enchantId = enchant != null ? enchant.getId() : null;
		Integer gem1Id = getSocketCount() >= 1 && getGem(0) != null ? getGem(0).getId() : null;
		Integer gem2Id = getSocketCount() >= 2 && getGem(1) != null ? getGem(1).getId() : null;
		Integer gem3Id = getSocketCount() >= 3 && getGem(2) != null ? getGem(2).getId() : null;
		Integer gem4Id = getSocketCount() >= 4 && getGem(3) != null ? getGem(3).getId() : null;
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

	public int getId() {
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

	public boolean isEnchantable() {
		return item.isEnchantable();
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

	public Attributes getSocketBonus() {
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
		if (!isEnchantable()) {
			throw new IllegalArgumentException("Not enchantable");
		}
		if (!enchant.matches(item.getItemType())) {
			throw new IllegalArgumentException("Enchanting wrong item type");
		}
		this.enchant = enchant;
		return this;
	}

	public EquippableItem gem(Gem... gems) {
		if (gems.length != getSocketCount()) {
			throw new IllegalArgumentException("Wrong #sockets");
		}
		for (int i = 0; i < gems.length; i++) {
			Gem gem = gems[i];
			insertGem(i, gem);
		}
		return this;
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
	public void collectAttributes(AttributeCollector collector) {
		collector.addAttributes(item);
		collector.addAttributes(enchant);
		if (sockets.getSocketCount() > 0) {
			collector.addAttribute(sockets);
		}
		if (item.getItemSet() != null) {
			collector.addAttribute(new ItemSetPiece(item));
		}
	}

	public int getGemCount(SocketType socketType) {
		return sockets.getGemCount(socketType);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof EquippableItem)) return false;
		EquippableItem that = (EquippableItem) o;
		return Objects.equals(item, that.item) &&
				Objects.equals(sockets, that.sockets) &&
				Objects.equals(enchant, that.enchant);
	}

	@Override
	public int hashCode() {
		return Objects.hash(item, sockets, enchant);
	}

	@Override
	public String toString() {
		return item.toString();
	}
}
