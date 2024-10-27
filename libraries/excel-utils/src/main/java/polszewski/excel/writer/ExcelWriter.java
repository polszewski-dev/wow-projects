package polszewski.excel.writer;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import polszewski.excel.writer.style.Style;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * User: POlszewski
 * Date: 2019-01-10
 */
public class ExcelWriter {
	private Workbook workbook;
	private Sheet sheet;
	private Row row;
	private int rowIdx;
	private int colIdx;
	private Map<Style, CellStyle> styleMap = new HashMap<>();
	private Map<polszewski.excel.writer.style.Font, Font> fontMap = new HashMap<>();
	private DataFormat dataFormat;

	public ExcelWriter open() {
		workbook = new HSSFWorkbook();
		return this;
	}

	public ExcelWriter open(String sheetName) {
		return open().nextSheet(sheetName);
	}

	public void save(String filePath) throws IOException {
		try {
			try (var fileOut = new FileOutputStream(filePath)) {
				workbook.write(fileOut);
				workbook.close();
			}
		} finally {
			this.dataFormat = null;
			this.styleMap.clear();
			this.styleMap = null;
			this.fontMap.clear();
			this.fontMap = null;
			this.row = null;
			this.sheet = null;
			this.workbook = null;
		}
	}

	public ExcelWriter nextSheet(String sheetName) {
		this.sheet = workbook.createSheet(sheetName);
		this.row = null;
		this.rowIdx = 0;
		this.colIdx = 0;
		return this;
	}

	public ExcelWriter setColumnWidth(int value) {
		this.sheet.setColumnWidth(colIdx, value * 256);//Set the width (in units of 1/256th of a character width)
		return this;
	}

	public ExcelWriter setCell(String value) {
		return setCell(value, null);
	}

	public ExcelWriter setCell(double value) {
		return setCell(value, null);
	}

	public ExcelWriter setCell(String value, Style style) {
		return setCell(value, style, 1);
	}

	public ExcelWriter setCell(double value, Style style) {
		return setCell(value, style, 1);
	}

	public ExcelWriter setCell(String value, Style style, int colCount) {
		if (colCount > 1) {
			sheet.addMergedRegion(new CellRangeAddress(rowIdx, rowIdx, colIdx, colIdx + colCount - 1));
		}
		getCell(style).setCellValue(value);
		return nextCell();
	}

	public ExcelWriter setCell(double value, Style style, int colCount) {
		if (colCount > 1) {
			sheet.addMergedRegion(new CellRangeAddress(rowIdx, rowIdx, colIdx, colIdx + colCount - 1));
		}
		getCell(style).setCellValue(value);
		return nextCell();
	}

	private Cell getCell(Style style) {
		ensureRow();
		Cell cell = row.createCell(colIdx);
		if (style != null) {
			cell.setCellStyle(getCellStyle(style));
		}
		return cell;
	}

	private CellStyle getCellStyle(Style style) {
		CellStyle cellStyle = styleMap.get(style);
		if (cellStyle != null) {
			return cellStyle;
		}
		cellStyle = workbook.createCellStyle();
		if (style.alignment() != null) {
			cellStyle.setAlignment(style.alignment());
		}
		if (style.verticalAlignment() != null) {
			cellStyle.setVerticalAlignment(style.verticalAlignment());
		}
		if (style.backgroundColor() != null) {
			cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			cellStyle.setFillForegroundColor(style.backgroundColor().getIndex());
		}
		if (style.font() != null) {
			cellStyle.setFont(getFont(style.font()));
		}
		if (style.format() != null) {
			if (dataFormat == null) {
				this.dataFormat = workbook.createDataFormat();
			}
			cellStyle.setDataFormat(dataFormat.getFormat(style.format()));
		}
		styleMap.put(style, cellStyle);
		return cellStyle;
	}

	private Font getFont(polszewski.excel.writer.style.Font font) {
		Font xlsFont = fontMap.get(font);
		if (xlsFont != null) {
			return xlsFont;
		}
		xlsFont = workbook.createFont();
		if (font.name() != null) {
			xlsFont.setFontName(font.name());
		}
		if (font.size() != null) {
			xlsFont.setFontHeightInPoints(font.size().shortValue());
		}
		if (font.color() != null) {
			xlsFont.setColor(font.color().getIndex());
		}
		if (font.bold() != null) {
			xlsFont.setBold(font.bold());
		}
		if (font.italic() != null) {
			xlsFont.setItalic(font.italic());
		}
		fontMap.put(font, xlsFont);
		return xlsFont;
	}

	private void ensureRow() {
		if (row == null) {
			this.row = sheet.createRow(rowIdx);
		}
	}

	public ExcelWriter nextCell() {
		++colIdx;
		return this;
	}

	public ExcelWriter nextRow() {
		++rowIdx;
		this.row = null;
		this.colIdx = 0;
		return this;
	}

	public ExcelWriter freeze(int colSplit, int rowSplit) {
		sheet.createFreezePane(colSplit, rowSplit);
		return this;
	}
}
