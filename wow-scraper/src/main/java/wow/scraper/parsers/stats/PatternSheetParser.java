package wow.scraper.parsers.stats;

import polszewski.excel.reader.templates.ExcelSheetParser;
import wow.commons.model.pve.GameVersionId;
import wow.commons.util.PrimitiveAttributeSupplier;
import wow.scraper.parsers.setters.MiscStatSetter;
import wow.scraper.parsers.setters.StatSetter;
import wow.scraper.parsers.setters.StatSetterParams;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * User: POlszewski
 * Date: 2022-11-22
 */
public class PatternSheetParser extends ExcelSheetParser {
	private final ExcelColumn colPattern = column("pattern");
	private final ExcelColumn colStat1 = column("stat1");
	private final ExcelColumn colStat2 = column("stat2");

	private final ExcelColumn colSpecialType = column("special:type");
	private final ExcelColumn colSpecialStat = column("special:stat");
	private final ExcelColumn colSpecialAmount = column("special:amount");
	private final ExcelColumn colSpecialDuration = column("special:duration");
	private final ExcelColumn colExpression = column("special:expression");

	private final ExcelColumn colReqVersion = column("req_version");

	private final List<StatPattern> patterns;

	public PatternSheetParser(String sheetName, List<StatPattern> patterns) {
		super(sheetName);
		this.patterns = patterns;
	}

	@Override
	protected ExcelColumn getColumnIndicatingOptionalRow() {
		return colPattern;
	}

	@Override
	protected void readSingleRow() {
		var pattern = colPattern.getString();
		var setters = getParsedStatSetters();
		var params = getParams();
		var reqVersion = colReqVersion.getSet(GameVersionId::parse);
		var statPattern = new StatPattern(pattern.trim(), setters, params, reqVersion);
		patterns.add(statPattern);
	}

	private List<StatSetter> getParsedStatSetters() {
		var setters = Stream.of(
						colStat1.getString(null),
						colStat2.getString(null)
				)
				.filter(Objects::nonNull)
				.map(StatSetterParser::new)
				.map(StatSetterParser::parse)
				.toList();

		if (!setters.isEmpty()) {
			return setters;
		}

		return List.of(new MiscStatSetter());
	}

	private StatSetterParams getParams() {
		StatSetterParams params = new StatSetterParams();

		params.setType(colSpecialType.getString(null));
		params.setStatsSupplier(colSpecialStat.getEnum(PrimitiveAttributeSupplier::fromString, null));
		params.setAmount(colSpecialAmount.getString(null));
		params.setDuration(colSpecialDuration.getString(null));
		params.setExpression(colExpression.getString(null));

		return params;
	}
}
