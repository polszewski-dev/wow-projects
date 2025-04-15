package wow.commons.repository.impl.parser.item;

import lombok.AllArgsConstructor;
import wow.commons.model.categorization.ItemType;
import wow.commons.model.item.TradedItem;
import wow.commons.model.profession.ProfessionId;
import wow.commons.model.pve.Faction;
import wow.commons.model.pve.Npc;
import wow.commons.model.pve.PhaseId;
import wow.commons.model.pve.Zone;
import wow.commons.model.source.*;
import wow.commons.repository.item.TradedItemRepository;
import wow.commons.repository.pve.FactionRepository;
import wow.commons.repository.pve.NpcRepository;
import wow.commons.repository.pve.ZoneRepository;
import wow.commons.util.parser.ParsedMultipleValues;
import wow.commons.util.parser.Rule;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * User: POlszewski
 * Date: 2021-03-13
 */
@AllArgsConstructor
public class SourceParser {
	private final PhaseId phaseId;

	private final ZoneRepository zoneRepository;
	private final NpcRepository npcRepository;
	private final FactionRepository factionRepository;
	private final TradedItemRepository tradedItemRepository;

	private final Set<Source> result = new LinkedHashSet<>();

	private final Rule[] rules = {
			Rule.regex("NpcDrop:(.*):(\\d+)", this::parseNpcDrop),
			Rule.prefix("ZoneDrop:", this::parseZoneDrop),
			Rule.prefix("Token:", this::parseToken),
			Rule.prefix("ItemStartingQuest:", this::parseItemStartingQuest),
			Rule.prefix("Faction:", this::parseReputationReward),
			Rule.prefix("Crafted:", this::parseCrafted),
			Rule.prefix("Quest:", this::parseQuest),
			Rule.exact("Quests", this::parseQuests),
			Rule.exact("Badges", this::parseBadges),
			Rule.exact("PvP", this::parsePvP),
			Rule.exact("WorldDrop", this::parseWorldDrop),
			Rule.regex("ContainerObject:(.*):(\\d+)", this::parseContainerObjectUnknownZone),
			Rule.regex("ContainerObject:(.*):(\\d+):(\\d+)", this::parseContainerObject),
			Rule.regex("ContainerItem:(.*):(\\d+)", this::parseContainerItem),
	};

	public Set<Source> parse(String line) {
		if (line == null || line.equals("NONE")) {
			return Set.of();
		}

		for (String part : line.split("#")) {
			parseSingleSource(part);
		}

		return result;
	}

	private void parseSingleSource(String value) {
		for (Rule rule : rules) {
			if (rule.matchAndTakeAction(value)) {
				return;
			}
		}
		throw new IllegalArgumentException("Invalid source: " + value);
	}

	private void parseNpcDrop(ParsedMultipleValues npcDropParams) {
		int npcId = npcDropParams.getInteger(1);
		Npc npc = npcRepository.getNpc(npcId, phaseId).orElseThrow();
		result.add(new NpcDrop(npc, npc.getZones()));
	}

	private void parseZoneDrop(String zoneIdStr) {
		int zoneId = Integer.parseInt(zoneIdStr);
		Zone zone = zoneRepository.getZone(zoneId, phaseId).orElseThrow();
		result.add(new ZoneDrop(List.of(zone)));
	}

	private void parseToken(String tokenIdStr) {
		int tokenId = Integer.parseInt(tokenIdStr);
		TradedItem token = getTradedItem(tokenId);
		if (token.getItemType() != ItemType.TOKEN) {
			throw new IllegalArgumentException("Expected token: " + tokenId);
		}
		result.add(new Traded(token));
	}

	private void parseItemStartingQuest(String itemStartingQuestIdStr) {
		int itemStartingQuestId = Integer.parseInt(itemStartingQuestIdStr);
		TradedItem itemStartingQuest = getTradedItem(itemStartingQuestId);
		if (itemStartingQuest.getItemType() != ItemType.QUEST) {
			throw new IllegalArgumentException("Expected item starting quest: " + itemStartingQuestId);
		}
		result.add(new Traded(itemStartingQuest));
	}

	private TradedItem getTradedItem(int tokenId) {
		return tradedItemRepository.getTradedItem(tokenId, phaseId).orElseThrow();
	}

	private void parseReputationReward(String factionName) {
		Faction faction = factionRepository.getFaction(factionName, phaseId).orElseThrow();
		result.add(new ReputationReward(faction));
	}

	private void parseCrafted(String professionName) {
		ProfessionId professionId = ProfessionId.parse(professionName);
		assertNotNull(professionId, professionName);
		result.add(new Crafted(professionId));
	}

	private void parseQuest(String questName) {
		result.add(new QuestReward(false, questName));
	}

	private void parseQuests() {
		result.add(new QuestReward(false, null));
	}

	private void parseBadges() {
		result.add(new BadgeVendor());
	}

	private void parsePvP() {
		result.add(new PvP());
	}

	private void parseWorldDrop() {
		result.add(new WorldDrop());
	}

	private void parseContainerObjectUnknownZone(ParsedMultipleValues params) {
		String containerName = params.get(0);
		int containerId = params.getInteger(1);
		result.add(new ContainedInObject(containerId, containerName, List.of()));
	}

	private void parseContainerObject(ParsedMultipleValues params) {
		String containerName = params.get(0);
		int containerId = params.getInteger(1);
		int zoneId = params.getInteger(2);
		Zone zone = zoneRepository.getZone(zoneId, phaseId).orElseThrow();
		result.add(new ContainedInObject(containerId, containerName, List.of(zone)));
	}

	private void parseContainerItem(ParsedMultipleValues params) {
		result.add(new ContainedInItem(params.getInteger(1), params.get(0)));
	}

	private static void assertNotNull(Object value, String error) {
		if (value == null) {
			throw new IllegalArgumentException(error);
		}
	}
}
