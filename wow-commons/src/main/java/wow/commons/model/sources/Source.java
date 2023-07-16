package wow.commons.model.sources;

import wow.commons.model.item.TradedItem;
import wow.commons.model.professions.ProfessionId;
import wow.commons.model.pve.Faction;
import wow.commons.model.pve.Npc;
import wow.commons.model.pve.Zone;

import java.util.List;
import java.util.stream.Collectors;

/**
 * User: POlszewski
 * Date: 2021-01-21
 */
public abstract class Source {
	public List<Zone> getZones() {
		return List.of();
	}

	public String getZoneShortNames() {
		return getZones().stream()
				.map(Zone::getShortName)
				.collect(Collectors.joining(", "));
	}

	public TradedItem getSourceItem() {
		return null;
	}

	public Npc getNpc() {
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

	public boolean isNpcDrop() {
		return false;
	}

	public boolean isTrashDrop() {
		return (isRaidDrop() || isDungeonDrop()) && getNpc() == null;
	}

	public boolean isWorldBossDrop() {
		return (!isRaidDrop() && !isDungeonDrop()) && getNpc() != null && getNpc().isBoss();
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
		return getZones().stream().anyMatch(Zone::isRaid);
	}

	public boolean isHeroicDrop() {
		return false;
	}

	public boolean isDungeonDrop() {
		return getZones().stream().anyMatch(Zone::isDungeon);
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
