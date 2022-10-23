package wow.commons.repository.impl.parsers.stats;

import wow.commons.repository.impl.parsers.setters.*;
import wow.commons.util.ExcelParser;

import java.io.InputStream;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * User: POlszewski
 * Date: 2022-10-13
 */
public class StatPatternExcelParser extends ExcelParser {
	private final List<StatPattern> itemStatPatterns;
	private final List<StatPattern> gemStatPatterns;
	private final List<StatPattern> socketBonusStatPatterns;

	public StatPatternExcelParser(
			List<StatPattern> itemStatPatterns,
			List<StatPattern> gemStatPatterns,
			List<StatPattern> socketBonusStatPatterns) {
		this.itemStatPatterns = itemStatPatterns;
		this.gemStatPatterns = gemStatPatterns;
		this.socketBonusStatPatterns = socketBonusStatPatterns;
	}

	@Override
	protected InputStream getExcelInputStream() {
		return fromResourcePath("/xls/stat_parsers.xls");
	}

	@Override
	protected Stream<SheetReader> getSheetReaders() {
		return Stream.of(
				new SheetReader("item", () -> readStatPatterns(itemStatPatterns), COL_PATTERN),
				new SheetReader("item2", () -> readStatPatterns(itemStatPatterns), COL_PATTERN),
				new SheetReader("item3", () -> readStatPatterns(itemStatPatterns), COL_PATTERN),
				new SheetReader("gem", () -> readStatPatterns(gemStatPatterns), COL_PATTERN),
				new SheetReader("socket", () -> readStatPatterns(socketBonusStatPatterns), COL_PATTERN)
		);
	}

	private final ExcelColumn COL_PATTERN = column("pattern");
	private final ExcelColumn COL_STAT1 = column("stat1");
	private final ExcelColumn COL_STAT2 = column("stat2");
	private final ExcelColumn COL_ALIAS = column("alias");

	private final ExcelColumn COL_SPECIAL_TYPE = column("special:type");
	private final ExcelColumn COL_SPECIAL_STAT = column("special:stat");
	private final ExcelColumn COL_SPECIAL_AMOUNT = column("special:amount");
	private final ExcelColumn COL_SPECIAL_DURATION = column("special:duration");
	private final ExcelColumn COL_SPECIAL_CD = column("special:cd");
	private final ExcelColumn COL_SPECIAL_PROC_CHANCE = column("special:proc chance");
	private final ExcelColumn COL_SPECIAL_PROC_CD = column("special:proc cd");

	private void readStatPatterns(List<StatPattern> patterns) {
		var pattern = COL_PATTERN.getString();
		var setters = getParsedStatSetters();
		var params = getParams(patterns);
		var statPattern = new StatPattern(pattern.trim(), setters, params, COL_ALIAS.getString(null));
		patterns.add(statPattern);
	}

	private List<StatSetter> getParsedStatSetters() {
		var setters = Stream.of(
						COL_STAT1.getString(null),
						COL_STAT2.getString(null)
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
			case "Ignored":
				return IgnoreStatSetter.INSTANCE;
			default:
				return new IntStatSetter(PrimitiveAttributeSupplier.fromString(line), Math.max(groupNo, 1));
		}
	}

	private StatSetterParams getParams(List<StatPattern> patterns) {
		StatSetterParams params = new StatSetterParams();

		if (patterns == itemStatPatterns) {
			params.setType(COL_SPECIAL_TYPE.getString(null));
			params.setStatsSupplier(COL_SPECIAL_STAT.getEnum(PrimitiveAttributeSupplier::fromString, null));
			params.setAmount(COL_SPECIAL_AMOUNT.getString(null));
			params.setDuration(COL_SPECIAL_DURATION.getString(null));
			params.setCooldown(COL_SPECIAL_CD.getString(null));
			params.setProcChance(COL_SPECIAL_PROC_CHANCE.getString(null));
			params.setProcCooldown(COL_SPECIAL_PROC_CD.getString(null));
		}

		return params;
	}
}
