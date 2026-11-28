package wow.character.model.equipment;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import wow.commons.model.effect.Effect;
import wow.commons.model.item.Gem;
import wow.commons.model.item.SocketType;

/**
 * User: POlszewski
 * Date: 2021-12-14
 */
@Getter
public class ItemSocket {
	private final EquippableItem equippableItem;
	private final int socketNo;
	private final SocketType socketType;
	private Gem gem;

	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.PACKAGE)
	private boolean hasMatch;

	ItemSocket(EquippableItem equippableItem, int socketNo, SocketType socketType) {
		this.equippableItem = equippableItem;
		this.socketNo = socketNo;
		this.socketType = socketType;
	}

	public boolean isMetaSocket() {
		return socketType == SocketType.META;
	}

	public boolean hasGem() {
		return gem != null;
	}

	public void insertGem(Gem gem) {
		if (gem != null && !socketType.accepts(gem.getColor())) {
			throw new IllegalArgumentException(String.format("%s socket does not accept gems of color: %s", socketType, gem.getColor()));
		}

		gemUnequipped(this.gem);
		this.gem = gem;
		gemEquipped();
	}

	private void gemUnequipped(Gem gem) {
		if (gem != null) {
			equippableItem.gemUnequipped(this, gem);
		}
	}

	private void gemEquipped() {
		if (gem != null) {
			equippableItem.gemEquipped(this);
		}
	}

	public boolean matchesSocketColor(Gem gem) {
		return gem == null || gem.matchesSocket(socketType);
	}

	public boolean hasGemMatchingSocketColor() {
		return gem != null && matchesSocketColor(gem);
	}

	public boolean hasMatchingGem(int numRed, int numYellow, int numBlue) {
		return hasGemMatchingSocketColor() &&
				(!isMetaSocket() || gem.isMetaConditionTrue(numRed, numYellow, numBlue));
	}

	boolean allSocketsHaveMatchingGems() {
		return equippableItem.allSocketsHaveMatchingGems();
	}

	Effect getSocketBonus() {
		return equippableItem.getSocketBonus();
	}

	boolean hasMatch() {
		return hasMatch;
	}

	@Override
	public String toString() {
		return gem != null ? "[" + gem.getName() + "]" : "[]";
	}
}
