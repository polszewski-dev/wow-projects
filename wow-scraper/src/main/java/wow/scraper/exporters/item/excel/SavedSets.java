package wow.scraper.exporters.item.excel;

import lombok.AllArgsConstructor;
import lombok.Data;
import wow.commons.model.character.CharacterClassId;
import wow.commons.model.item.ItemSetBonus;
import wow.commons.model.professions.ProfessionId;
import wow.commons.model.pve.GameVersionId;
import wow.scraper.parsers.tooltip.ItemTooltipParser;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Consumer;

/**
 * User: POlszewski
 * Date: 2023-05-19
 */
@AllArgsConstructor
public class SavedSets {
	@Data
	@AllArgsConstructor
	static class SetInfo {
		private String itemSetName;
		private List<String> itemSetPieces;
		private List<ItemSetBonus> itemSetBonuses;
		private GameVersionId gameVersion;
		private List<CharacterClassId> itemSetRequiredClass;
		private ProfessionId itemSetBonusRequiredProfession;
		private Integer itemSetBonusRequiredProfessionLevel;
	}

	private final Map<String, SetInfo> map = new TreeMap<>();

	public void save(ItemTooltipParser parser) {
		if (parser.getItemSetName() == null) {
			return;
		}

		SetInfo setInfo = map.computeIfAbsent(getKey(parser), x -> newSetInfo(parser));

		syncRequiredClass(setInfo, parser);
	}

	public SetInfo get(ItemTooltipParser parser) {
		return map.get(getKey(parser));
	}

	public void forEach(Consumer<SetInfo> consumer) {
		map.values().forEach(consumer);
	}

	private SetInfo newSetInfo(ItemTooltipParser parser) {
		return new SetInfo(
				parser.getItemSetName(),
				parser.getItemSetPieces(),
				parser.getItemSetBonuses(),
				parser.getGameVersion(),
				null,
				parser.getItemSetRequiredProfession(),
				parser.getItemSetRequiredProfessionLevel()
		);
	}

	private String getKey(ItemTooltipParser parser) {
		return parser.getItemSetName() + "#" + parser.getGameVersion().ordinal();
	}

	private void syncRequiredClass(SetInfo setInfo, ItemTooltipParser parser) {
		if (parser.getRequiredClass() != null && !parser.getRequiredClass().isEmpty()) {
			if (setInfo.getItemSetRequiredClass() == null) {
				setInfo.setItemSetRequiredClass(parser.getRequiredClass());
			} else {
				assertTheSameClassRequirements(parser, setInfo);
			}
		}
	}

	private void assertTheSameClassRequirements(ItemTooltipParser parser, SetInfo setInfo) {
		if (!setInfo.getItemSetRequiredClass().equals(parser.getRequiredClass())) {
			throw new IllegalArgumentException("Set pieces have different class requirements: " + setInfo.itemSetName);
		}
	}
}
