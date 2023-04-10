package wow.scraper.excel;

import lombok.AllArgsConstructor;
import lombok.Data;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.attributes.complex.ComplexAttribute;
import wow.commons.model.attributes.primitive.PrimitiveAttribute;
import wow.commons.model.categorization.ItemRarity;
import wow.commons.model.character.CharacterClassId;
import wow.commons.model.item.ItemSetBonus;
import wow.commons.model.professions.ProfessionId;
import wow.commons.model.pve.GameVersionId;
import wow.commons.model.pve.PhaseId;
import wow.commons.model.pve.Side;
import wow.commons.repository.impl.parsers.excel.mapper.ComplexAttributeMapper;
import wow.scraper.classifiers.PveRoleStatClassifier;
import wow.scraper.config.ScraperConfig;
import wow.scraper.model.WowheadItemQuality;
import wow.scraper.parsers.WowheadSourceParser;
import wow.scraper.parsers.tooltip.AbstractTooltipParser;
import wow.scraper.parsers.tooltip.GemTooltipParser;
import wow.scraper.parsers.tooltip.ItemTooltipParser;
import wow.scraper.parsers.tooltip.TradedItemParser;

import java.io.IOException;
import java.util.ArrayList;
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
@AllArgsConstructor
public class ItemBaseExcelBuilder extends AbstractExcelBuilder {
	private final ScraperConfig config;

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
		flushItems();
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
		if (parser.isRandomEnchantment() || isToBeIgnored(parser.getItemId())) {
			return;
		}
		itemParserQueue.add(parser);
		saveSetInfo(parser);
	}

	public void add(GemTooltipParser parser) {
		if (isToBeIgnored(parser.getItemId())) {
			return;
		}
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
		if (isToBeIgnored(parser.getItemId())) {
			return;
		}
		writeCommonColumns(parser);
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
		setHeader(PVE_ROLES);
		setHeader(ITEM_SOCKET_TYPES);
		writeAttributeHeader(SOCKET_BONUS_PREFIX, SOCKET_BONUS_MAX_STATS);
		setHeader(ITEM_ITEM_SET, 30);
		writeAttributeHeader("", ITEM_MAX_STATS);
		writeIconAndTooltipHeader();
		writer.nextRow().freeze(2, 1);
	}

	private void writeItemRow(ItemTooltipParser parser) {
		writeCommonColumns(parser);
		setValue(parser.getRequiredLevel());
		setValue(getRequiredClass(parser));
		setValue(parser.getRequiredRace());
		setValue(getRequiredSide(parser));
		setValue(parser.getRequiredProfession());
		setValue(parser.getRequiredProfessionLevel());
		setValue(parser.getRequiredProfessionSpec());
		writePveRoles(parser.getStatParser().getParsedStats());
		setValue(parser.getSocketTypes());
		writeAttributes(parser.getSocketBonus(), SOCKET_BONUS_MAX_STATS);
		setValue(parser.getItemSetName());
		writeAttributes(parser.getStatParser().getParsedStats(), ITEM_MAX_STATS);
		writeIconAndTooltip(parser);
		writer.nextRow();
	}

	private Side getRequiredSide(AbstractTooltipParser parser) {
		return config.getRequiredSideOverride(parser.getItemId())
				.orElse(parser.getRequiredSide());
	}

	private List<CharacterClassId> getRequiredClass(ItemTooltipParser parser) {
		if (parser.getItemSetName() != null) {
			SetInfo setInfo = savedSets.get(getSavedSetKey(parser));
			return setInfo.getItemSetRequiredClass();
		}
		return parser.getRequiredClass();
	}

	private void writeItemSetHeader() {
		setHeader(ITEM_SET_NAME, 30);
		setHeader(REQ_VERSION);
		setHeader(REQ_CLASS);
		setHeader(ITEM_SET_BONUS_REQ_PROFESSION);
		setHeader(ITEM_SET_BONUS_REQ_PROFESSION_LEVEL);

		for (int bonusIdx = 1; bonusIdx <= ITEM_SET_MAX_BONUSES; ++bonusIdx) {
			setHeader(itemSetBonusNumPieces(bonusIdx));
			setHeader(itemSetBonusDescription(bonusIdx));
			writeAttributeHeader(itemSetBonusStatPrefix(bonusIdx), ITEM_SET_BONUS_MAX_STATS);
		}

		writer.nextRow().freeze(1, 1);
	}

	private void writeItemSetRow(SetInfo setInfo) {
		setValue(setInfo.getItemSetName());
		setValue(setInfo.getGameVersion());
		setValue(setInfo.getItemSetRequiredClass());
		setValue(setInfo.getItemSetBonusRequiredProfession());
		setValue(setInfo.getItemSetBonusRequiredProfessionLevel());
		setList(setInfo.getItemSetBonuses(), ITEM_SET_MAX_BONUSES, x -> {
			setValue(x.getNumPieces());
			setValue(x.getDescription());
			writeAttributes(x.getBonusStats(), ITEM_SET_BONUS_MAX_STATS);
		}, 2 + 2 * ITEM_SET_BONUS_MAX_STATS);
		writer.nextRow();
	}

	private void writeGemHeader() {
		writeCommonHeader();
		setHeader(REQ_PROFESSION);
		setHeader(REQ_PROFESSION_LEVEL);
		setHeader(REQ_SIDE);
		setHeader(PVE_ROLES);
		setHeader(GEM_COLOR);
		writeAttributeHeader("", GEM_MAX_STATS);
		setHeader(GEM_META_ENABLERS, 20);
		writeIconAndTooltipHeader();
		writer.nextRow().freeze(2, 1);
	}

	private void writeGemRow(GemTooltipParser parser) {
		writeCommonColumns(parser);
		setValue(parser.getRequiredProfession());
		setValue(parser.getRequiredProfessionLevel());
		setValue(getRequiredSide(parser));
		writePveRoles(parser.getStats());
		setValue(parser.getColor());
		writeAttributes(parser.getStats(), GEM_MAX_STATS);
		setValue(parser.getMetaEnablers());
		writeIconAndTooltip(parser);
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
		setHeader(REQ_VERSION);
		setHeader(REQ_PHASE);
	}

	private void writeCommonColumns(AbstractTooltipParser parser) {
		setValue(parser.getItemId());
		setValue(parser.getName());
		setValue(parser.getItemType());
		setValue(parser.getItemSubType() != null ? parser.getItemSubType().toString() : null);
		setValue(getItemRarity(parser));
		setValue(parser.getBinding());
		setValue(parser.isUnique() ? 1 : 0);
		setValue(parser.getItemLevel());
		setValue(parseSource(parser));
		setValue(parser.getGameVersion());
		setValue(getPhase(parser));
	}

	private boolean isToBeIgnored(int itemId) {
		return config.getIgnoredItemIds().contains(itemId);
	}

	private PhaseId getPhase(AbstractTooltipParser parser) {
		PhaseId phaseOverride = config.getPhaseOverrides().get(parser.getItemId());
		PhaseId phase = parser.getPhase();

		if (phaseOverride != null && phaseOverride.getGameVersionId() == phase.getGameVersionId()) {
			return phaseOverride;
		}
		return phase;
	}

	private void writeAttributeHeader(String prefix, int maxAttributes) {
		for (int i = 1; i <= maxAttributes; ++i) {
			setHeader(colStat(prefix, i), 20);
			setHeader(colAmount(prefix, i), 5);
		}
	}

	private void writeAttributes(Attributes attributes, int maxAttributes) {
		if (attributes == null) {
			fillRemainingEmptyCols(2 * maxAttributes);
			return;
		}

		int colNo = 0;

		for (PrimitiveAttribute attribute : attributes.getPrimitiveAttributes()) {
			setValue(ComplexAttributeMapper.getIdAndCondition(attribute));
			setValue(attribute.getDouble());
			++colNo;
		}

		for (ComplexAttribute attribute : attributes.getComplexAttributes()) {
			setValue(ComplexAttributeMapper.toString(attribute));
			setValue((String)null);
			++colNo;
		}

		if (colNo > maxAttributes) {
			throw new IllegalArgumentException("Can't write more attributes than " + maxAttributes);
		}

		fillRemainingEmptyCols(2 * (maxAttributes - colNo));
	}

	private void writePveRoles(Attributes attributes) {
		setValue(PveRoleStatClassifier.classify(attributes));
	}

	private void writeIconAndTooltipHeader() {
		setHeader(ICON);
		setHeader(TOOLTIP);
	}

	private void writeIconAndTooltip(AbstractTooltipParser parser) {
		setValue(parser.getIcon());
		setValue("");
	}

	private final List<ItemTooltipParser> itemParserQueue = new ArrayList<>();

	private void flushItems() {
		for (ItemTooltipParser parser : itemParserQueue) {
			writeItemRow(parser);
		}
		itemParserQueue.clear();
	}

	@Data
	@AllArgsConstructor
	private static class SetInfo {
		private String itemSetName;
		private List<String> itemSetPieces;
		private List<ItemSetBonus> itemSetBonuses;
		private GameVersionId gameVersion;
		private List<CharacterClassId> itemSetRequiredClass;
		private ProfessionId itemSetBonusRequiredProfession;
		private Integer itemSetBonusRequiredProfessionLevel;
	}

	private final Map<String, SetInfo> savedSets = new TreeMap<>();

	private void saveSetInfo(ItemTooltipParser parser) {
		if (parser.getItemSetName() == null) {
			return;
		}

		SetInfo setInfo = savedSets.computeIfAbsent(getSavedSetKey(parser), x -> new SetInfo(
				parser.getItemSetName(),
				parser.getItemSetPieces(),
				parser.getItemSetBonuses(),
				parser.getGameVersion(),
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

	private static String getSavedSetKey(ItemTooltipParser parser) {
		return parser.getItemSetName() + "#" + parser.getGameVersion().ordinal();
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

	private static String parseSource(AbstractTooltipParser parser) {
		WowheadSourceParser sourceParser = new WowheadSourceParser(parser.getItemDetailsAndTooltip(), parser.getRequiredFactionName());
		List<String> sources = sourceParser.getSource();
		return String.join("#", sources);
	}
}
