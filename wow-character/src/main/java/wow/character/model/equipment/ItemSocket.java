package wow.character.model.equipment;

import lombok.Getter;
import wow.character.model.Copyable;
import wow.commons.model.item.Gem;
import wow.commons.model.item.SocketType;

/**
 * User: POlszewski
 * Date: 2021-12-14
 */
@Getter
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
	public String toString() {
		return gem != null ? "[" + gem.getName() + "]" : "[]";
	}
}
