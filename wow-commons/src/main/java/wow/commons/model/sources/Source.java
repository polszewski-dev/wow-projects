package wow.commons.model.sources;

import wow.commons.model.item.Item;
import wow.commons.model.pve.Boss;
import wow.commons.model.pve.Faction;
import wow.commons.model.pve.Instance;
import wow.commons.model.pve.Phase;

/**
 * User: POlszewski
 * Date: 2021-01-21
 */
public abstract class Source {
	private final Phase phase;

	protected Source(Phase phase) {
		if (phase == null) {
			throw new NullPointerException();
		}
		this.phase = phase;
	}

	public abstract Instance getInstance();

	public Item getSourceToken() {
		return null;
	}

	public Boss getBoss() {
		return null;
	}

	public Faction getFaction() {
		return null;
	}

	public String getQuestName() {
		return null;
	}

	public final Phase getPhase() {
		return phase;
	}

	public boolean isBossDrop() {
		return this instanceof BossDrop;
	}

	public boolean isTradedFromToken() {
		return this instanceof TradedFromToken;
	}

	public boolean isTrashDrop() {
		return this instanceof TrashDrop;
	}

	public boolean isMiscInstance() {
		return this instanceof MiscInstance;
	}

	public boolean isReputationReward() {
		return this instanceof ReputationReward;
	}

	public boolean isPurchasedFromVendor() {
		return this instanceof PurchasedFromVendor;
	}

	public boolean isCrafted() {
		return this instanceof Crafted;
	}

	public boolean isNormalQuestReward() {
		return this instanceof QuestReward && !((QuestReward)this).isDungeon();
	}

	public boolean isDungeonQuestReward() {
		return this instanceof QuestReward && ((QuestReward)this).isDungeon();
	}

	public boolean isRaidDrop() {
		return getInstance() != null && getInstance().isRaid();
	}

	public boolean isDungeonDrop() {
		return getInstance() != null && getInstance().isDungeon();
	}

	public boolean isWorldDrop() {
		return this instanceof WorldDrop;
	}

	public boolean isBadgeVendor() {
		return this instanceof BadgeVendor;
	}

	public boolean isPvp() { return this instanceof PvP; }

	public abstract boolean equals(Object o);

	public abstract int hashCode();

	public abstract String toString();
}
