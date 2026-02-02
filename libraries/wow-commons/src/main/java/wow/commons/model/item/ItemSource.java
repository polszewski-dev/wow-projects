package wow.commons.model.item;

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
public sealed interface ItemSource {
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
		return this instanceof Traded;
	}

	default boolean isNpcDrop() {
		return this instanceof NpcDrop;
	}

	default boolean isTrashDrop() {
		return (isRaidDrop() || isDungeonDrop()) && npc() == null;
	}

	default boolean isWorldBossDrop() {
		return (!isRaidDrop() && !isDungeonDrop()) && npc() != null && npc().isBoss();
	}

	default boolean isReputationReward() {
		return this instanceof ReputationReward;
	}

	default boolean isCrafted() {
		return this instanceof Crafted;
	}

	default boolean isQuestReward() {
		return this instanceof QuestReward;
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

	default boolean isZoneDrop() { return this instanceof ZoneDrop; }

	default boolean isWorldDrop() {
		return this instanceof WorldDrop;
	}

	default boolean isBadgeVendor() {
		return this instanceof BadgeVendor;
	}

	default boolean isPvP() {
		return this instanceof PvP;
	}

	PvP PVP = new PvP();
	BadgeVendor BADGE_VENDOR = new BadgeVendor();
	WorldDrop WORLD_DROP = new WorldDrop();

	record PvP() implements ItemSource {}

	record BadgeVendor() implements ItemSource {}

	record WorldDrop() implements ItemSource {}

	record ContainedInItem(int id, String name) implements ItemSource {}

	record ContainedInObject(int id, String name, List<Zone> zones) implements ItemSource {}

	record Crafted(ProfessionId professionId) implements ItemSource { }

	record NpcDrop(Npc npc, List<Zone> zones) implements ItemSource {}

	record QuestReward(boolean dungeon, String questName) implements ItemSource {}

	record ReputationReward(Faction faction) implements ItemSource {}

	record Traded(TradedItem sourceItem) implements ItemSource {}

	record ZoneDrop(List<Zone> zones) implements ItemSource {}
}
