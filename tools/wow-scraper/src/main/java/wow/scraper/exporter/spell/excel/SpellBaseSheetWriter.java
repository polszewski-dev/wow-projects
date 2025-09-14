package wow.scraper.exporter.spell.excel;

import wow.commons.model.Percent;
import wow.commons.model.effect.Effect;
import wow.commons.model.effect.component.Event;
import wow.commons.model.effect.component.StatConversion;
import wow.scraper.exporter.excel.ExcelSheetWriter;
import wow.scraper.exporter.excel.WowExcelBuilder;

import static wow.commons.repository.impl.parser.spell.SpellBaseExcelColumnNames.*;
import static wow.scraper.util.CommonAssertions.assertSizeNoLargerThan;

/**
 * User: POlszewski
 * Date: 2023-08-27
 */
public abstract class SpellBaseSheetWriter<P, B extends WowExcelBuilder> extends ExcelSheetWriter<P, B> {
	protected SpellBaseSheetWriter(B builder) {
		super(builder);
	}

	protected void writeModifierComponentHeader(int maxAttributes) {
		writeAttributeHeader(MOD_PREFIX, maxAttributes);
	}

	protected void writeModifierComponent(Effect effect, int maxAttributes) {
		var modifierComponent = effect.getModifierComponent();
		var attributes = modifierComponent != null ? modifierComponent.attributes() : null;

		writeAttributes(attributes, maxAttributes);
	}

	protected void writeStatConversionHeader() {
		for (int i = 1; i <= MAX_STAT_CONVERSIONS; ++i) {
			writeStatConversionHeader(i);
		}
	}

	private void writeStatConversionHeader(int idx) {
		var prefix = getStatConversionPrefix(idx);
		setHeader(STAT_CONVERSION_FROM, prefix);
		setHeader(STAT_CONVERSION_TO, prefix);
		setHeader(STAT_CONVERSION_TO_CONDITION, prefix);
		setHeader(STAT_CONVERSION_RATIO, prefix);
	}

	protected void writeStatConversions(Effect effect) {
		for (int i = 0; i < MAX_STAT_CONVERSIONS; ++i) {
			if (i < effect.getStatConversions().size()) {
				writeStatConversion(effect.getStatConversions().get(i));
			} else {
				writeStatConversion(null);
			}
		}
	}

	private void writeStatConversion(StatConversion statConversion) {
		if (statConversion == null) {
			fillRemainingEmptyCols(4);
			return;
		}

		setValue(statConversion.from());
		setValue(statConversion.to());
		setValue(statConversion.toCondition());
		setValue(statConversion.ratioPct());
	}

	protected void writeEventHeader(int maxEvents) {
		for (int i = 1; i <= maxEvents; ++i) {
			writeSingleEventHeader(i);
		}
	}

	private void writeSingleEventHeader(int i) {
		String prefix = getEventPrefix(i);
		setHeader(EVENT_ON, prefix);
		setHeader(EVENT_CONDITION, prefix);
		setHeader(EVENT_CHANCE_PCT, prefix);
		setHeader(EVENT_ACTION, prefix);
		setHeader(EVENT_TRIGGERED_SPELL, prefix);
		setHeader(EVENT_ACTION_PARAMS, prefix);
	}

	protected void writeEvents(Effect effect, int maxEvents) {
		assertSizeNoLargerThan("events", effect.getEvents(), maxEvents);

		for (int i = 0; i < maxEvents; ++i) {
			if (i < effect.getEvents().size()) {
				writeEvent(effect.getEvents().get(i));
			} else {
				writeEvent(null);
			}
		}
	}

	private void writeEvent(Event event) {
		if (event == null) {
			fillRemainingEmptyCols(6);
			return;
		}

		setValue(event.types());
		setValue(event.condition());
		setValue(event.chance(), Percent._100);
		setValue(event.actions());
		setValue(event.triggeredSpell() != null ? event.triggeredSpell().getId().value() : null);
		setValue(event.actionParameters() != null ? event.actionParameters().value() : null);
	}

	@Override
	protected <T extends Enum<T>> String formatEnum(Enum<T> value) {
		boolean defaultName = value.name().equals(value.toString());
		return defaultName ? value.toString().toLowerCase() : value.toString();
	}
}
