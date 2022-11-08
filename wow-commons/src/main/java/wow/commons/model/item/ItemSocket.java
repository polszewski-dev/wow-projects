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

	private final boolean readOnly;

	ItemSocket(SocketType socketType) {
		this(socketType, false);
	}

	private ItemSocket(SocketType socketType, boolean readOnly) {
		this.socketType = socketType;
		this.readOnly = readOnly;
	}

	@Override
	public ItemSocket copy(boolean readOnly) {
		if (this.readOnly && readOnly) {
			return this;
		}

		ItemSocket copy = new ItemSocket(socketType, readOnly);
		copy.gem = gem;
		return copy;
	}

	@Override
	public boolean isReadOnly() {
		return readOnly;
	}

	public boolean isMetaSocket() {
		return socketType == SocketType.META;
	}

	public Gem getGem() {
		return gem;
	}

	public void insertGem(Gem gem) {
		assertCanBeModified();
		if (gem != null && !socketType.accepts(gem.getColor())) {
			throw new IllegalArgumentException(String.format("%s socket does not accept gems of color: %s", socketType, gem.getColor()));
		}
		this.gem = gem;
	}

	public boolean insertedGemMatchesSocketColor() {
		return gemMatchesSocketColor(gem);
	}

	public boolean gemMatchesSocketColor(Gem gem) {
		return gem != null && gem.getColor().matchesSocket(socketType);
	}

	public boolean isCompleteMatch(int numRed, int numYellow, int numBlue) {
		if (!insertedGemMatchesSocketColor()) {
			return false;
		}
		if (!isMetaSocket()) {
			return true;
		}
		return gem.isMetaConditionTrue(numRed, numYellow, numBlue);
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
