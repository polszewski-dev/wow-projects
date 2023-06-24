package wow.scraper.exporters.item.excel;

import wow.commons.model.attributes.Attributes;
import wow.commons.model.attributes.complex.ComplexAttribute;
import wow.commons.model.attributes.primitive.PrimitiveAttribute;
import wow.commons.model.categorization.ItemRarity;
import wow.commons.model.categorization.ItemSubType;
import wow.commons.model.pve.PhaseId;
import wow.commons.model.pve.Side;
import wow.commons.repository.impl.parsers.excel.mapper.ComplexAttributeMapper;
import wow.scraper.classifiers.PveRoleStatClassifier;
import wow.scraper.exporters.excel.ExcelSheetWriter;
import wow.scraper.model.WowheadItemQuality;
import wow.scraper.parsers.WowheadSourceParser;
import wow.scraper.parsers.tooltip.AbstractItemTooltipParser;
import wow.scraper.parsers.tooltip.AbstractSpellTooltipParser;
import wow.scraper.parsers.tooltip.AbstractTooltipParser;

import java.util.List;

import static wow.commons.repository.impl.parsers.excel.CommonColumnNames.*;
import static wow.commons.repository.impl.parsers.items.ItemBaseExcelColumnNames.*;
import static wow.scraper.util.CommonAssertions.assertBothAreEqual;

/**
 * User: POlszewski
 * Date: 2023-05-18
 */
public abstract class ItemBaseSheetWriter<T> extends ExcelSheetWriter<T, ItemBaseExcelBuilder> {
	protected ItemBaseSheetWriter(ItemBaseExcelBuilder builder) {
		super(builder);
	}

	protected void writeCommonHeader() {
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

	protected void writeCommonColumns(AbstractItemTooltipParser parser) {
		setValue(parser.getItemId());
		setValue(parser.getName());
		setValue(parser.getItemType());
		setValue(ItemSubType.name(parser.getItemSubType()));
		setValue(getItemRarity(parser));
		setValue(parser.getBinding());
		setValue(parser.isUnique() ? 1 : 0);
		setValue(parser.getItemLevel());
		setValue(parseSource(parser));
		setValue(parser.getGameVersion());
		setValue(getPhase(parser));
	}

	protected void writeIconAndTooltipHeader() {
		setHeader(ICON);
		setHeader(TOOLTIP);
	}

	protected void writeIconAndTooltip(AbstractTooltipParser<?> parser) {
		setValue(parser.getIcon());
		setValue("");
	}

	protected void writePveRoles(Attributes attributes) {
		setValue(PveRoleStatClassifier.classify(attributes));
	}

	protected void writeAttributeHeader(String prefix, int maxAttributes) {
		for (int i = 1; i <= maxAttributes; ++i) {
			setHeader(colStat(prefix, i), 20);
			setHeader(colAmount(prefix, i), 5);
		}
	}

	protected void writeAttributes(Attributes attributes, int maxAttributes) {
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

	protected PhaseId getPhase(AbstractItemTooltipParser parser) {
		PhaseId phaseOverride = config.getItemPhaseOverrides().get(parser.getItemId());
		PhaseId phase = parser.getPhase();

		return getPhaseId(phaseOverride, phase);
	}

	protected PhaseId getPhase(AbstractSpellTooltipParser parser) {
		PhaseId phaseOverride = config.getSpellPhaseOverrides().get(parser.getSpellId());
		PhaseId phase = parser.getPhase();

		return getPhaseId(phaseOverride, phase);
	}

	private PhaseId getPhaseId(PhaseId phaseOverride, PhaseId phase) {
		if (phaseOverride != null && phaseOverride.getGameVersionId() == phase.getGameVersionId()) {
			return phaseOverride;
		}
		return phase;
	}

	protected ItemRarity getItemRarity(AbstractTooltipParser<?> parser) {
		Integer quality = parser.getDetails().getQuality();
		return WowheadItemQuality.fromCode(quality).getItemRarity();
	}

	protected Side getRequiredSide(AbstractItemTooltipParser parser) {
		Side side = config.getItemRequiredSideOverride(parser.getItemId())
				.orElse(parser.getRequiredSide());

		if (parser.getRequiredFactionName() == null) {
			return side;
		}

		Side factionSide = config.getRequiredSideFromFaction(parser.getRequiredFactionName());

		if (side != null && factionSide != null) {
			assertBothAreEqual("side", side, factionSide);
			return side;
		}

		if (factionSide == null) {
			return side;
		}

		return factionSide;
	}

	protected String parseSource(AbstractItemTooltipParser parser) {
		WowheadSourceParser sourceParser = new WowheadSourceParser(parser.getDetails(), parser.getRequiredFactionName());
		List<String> sources = sourceParser.getSource();
		return String.join("#", sources);
	}
}
