package wow.scraper.exporters.spell.excel;

import wow.commons.model.pve.PhaseId;
import wow.scraper.exporters.excel.ExcelSheetWriter;
import wow.scraper.parsers.tooltip.AbilityTooltipParser;

/**
 * User: POlszewski
 * Date: 2023-08-27
 */
public abstract class SpellBaseSheetWriter<P> extends ExcelSheetWriter<P, SpellBaseExcelBuilder> {
	protected SpellBaseSheetWriter(SpellBaseExcelBuilder builder) {
		super(builder);
	}

	protected PhaseId getPhase(AbilityTooltipParser parser) {
		PhaseId phaseOverride = config.getSpellPhaseOverrides().get(parser.getSpellId());

		if (phaseOverride != null) {
			return phaseOverride;
		}

		if (parser.getPhase() != parser.getGameVersion().getEarliestNonPrepatchPhase()) {
			return parser.getPhase();
		}

		return null;
	}

	@Override
	protected <T extends Enum<T>> void setValue(Enum<T> value) {
		if (value != null) {
			setValue(value.name().toLowerCase());
		} else {
			setValue((String)null);
		}
	}
}
