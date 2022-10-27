package wow.scraper.excel;

import polszewski.excel.writer.ExcelWriter;
import polszewski.excel.writer.style.Font;
import polszewski.excel.writer.style.Style;
import wow.commons.model.Money;
import wow.commons.model.Percent;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * User: POlszewski
 * Date: 2022-10-31
 */
public abstract class AbstractExcelBuilder {
	private static final Font FONT = Font.DEFAULT;
	private static final Style DATA_STYLE = Style.DEFAULT.font(FONT);
	private static final Style HEADER_STYLE = DATA_STYLE.font(FONT.bold());

	protected final ExcelWriter writer = new ExcelWriter();

	public abstract void start();

	public void finish(String fileName) throws IOException {
		writer.save(fileName);
	}

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

	protected void setValue(Enum value) {
		if (value != null) {
			writer.setCell(value.name(), DATA_STYLE);
		} else {
			writer.setCell(null, DATA_STYLE);
		}
	}

	protected void setValue(String value) {
		if (value != null) {
			writer.setCell(value, DATA_STYLE);
		} else {
			writer.setCell(null, DATA_STYLE);
		}
	}

	protected void setValue(Percent value) {
		if (value != null) {
			writer.setCell(value.getValue(), DATA_STYLE);
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

	protected <T extends Enum> void setValue(List<T> list) {
		if (list != null && !list.isEmpty()) {
			setValue(list.stream().map(Enum::name).collect(Collectors.joining(",")));
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
				for (int c = 0; c < numEmptyCells; c++) {
					writer.nextCell();
				}
			}
		}
	}
}
