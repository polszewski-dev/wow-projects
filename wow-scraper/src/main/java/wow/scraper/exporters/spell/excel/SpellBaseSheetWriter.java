package wow.scraper.exporters.spell.excel;

import wow.scraper.exporters.excel.ExcelSheetWriter;

/**
 * User: POlszewski
 * Date: 2023-08-27
 */
public abstract class SpellBaseSheetWriter<P> extends ExcelSheetWriter<P, SpellBaseExcelBuilder> {
	protected SpellBaseSheetWriter(SpellBaseExcelBuilder builder) {
		super(builder);
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
