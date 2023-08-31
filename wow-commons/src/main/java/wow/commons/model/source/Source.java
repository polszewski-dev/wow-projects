package wow.commons.model.source;

import wow.commons.model.item.TradedItem;
import wow.commons.model.profession.ProfessionId;
import wow.commons.model.pve.Faction;
import wow.commons.model.pve.Npc;
import wow.commons.model.pve.Zone;

import java.util.List;
import java.util.stream.Collectors;

/**
 * User: POlszewski
 * Date: 2021-01-21
 */
public sealed interface Source
		permits BadgeVendor, ContainedInItem, ContainedInObject, Crafted, NpcDrop, PvP, QuestReward, ReputationReward, Traded, WorldDrop, ZoneDrop {

	default List<Zone> zones() {
		return List.of();
	}

	default String zoneShortNames() {
		return zones().stream()
				.map(Zone::getShortName)
				.collect(Collectors.joining(", "));
	}

	default TradedItem sourceItem() {
		return null;
	}

	default Npc npc() {
		return null;
	}

	default Faction faction() {
		return null;
	}

	default String questName() {
		return null;
	}

	default ProfessionId professionId() { return null; }

	default boolean isTraded() {
		return false;
	}

	default boolean isNpcDrop() {
		return false;
	}

	default boolean isTrashDrop() {
		return (isRaidDrop() || isDungeonDrop()) && npc() == null;
	}

	default boolean isWorldBossDrop() {
		return (!isRaidDrop() && !isDungeonDrop()) && npc() != null && npc().isBoss();
	}

	default boolean isReputationReward() {
		return false;
	}

	default boolean isCrafted() {
		return false;
	}

	default boolean isQuestReward() {
		return false;
	}

	default boolean isRaidDrop() {
		return zones().stream().anyMatch(Zone::isRaid);
	}

	default boolean isHeroicDrop() {
		return false;
	}

	default boolean isDungeonDrop() {
		return zones().stream().anyMatch(Zone::isDungeon);
	}

	default boolean isZoneDrop() { return false; }

	default boolean isWorldDrop() {
		return false;
	}

	default boolean isBadgeVendor() {
		return false;
	}

	default boolean isPvP() {
		return false;
	}
}
