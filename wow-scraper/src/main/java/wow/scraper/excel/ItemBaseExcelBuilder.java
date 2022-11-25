package wow.scraper.excel;

import lombok.AllArgsConstructor;
import lombok.Data;
import wow.commons.model.categorization.ItemRarity;
import wow.commons.model.item.ItemSetBonus;
import wow.commons.model.professions.Profession;
import wow.commons.model.unit.CharacterClass;
import wow.scraper.model.WowheadItemQuality;
import wow.scraper.parsers.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static wow.commons.repository.impl.parsers.excel.CommonColumnNames.*;
import static wow.commons.repository.impl.parsers.items.ItemBaseExcelColumnNames.*;
import static wow.commons.repository.impl.parsers.items.ItemBaseExcelSheetNames.*;

/**
 * User: POlszewski
 * Date: 2022-10-30
 */
public class ItemBaseExcelBuilder extends AbstractExcelBuilder {
	private static final int MAX_STATS = 10;
	private static final int MAX_BONUSES = 6;
	private static final int MAX_PIECES = 9;

	@Override
	public void start() {
		writer.open();
	}

	public void addTradedItemHeader() {
		writer.nextSheet(TRADE);
		writeTradedItemHeader();
	}

	public void addItemHeader() {
		writer.nextSheet(ITEM);
		writeItemHeader();
	}

	public void addGemHeader() {
		writer.nextSheet(GEM);
		writeGemHeader();
	}

	@Override
	public void finish(String fileName) throws IOException {
		writer.nextSheet(SET);
		writeItemSetHeader();
		savedSets.values().forEach(this::writeItemSetRow);
		super.finish(fileName);
	}

	public void add(TradedItemParser parser) {
		writeTradedItemRow(parser);
	}

	public void add(ItemTooltipParser parser) {
		if (parser.isRandomEnchantment()) {
			return;
		}

		writeItemRow(parser);
		saveSetInfo(parser);
	}

	public void add(GemTooltipParser parser) {
		writeGemRow(parser);
	}

	private void writeTradedItemHeader() {
		writeCommonHeader();
		setHeader(REQ_LEVEL);
		setHeader(REQ_CLASS);
		writeIconAndTooltipHeader();
		writer.nextRow().freeze(2, 1);
	}

	private void writeTradedItemRow(TradedItemParser parser) {
		writeCommonColumns(parser, null);
		setValue(parser.getRequiredLevel());
		setValue(parser.getRequiredClass());
		writeIconAndTooltip(parser);
		writer.nextRow();
	}

	private void writeItemHeader() {
		writeCommonHeader();
		setHeader(REQ_LEVEL);
		setHeader(REQ_CLASS);
		setHeader(REQ_RACE);
		setHeader(REQ_SIDE);
		setHeader(REQ_PROFESSION);
		setHeader(REQ_PROFESSION_LEVEL);
		setHeader(REQ_PROFESSION_SPEC);
		setHeader(ITEM_SOCKET_TYPES);
		setHeader(ITEM_SOCKET_BONUS);
		setHeader(ITEM_ITEM_SET, 30);

		for (int i = 1; i <= MAX_STATS; ++i) {
			setHeader("stat" + i, 20);
		}

		writeIconAndTooltipHeader();
		writer.nextRow().freeze(2, 1);
	}

	private void writeItemRow(ItemTooltipParser parser) {
		writeCommonColumns(parser, parser.getRequiredFactionName());
		setValue(parser.getRequiredLevel());
		setValue(parser.getRequiredClass());
		setValue(parser.getRequiredRace());
		setValue(parser.getRequiredSide());
		setValue(parser.getRequiredProfession());
		setValue(parser.getRequiredProfessionLevel());
		setValue(parser.getRequiredProfessionSpec());
		setValue(parser.getSocketTypes());
		setValue(parser.getSocketBonus());
		setValue(parser.getItemSetName());
		setList(parser.getStatLines(), MAX_STATS);
		writeIconAndTooltip(parser);
		writer.nextRow();
	}

	private void writeItemSetHeader() {
		setHeader(ITEM_SET_NAME, 30);
		setHeader(REQ_CLASS);
		setHeader(REQ_PROFESSION);
		setHeader(REQ_PROFESSION_LEVEL);

		setHeader("#pieces");

		for (int i = 1; i <= MAX_PIECES; ++i) {
			setHeader("item" + i);
		}

		for (int i = 1; i <= MAX_BONUSES; ++i) {
			setHeader("bonus" + i + "_p");
			setHeader("bonus" + i + "_d", 30);
		}

		writer.nextRow().freeze(1, 1);
	}

	private void writeItemSetRow(SetInfo setInfo) {
		setValue(setInfo.getItemSetName());
		setValue(setInfo.getItemSetRequiredClass());
		setValue(setInfo.getItemSetRequiredProfession());
		setValue(setInfo.getItemSetRequiredProfessionLevel());
		setValue(setInfo.getItemSetPieces().size());
		setList(setInfo.getItemSetPieces(), MAX_PIECES);
		setList(setInfo.getItemSetBonuses(), MAX_BONUSES, x -> {
			setValue(x.getNumPieces());
			setValue(x.getDescription());
		}, 2);
		writer.nextRow();
	}

	private void writeGemHeader() {
		writeCommonHeader();
		setHeader(REQ_PROFESSION);
		setHeader(REQ_PROFESSION_LEVEL);
		setHeader(GEM_COLOR);
		setHeader(GEM_STATS, 20);
		setHeader(GEM_META_ENABLERS);
		writeIconAndTooltipHeader();
		writer.nextRow().freeze(2, 1);
	}

	private void writeGemRow(GemTooltipParser parser) {
		writeCommonColumns(parser, null);
		setValue(parser.getRequiredProfession());
		setValue(parser.getRequiredProfessionLevel());
		setValue(parser.getColor());
		setValue(parser.getStatLines().get(0));
		setValue(parser.getMetaEnablers());
		writeIconAndTooltip(parser);
		if (parser.getStatLines().size() != 1) {
			throw new IllegalArgumentException("Only 1 stat line allowed");
		}
		writer.nextRow();
	}

	private void writeCommonHeader() {
		setHeader(ID);
		setHeader(NAME, 30);
		setHeader(ITEM_TYPE);
		setHeader(ITEM_SUBTYPE);
		setHeader(RARITY);
		setHeader(BINDING);
		setHeader(UNIQUE);
		setHeader(ITEM_LEVEL);
		setHeader(SOURCE, 20);
		setHeader(PHASE);
	}

	private void writeCommonColumns(AbstractTooltipParser parser, String requiredFactionName) {
		setValue(parser.getItemId());
		setValue(parser.getName());
		setValue(parser.getItemType());
		setValue(parser.getItemSubType() != null ? parser.getItemSubType().toString() : null);
		setValue(getItemRarity(parser));
		setValue(parser.getBinding());
		setValue(parser.isUnique() ? 1 : 0);
		setValue(parser.getItemLevel());
		setValue(parseSource(requiredFactionName, parser));
		setValue(parser.getPhase());
	}

	private void writeIconAndTooltipHeader() {
		setHeader(ICON);
		setHeader(TOOLTIP);
	}

	private void writeIconAndTooltip(AbstractTooltipParser parser) {
		setValue(parser.getIcon());
		setValue(parser.getTooltip());
	}

	@Data
	@AllArgsConstructor
	private static class SetInfo {
		private String itemSetName;
		private List<String> itemSetPieces;
		private List<ItemSetBonus> itemSetBonuses;
		private List<CharacterClass> itemSetRequiredClass;
		private Profession itemSetRequiredProfession;
		private Integer itemSetRequiredProfessionLevel;
	}

	private final Map<String, SetInfo> savedSets = new TreeMap<>();

	private void saveSetInfo(ItemTooltipParser parser) {
		if (parser.getItemSetName() == null) {
			return;
		}

		SetInfo setInfo = savedSets.computeIfAbsent(parser.getItemSetName(), x -> new SetInfo(
				parser.getItemSetName(),
				parser.getItemSetPieces(),
				parser.getItemSetBonuses(),
				null,
				parser.getItemSetRequiredProfession(),
				parser.getItemSetRequiredProfessionLevel()
		));

		if (parser.getRequiredClass() != null && !parser.getRequiredClass().isEmpty()) {
			if (setInfo.getItemSetRequiredClass() == null) {
				setInfo.setItemSetRequiredClass(parser.getRequiredClass());
			} else {
				assertTheSameClassRequirements(parser, setInfo);
			}
		}
	}

	private static void assertTheSameClassRequirements(ItemTooltipParser parser, SetInfo setInfo) {
		if (!setInfo.getItemSetRequiredClass().equals(parser.getRequiredClass())) {
			throw new IllegalArgumentException("Set pieces have different class requirements: " + setInfo.itemSetName);
		}
	}

	private static ItemRarity getItemRarity(AbstractTooltipParser parser) {
		Integer quality = parser.getItemDetailsAndTooltip().getDetails().getQuality();
		return WowheadItemQuality.fromCode(quality).getItemRarity();
	}

	private static String parseSource(String requiredFactionName, AbstractTooltipParser parser) {
		if (requiredFactionName != null) {
			return "Faction:" + requiredFactionName;
		}
		WowheadSourceParser sourceParser = new WowheadSourceParser(parser.getItemDetailsAndTooltip());
		List<String> sources = sourceParser.getSource();
		return String.join("#", sources);
	}
}
