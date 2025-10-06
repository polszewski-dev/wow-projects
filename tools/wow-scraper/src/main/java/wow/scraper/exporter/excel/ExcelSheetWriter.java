package wow.scraper.exporter.excel;

import wow.commons.model.attribute.AttributeCondition;
import wow.commons.model.attribute.Attributes;
import wow.commons.model.config.Described;
import wow.commons.model.config.TimeRestriction;
import wow.commons.model.effect.Effect;
import wow.commons.model.effect.component.AbsorptionCondition;
import wow.commons.model.effect.component.StatConversionCondition;
import wow.commons.model.spell.AbilityId;
import wow.commons.repository.impl.parser.excel.mapper.ItemEffectMapper;
import wow.commons.util.AttributesFormater;
import wow.commons.util.condition.AbsorptionConditionFormatter;
import wow.commons.util.condition.AttributeConditionFormatter;
import wow.commons.util.condition.StatConversionConditionFormatter;
import wow.scraper.config.ScraperConfig;
import wow.scraper.config.ScraperDatafixes;

import java.util.List;

import static wow.commons.repository.impl.parser.excel.CommonColumnNames.*;

/**
 * User: POlszewski
 * Date: 2023-06-20
 */
public abstract class ExcelSheetWriter<T, B extends WowExcelBuilder> extends ExcelCellWriter {
	protected final ScraperConfig config;
	protected final ScraperDatafixes datafixes;
	private final ItemEffectMapper itemEffectMapper;

	protected ExcelSheetWriter(B builder) {
		super(builder.getWriter());
		this.config = builder.getConfig();
		this.datafixes = builder.getDatafixes();
		this.itemEffectMapper = new ItemEffectMapper(null);
	}

	public abstract void writeHeader();

	public abstract void writeRow(T params);

	protected void writeTimeRestrictionHeader() {
		setHeader(REQ_VERSION);
		setHeader(REQ_PHASE);
	}

	protected void writeTimeRestriction(TimeRestriction timeRestriction) {
		setValue(timeRestriction.getGameVersionId());

		var phase = timeRestriction.earliestPhaseId();

		if (phase == timeRestriction.getGameVersionId().getEarliestPhase()) {
			phase = null;
		}

		setValue(phase);
	}

	protected void writeAttributeHeader(String prefix, int maxAttributes) {
		for (int i = 1; i <= maxAttributes; ++i) {
			setHeader(getAttrValue(i), prefix);
			setHeader(getAttrId(i), prefix);
		}
	}

	protected void writeAttributes(Attributes attributes, int maxAttributes) {
		final int colsPerAttribute = 2;

		if (attributes == null) {
			fillRemainingEmptyCols(colsPerAttribute * maxAttributes);
			return;
		}

		int attrNo = 0;

		for (var attribute : attributes.list()) {
			setValue(attribute.value());
			setValue(AttributesFormater.formatWithoutValue(attribute));
			++attrNo;
		}

		if (attrNo > maxAttributes) {
			throw new IllegalArgumentException("Can't write more attributes than " + maxAttributes);
		}

		fillRemainingEmptyCols(colsPerAttribute * (maxAttributes - attrNo));
	}

	protected void writeEffectHeader(String prefix, int maxEffects) {
		for (int i = 1; i <= maxEffects; ++i) {
			setHeader(colEffectStats(prefix, i), 10);
			setHeader(colEffectDescr(prefix, i), 20);
		}
	}

	protected void writeEffect(Effect effect) {
		writeEffects(effect != null ? List.of(effect) : List.of(), 1);
	}

	protected void writeEffects(List<Effect> effects, int maxEffects) {
		if (effects == null) {
			fillRemainingEmptyCols(2 * maxEffects);
			return;
		}

		int colNo = 0;

		for (var effect : effects) {
			setValue(itemEffectMapper.toString(effect));
			setValue(effect.getTooltip());
			++colNo;
		}

		if (colNo > maxEffects) {
			throw new IllegalArgumentException("Can't write more effects than " + maxEffects);
		}

		fillRemainingEmptyCols(2 * (maxEffects - colNo));
	}

	protected void setValue(AttributeCondition condition) {
		setValue(AttributeConditionFormatter.formatCondition(condition));
	}

	protected void setValue(AbsorptionCondition condition) {
		setValue(AbsorptionConditionFormatter.formatCondition(condition));
	}

	protected void setValue(StatConversionCondition condition) {
		setValue(StatConversionConditionFormatter.formatCondition(condition));
	}

	protected void setValue(AbilityId abilityId) {
		if (abilityId != null) {
			setValue(abilityId.name());
		} else {
			setValue((String) null);
		}
	}

	protected void writeIconAndTooltipHeader() {
		setHeader(TOOLTIP, 120);
		setHeader(ICON);
	}

	protected void writeIconAndTooltip(Described described) {
		writeIconAndTooltip(described.getIcon(), described.getTooltip());
	}

	protected void writeIconAndTooltip(String icon, String tooltip) {
		setValue(tooltip);
		setValue(icon);
	}
}
