package wow.commons.repository.impl.parsers.items;

import lombok.AllArgsConstructor;
import wow.commons.model.categorization.ItemType;
import wow.commons.model.config.TimeRestriction;
import wow.commons.model.item.TradedItem;
import wow.commons.model.professions.ProfessionId;
import wow.commons.model.pve.Boss;
import wow.commons.model.pve.Faction;
import wow.commons.model.pve.PhaseId;
import wow.commons.model.pve.Zone;
import wow.commons.model.sources.*;
import wow.commons.repository.ItemRepository;
import wow.commons.repository.PveRepository;
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

	private final PveRepository pveRepository;
	private final ItemRepository itemRepository;

	private final Set<Source> result = new LinkedHashSet<>();

	private final Rule[] rules = {
			Rule.regex("BossDrop:(.*):(\\d+):(\\d+)", this::parseBossDrop),
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
	};

	public Set<Source> parse(String line) {
		if (line == null) {
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

	private void parseBossDrop(ParsedMultipleValues bossDropParams) {
		String bossName = bossDropParams.get(0);//optional
		int bossId = bossDropParams.getInteger(1);
		int zoneId = bossDropParams.getInteger(2);//optional

		if (bossId == 0) {
			throw new IllegalArgumentException();
		}

		Boss boss = pveRepository.getBoss(bossId, phaseId).orElseGet(() -> getMissingBoss(bossName, bossId, zoneId));
		result.add(new BossDrop(boss, boss.getZones().get(0)));
	}

	private Boss getMissingBoss(String bossName, int bossId, int zoneId) {
		if (bossName.isEmpty()) {
			throw new IllegalArgumentException();
		}
		List<Zone> zones = List.of(pveRepository.getZone(zoneId, phaseId).orElseThrow());
		TimeRestriction timeRestriction = TimeRestriction.builder()
				.versions(List.of(phaseId.getGameVersionId()))
				.build();
		return new Boss(bossId, bossName, zones, timeRestriction);
	}

	private void parseZoneDrop(String zoneIdStr) {
		int zoneId = Integer.parseInt(zoneIdStr);
		Zone zone = pveRepository.getZone(zoneId, phaseId).orElseThrow();
		result.add(new ZoneDrop(zone));
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
		return itemRepository.getTradedItem(tokenId, phaseId).orElseThrow();
	}

	private void parseReputationReward(String factionName) {
		Faction faction = pveRepository.getFaction(factionName, phaseId).orElseThrow();
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

	private static void assertNotNull(Object value, String error) {
		if (value == null) {
			throw new IllegalArgumentException(error);
		}
	}
}
