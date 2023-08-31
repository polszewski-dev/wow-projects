package wow.scraper.repository.impl.excel;

import wow.commons.model.attribute.condition.AttributeCondition;
import wow.commons.model.attribute.primitive.PrimitiveAttributeId;
import wow.commons.model.pve.GameVersionId;
import wow.commons.repository.impl.parser.excel.WowExcelSheetParser;
import wow.scraper.parser.spell.params.AttributePattern;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.IntStream;

import static wow.commons.repository.impl.parser.excel.CommonColumnNames.*;

/**
 * User: POlszewski
 * Date: 2023-09-09
 */
public abstract class AbstractPatternSheetParser extends WowExcelSheetParser {
	protected AbstractPatternSheetParser(String sheetName) {
		super(sheetName);
	}

	@Override
	protected ExcelColumn getColumnIndicatingOptionalRow() {
		return colPattern;
	}

	private final ExcelColumn colPattern = column("pattern");

	protected String getPattern() {
		return colPattern.getString().trim();
	}

	private final ExcelColumn colReqVersion = column("req_version");

	protected Set<GameVersionId> getReqVersion() {
		return colReqVersion.getSet(GameVersionId::parse);
	}

	protected List<AttributePattern> getAttributes(String prefix, int maxAttributes) {
		return IntStream.rangeClosed(1, maxAttributes)
				.mapToObj(idx -> getAttributePattern(prefix, idx))
				.filter(Objects::nonNull)
				.toList();
	}

	private AttributePattern getAttributePattern(String prefix, int idx) {
		var colId = column(getAttrId(idx), true).prefixed(prefix);
		var colValue = column(getAttrValue(idx), true).prefixed(prefix);
		var colCondition = column(getAttrCondition(idx), true).prefixed(prefix);

		if (colId.isEmpty()) {
			assertAllColumnsAreEmpty(colId, colCondition, colValue);
			return null;
		}

		var id = colId.getEnum(PrimitiveAttributeId::parse);
		var value = colValue.getString();
		var condition = colCondition.getString(null);

		validateCondition(condition);

		return new AttributePattern(id, value, condition, false);
	}

	protected void assertAllColumnsAreEmpty(ExcelColumn... columns) {
		for (ExcelColumn column : columns) {
			if (!column.isEmpty()) {
				throw new IllegalArgumentException("Column '%s' is not empty".formatted(column.getName()));
			}
		}
	}

	private static void validateCondition(String condition) {
		if (condition != null && !condition.contains("$")) {
			AttributeCondition.parse(condition);
		}
	}
}
