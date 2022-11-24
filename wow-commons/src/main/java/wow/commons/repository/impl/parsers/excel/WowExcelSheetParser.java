package wow.commons.repository.impl.parsers.excel;

import wow.commons.model.Duration;
import wow.commons.model.Percent;
import wow.commons.util.ExcelSheetReader;

import java.util.Optional;

/**
 * User: POlszewski
 * Date: 2022-11-24
 */
public abstract class WowExcelSheetParser extends ExcelSheetReader {
	protected WowExcelSheetParser(String sheetName) {
		super(sheetName);
	}

	protected class ExcelColumn extends ExcelSheetReader.ExcelColumn {
		public ExcelColumn(String name, boolean optional) {
			super(name, optional);
		}

		public Percent getPercent(Percent defaultValue) {
			return getOptionalPercent().orElse(defaultValue);
		}

		public Percent getPercent() {
			return getOptionalPercent().orElseThrow(this::columnIsEmpty);
		}

		public Duration getDuration(Duration defaultValue) {
			return getOptionalDuration().orElse(defaultValue);
		}

		public Duration getDuration() {
			return getOptionalDuration().orElseThrow(this::columnIsEmpty);
		}

		private Optional<Percent> getOptionalPercent() {
			return getOptionalString()
					.map(seconds -> Percent.of(Double.parseDouble(seconds)));
		}

		private Optional<Duration> getOptionalDuration() {
			return getOptionalString()
					.map(seconds -> Duration.seconds(Double.parseDouble(seconds)));
		}
	}

	@Override
	protected ExcelColumn column(String name, boolean optional) {
		return new ExcelColumn(name, optional);
	}

	@Override
	protected ExcelColumn column(String name) {
		return new ExcelColumn(name, false);
	}
}
