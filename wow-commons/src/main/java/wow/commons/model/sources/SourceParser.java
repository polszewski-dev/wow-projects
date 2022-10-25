package wow.commons.model.sources;

import wow.commons.model.item.Item;
import wow.commons.model.item.ItemLink;
import wow.commons.model.item.ItemTooltip;
import wow.commons.model.professions.Profession;
import wow.commons.model.pve.Boss;
import wow.commons.model.pve.Faction;
import wow.commons.model.pve.Instance;
import wow.commons.model.pve.Phase;
import wow.commons.repository.PVERepository;

import java.util.function.Function;

/**
 * User: POlszewski
 * Date: 2021-03-13
 */
public final class SourceParser {
	public static Source parse(String line, PVERepository pveRepository) {
		ItemLink tokenLink = ItemLink.tryParse(line);
		if (tokenLink != null) {
			Item dummy = new Item(new ItemTooltip(tokenLink, 0, null, null, null));
			return new TradedFromToken(dummy);
		}

		String[] parts = line.split(":");

		line = parts[0];
		parts[0] = null;

		Phase phase = getArgument(parts, part -> {
			if (part.matches("^(p\\d+)$")) {
				return Phase.valueOf("TBC_" + part.toUpperCase());
			}
			return null;
		});

		Boss boss = pveRepository.getBoss(line);
		if (boss != null) {
			return new BossDrop(boss, phase);
		}

		Instance instance = matchesInstanceName(line, "Trash", pveRepository);
		if (instance != null) {
			return new TrashDrop(instance, phase);
		}

		Instance instance2 = matchesInstanceName(line, "Misc", pveRepository);
		if (instance2 != null) {
			return new MiscInstance(instance2, phase);
		}

		Faction faction = pveRepository.getFaction(line);
		if (faction != null) {
			return new ReputationReward(faction, phase);
		}

		Profession profession = Profession.tryParse(line);
		if (profession != null) {
			return new Crafted(profession, phase);
		}

		if (line.equalsIgnoreCase("Quest")) {
			return new QuestReward(false, phase, getArgument(parts, Function.identity()));
		}

		if (line.equalsIgnoreCase("DungeonQuest")) {
			return new QuestReward(true, phase, getArgument(parts, Function.identity()));
		}

		if (line.equalsIgnoreCase("Vendor")) {
			return new PurchasedFromVendor(phase);
		}

		if (line.equalsIgnoreCase("BadgeVendor")) {
			return new BadgeVendor(phase);
		}

		if (line.equalsIgnoreCase("WorldDrop")) {
			return new WorldDrop(phase != null ? phase : Phase.TBC_P0);
		}

		if (line.equalsIgnoreCase("PvP")) {
			return new PvP(phase);
		}

		throw new IllegalArgumentException("Invalid source: " + line);
	}

	private static <T> T getArgument(String[] parts, Function<String, T> extractor) {
		for (int i = 1; i < parts.length; ++i) {
			if (parts[i] != null) {
				T value = extractor.apply(parts[i]);
				if (value != null) {
					parts[i] = null;
					return value;
				}
			}
		}
		return null;
	}

	private static Instance matchesInstanceName(String line, String sufix, PVERepository pveRepository) {
		return pveRepository.getAllInstances().stream().filter(x -> (x.getName() + sufix).equals(line)).findAny().orElse(null);
	}

	private SourceParser() {}
}
