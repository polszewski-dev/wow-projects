package wow.commons.model.sources;

import wow.commons.model.item.TradedItem;
import wow.commons.model.professions.ProfessionId;
import wow.commons.model.pve.Boss;
import wow.commons.model.pve.Faction;
import wow.commons.model.pve.Zone;

/**
 * User: POlszewski
 * Date: 2021-01-21
 */
public abstract class Source {
	public Zone getZone() {
		return null;
	}

	public TradedItem getSourceItem() {
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

	public ProfessionId getProfessionId() { return null; }

	public boolean isTraded() {
		return false;
	}

	public boolean isBossDrop() {
		return false;
	}

	public boolean isTrashDrop() {
		return (isRaidDrop() || isDungeonDrop()) && getBoss() == null;
	}

	public boolean isWorldBossDrop() {
		return (!isRaidDrop() && !isDungeonDrop()) && getBoss() != null;
	}

	public boolean isReputationReward() {
		return false;
	}

	public boolean isCrafted() {
		return false;
	}

	public boolean isQuestReward() {
		return false;
	}

	public boolean isRaidDrop() {
		return getZone() != null && getZone().isRaid();
	}

	public boolean isHeroicDrop() {
		return false;
	}

	public boolean isDungeonDrop() {
		return getZone() != null && getZone().isDungeon();
	}

	public boolean isZoneDrop() { return false; }

	public boolean isWorldDrop() {
		return false;
	}

	public boolean isBadgeVendor() {
		return false;
	}

	public boolean isPvP() {
		return false;
	}

	public abstract boolean equals(Object o);

	public abstract int hashCode();

	public abstract String toString();
}
