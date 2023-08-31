package wow.scraper.exporter.excel;

import lombok.AllArgsConstructor;
import polszewski.excel.writer.ExcelWriter;
import polszewski.excel.writer.style.Font;
import polszewski.excel.writer.style.Style;
import wow.commons.model.Duration;
import wow.commons.model.Money;
import wow.commons.model.Percent;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * User: POlszewski
 * Date: 2023-05-18
 */
@AllArgsConstructor
public abstract class ExcelCellWriter {
	private static final Font FONT = Font.DEFAULT.name("Arial").size(9);
	private static final Style DATA_STYLE = Style.DEFAULT.font(FONT);
	private static final Style HEADER_STYLE = DATA_STYLE.font(FONT.bold(true));

	protected final ExcelWriter writer;

	protected void setHeader(String colName) {
		setHeader(colName, (Integer) null);
	}

	protected void setHeader(String colName, String prefix) {
		setHeader(prefix + colName, (Integer) null);
	}

	protected void setHeader(String colName, Integer width) {
		if (width != null) {
			writer.setColumnWidth(width);
		}
		writer.setCell(colName, HEADER_STYLE);
	}

	protected void setValue(Integer value) {
		if (value != null) {
			writer.setCell(value, DATA_STYLE);
		} else {
			writer.setCell(null, DATA_STYLE);
		}
	}

	protected void setValue(Integer value, Integer defaultValue) {
		if (Objects.equals(value, defaultValue)) {
			setValue((Integer) null);
		} else {
			setValue(value);
		}
	}

	protected void setValue(Double value) {
		if (value != null) {
			writer.setCell(value, DATA_STYLE);
		} else {
			writer.setCell(null, DATA_STYLE);
		}
	}

	protected <T extends Enum<T>> void setValue(Enum<T> value) {
		if (value != null) {
			writer.setCell(formatEnum(value), DATA_STYLE);
		} else {
			writer.setCell(null, DATA_STYLE);
		}
	}

	protected <T extends Enum<T>> void setValue(Enum<T> value, Enum<T> defaultValue) {
		if (Objects.equals(value, defaultValue)) {
			setValue((Enum<T>) null);
		} else {
			setValue(value);
		}
	}

	protected <T extends Enum<T>> String formatEnum(Enum<T> value) {
		return value.toString();
	}

	protected void setValue(Boolean value) {
		if (value != null && value) {
			writer.setCell("true", DATA_STYLE);
		} else {
			writer.setCell(null, DATA_STYLE);
		}
	}

	protected void setValue(String value) {
		writer.setCell(value, DATA_STYLE);
	}

	protected void setValue(Duration value) {
		if (value != null) {
			if (value.isInfinite()) {
				writer.setCell(value.toString(), DATA_STYLE);
			} else {
				writer.setCell(value.getSeconds(), DATA_STYLE);
			}
		} else {
			writer.setCell(null, DATA_STYLE);
		}
	}

	protected void setValue(Duration value, Duration defaultValue) {
		if (Objects.equals(value, defaultValue)) {
			setValue((Duration) null);
		} else {
			setValue(value);
		}
	}

	protected void setValue(Percent value) {
		if (value != null) {
			writer.setCell(value.value(), DATA_STYLE);
		} else {
			writer.setCell(null, DATA_STYLE);
		}
	}

	protected void setValue(Percent value, Percent defaultValue) {
		if (Objects.equals(value, defaultValue)) {
			setValue((Percent) null);
		} else {
			setValue(value);
		}
	}

	protected void setValue(Money value) {
		if (value != null) {
			writer.setCell(value.toString(), DATA_STYLE);
		} else {
			writer.setCell(null, DATA_STYLE);
		}
	}

	protected <T> void setValue(List<T> list) {
		Function<T, String> mapper = (T x) -> x instanceof Enum<?> ? formatEnum((Enum<?>) x) : x.toString();
		setValue(list, mapper);
	}

	protected <T> void setValue(List<T> list, Function<T, String> mapper) {
		if (list != null && !list.isEmpty()) {
			setValue(list.stream().map(mapper).collect(Collectors.joining(",")));
		} else {
			writer.nextCell();
		}
	}

	protected void fillRemainingEmptyCols(int numCols) {
		for (int colNo = 0; colNo < numCols; ++colNo) {
			writer.nextCell();
		}
	}
}
