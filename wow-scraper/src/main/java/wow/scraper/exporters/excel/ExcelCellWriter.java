package wow.scraper.exporters.excel;

import lombok.AllArgsConstructor;
import polszewski.excel.writer.ExcelWriter;
import polszewski.excel.writer.style.Font;
import polszewski.excel.writer.style.Style;
import wow.commons.model.Money;
import wow.commons.model.Percent;

import java.util.List;
import java.util.function.Consumer;
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
		setHeader(colName, null);
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

	protected void setValue(Double value) {
		if (value != null) {
			writer.setCell(value, DATA_STYLE);
		} else {
			writer.setCell(null, DATA_STYLE);
		}
	}

	protected <T extends Enum<T>> void setValue(Enum<T> value) {
		if (value != null) {
			writer.setCell(value.name(), DATA_STYLE);
		} else {
			writer.setCell(null, DATA_STYLE);
		}
	}

	protected void setValue(String value) {
		writer.setCell(value, DATA_STYLE);
	}

	protected void setValue(Percent value) {
		if (value != null) {
			writer.setCell(value.value(), DATA_STYLE);
		} else {
			writer.setCell(null, DATA_STYLE);
		}
	}

	protected void setValue(Money value) {
		if (value != null) {
			writer.setCell(value.toString(), DATA_STYLE);
		} else {
			writer.setCell(null, DATA_STYLE);
		}
	}

	protected <T extends Enum<T>> void setValue(List<T> list) {
		if (list != null && !list.isEmpty()) {
			setValue(list.stream().map(Enum::name).collect(Collectors.joining(",")));
		} else {
			writer.nextCell();
		}
	}

	protected <T> void setValue(List<T> list, Function<T, String> mapper) {
		if (list != null && !list.isEmpty()) {
			setValue(list.stream().map(mapper).collect(Collectors.joining(",")));
		} else {
			writer.nextCell();
		}
	}

	protected void setList(List<String> values, int maxValues) {
		setList(values, maxValues, this::setValue, 1);
	}

	protected <T> void setList(List<T> values, int maxValues, Consumer<T> valueSetter, int numEmptyCells) {
		if (values.size() > maxValues) {
			throw new IllegalArgumentException("Too many values: " + values.size());
		}

		for (int i = 1; i <= maxValues; ++i) {
			if (values.size() >= i) {
				valueSetter.accept(values.get(i - 1));
			} else {
				fillRemainingEmptyCols(numEmptyCells);
			}
		}
	}

	protected void fillRemainingEmptyCols(int numCols) {
		for (int colNo = 0; colNo < numCols; ++colNo) {
			writer.nextCell();
		}
	}
}
