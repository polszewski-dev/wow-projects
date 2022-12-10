package wow.scraper.parsers.stats;

import polszewski.excel.reader.templates.ExcelSheetParser;
import wow.commons.util.PrimitiveAttributeSupplier;
import wow.scraper.parsers.setters.*;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
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
	private final ExcelColumn colSpecialCd = column("special:cd");
	private final ExcelColumn colSpecialProcChance = column("special:proc chance");
	private final ExcelColumn colSpecialProcCd = column("special:proc cd");

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
		var statPattern = new StatPattern(pattern.trim(), setters, params);
		patterns.add(statPattern);
	}

	private List<StatSetter> getParsedStatSetters() {
		var setters = Stream.of(
						colStat1.getString(null),
						colStat2.getString(null)
				)
				.filter(Objects::nonNull)
				.map(this::parseStatSetter)
				.collect(Collectors.toList());

		if (!setters.isEmpty()) {
			return setters;
		}

		return List.of(new MiscStatSetter(0));
	}

	private StatSetter parseStatSetter(String line) {
		line = line.trim();

		int groupNo = -1;

		if (line.contains(":")) {
			int pos = line.indexOf(":");
			groupNo = Integer.parseInt(line.substring(pos + 1));
			line = line.substring(0, pos);
		}

		switch (line) {
			case "Proc":
				return new ProcStatSetter(Math.max(groupNo, 0));
			case "OnUse":
				return new OnUseStatSetter(Math.max(groupNo, 0));
			case "Equivalent":
				return new EquivalentStatSetter(Math.max(groupNo, 0));
			case "Misc":
				return new MiscStatSetter(0);
			case "Ignored":
				return IgnoreStatSetter.INSTANCE;
			default:
				return new IntStatSetter(line, Math.max(groupNo, 1));
		}
	}

	private StatSetterParams getParams() {
		StatSetterParams params = new StatSetterParams();

		params.setType(colSpecialType.getString(null));
		params.setStatsSupplier(colSpecialStat.getEnum(PrimitiveAttributeSupplier::fromString, null));
		params.setAmount(colSpecialAmount.getString(null));
		params.setDuration(colSpecialDuration.getString(null));
		params.setCooldown(colSpecialCd.getString(null));
		params.setProcChance(colSpecialProcChance.getString(null));
		params.setProcCooldown(colSpecialProcCd.getString(null));

		return params;
	}
}
