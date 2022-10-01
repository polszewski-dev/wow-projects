package wow.commons.model.sources;

import wow.commons.model.item.ItemLink;
import wow.commons.model.pve.Boss;
import wow.commons.model.pve.Faction;
import wow.commons.model.pve.Instance;

/**
 * User: POlszewski
 * Date: 2021-01-21
 */
public abstract class Source {
	private final Integer phase;

	protected Source(Integer phase) {
		this.phase = phase;
	}

	public abstract Instance getInstance();

	public ItemLink getTradedFromToken() {
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

	public final int getPhase() {
		if (phase != null) {
			return phase;
		}
		return getDefaultPhase();
	}

	protected int getDefaultPhase() {
		throw new IllegalArgumentException("Can't infer the phase for: " + getClass().getSimpleName());
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
