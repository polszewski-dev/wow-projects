package wow.commons.model.sources;

import wow.commons.model.professions.Profession;
import wow.commons.model.pve.Faction;
import wow.commons.model.pve.Zone;
import wow.commons.repository.PVERepository;
import wow.commons.util.ParserUtil;

/**
 * User: POlszewski
 * Date: 2021-03-13
 */
public final class SourceParser {
	public static Source parse(String line, PVERepository pveRepository) {
		if (line == null) {
			return null;
		}

		Object[] bossDropParams = ParserUtil.parseMultipleValues("BossDrop:(.*):(.*)", line);
		if (bossDropParams != null) {
			String bossName = (String) bossDropParams[0];
			int zoneId = (int) bossDropParams[1];
			Zone zone = pveRepository.getZone(zoneId).orElseThrow();
			return new BossDrop(zone, bossName);
		}

		Object[] zoneDropParams = ParserUtil.parseMultipleValues("ZoneDrop:(.*)", line);
		if (zoneDropParams != null) {
			int zoneId = (int) zoneDropParams[0];
			Zone zone = pveRepository.getZone(zoneId).orElseThrow();
			return new TrashDrop(zone);
		}

		String factionName = ParserUtil.removePrefix("Faction:", line);
		if (factionName != null) {
			Faction faction = pveRepository.getFaction(factionName).orElseThrow();
			assertNotNull(faction, factionName);
			return new ReputationReward(faction);
		}

		String professionName = ParserUtil.removePrefix("Crafted:", line);
		if (professionName != null) {
			Profession profession = Profession.parse(professionName);
			assertNotNull(profession, professionName);
			return new Crafted(profession);
		}

		String questName = ParserUtil.removePrefix("Quest:", line);
		if (questName != null) {
			return new QuestReward(false, questName);
		}

		if (line.equals("Badges")) {
			return new BadgeVendor();
		}

		if (line.equals("WorldDrop")) {
			return new WorldDrop();
		}

		if (line.equals("PvP")) {
			return new PvP();
		}
		//TODO token
		throw new IllegalArgumentException("Invalid source: " + line);
	}

	private static void assertNotNull(Object value, String error) {
		if (value == null) {
			throw new IllegalArgumentException(error);
		}
	}

	private SourceParser() {}
}
