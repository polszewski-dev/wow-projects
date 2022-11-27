package wow.commons.model.item;

import wow.commons.model.Copyable;

import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2021-12-14
 */
public class ItemSocket implements Copyable<ItemSocket> {
	private final SocketType socketType;
	private Gem gem;

	public ItemSocket(SocketType socketType) {
		this.socketType = socketType;
	}

	@Override
	public ItemSocket copy() {
		ItemSocket copy = new ItemSocket(socketType);
		copy.gem = gem;
		return copy;
	}

	public boolean isMetaSocket() {
		return socketType == SocketType.META;
	}

	public Gem getGem() {
		return gem;
	}

	public void insertGem(Gem gem) {
		if (gem != null && !socketType.accepts(gem.getColor())) {
			throw new IllegalArgumentException(String.format("%s socket does not accept gems of color: %s", socketType, gem.getColor()));
		}
		this.gem = gem;
	}

	public boolean matchesSocketColor(Gem gem) {
		return gem == null || gem.getColor().matchesSocket(socketType);
	}

	public boolean hasGemMatchingSocketColor() {
		return gem != null && matchesSocketColor(gem);
	}

	public boolean hasMatchingGem(int numRed, int numYellow, int numBlue) {
		return hasGemMatchingSocketColor() &&
				(!isMetaSocket() || gem.isMetaConditionTrue(numRed, numYellow, numBlue));
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof ItemSocket)) return false;
		ItemSocket that = (ItemSocket) o;
		return socketType == that.socketType &&
				Objects.equals(gem, that.gem);
	}

	@Override
	public int hashCode() {
		return Objects.hash(socketType, gem);
	}

	@Override
	public String toString() {
		return gem != null ? "[" + gem.getShorterName() + "]" : "[]";
	}
}
