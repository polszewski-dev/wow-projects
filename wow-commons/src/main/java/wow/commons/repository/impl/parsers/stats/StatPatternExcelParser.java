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
				new SheetReader("base", () -> readStatPatterns(itemStatPatterns), colPattern),
				new SheetReader("onuse", () -> readStatPatterns(itemStatPatterns), colPattern),
				new SheetReader("proc", () -> readStatPatterns(itemStatPatterns), colPattern),
				new SheetReader("set", () -> readStatPatterns(itemStatPatterns), colPattern),
				new SheetReader("ignored", () -> readStatPatterns(itemStatPatterns), colPattern),
				new SheetReader("misc_bonus", () -> readStatPatterns(itemStatPatterns), colPattern),
				new SheetReader("misc_onuse", () -> readStatPatterns(itemStatPatterns), colPattern),
				new SheetReader("misc_proc", () -> readStatPatterns(itemStatPatterns), colPattern),
				new SheetReader("misc_set", () -> readStatPatterns(itemStatPatterns), colPattern),
				new SheetReader("gem", () -> readStatPatterns(gemStatPatterns), colPattern),
				new SheetReader("socket", () -> readStatPatterns(socketBonusStatPatterns), colPattern)
		);
	}

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

	private void readStatPatterns(List<StatPattern> patterns) {
		var pattern = colPattern.getString();
		var setters = getParsedStatSetters();
		var params = getParams(patterns);
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
			case "Ignored":
				return IgnoreStatSetter.INSTANCE;
			default:
				return new IntStatSetter(PrimitiveAttributeSupplier.fromString(line), Math.max(groupNo, 1));
		}
	}

	private StatSetterParams getParams(List<StatPattern> patterns) {
		StatSetterParams params = new StatSetterParams();

		if (patterns == itemStatPatterns) {
			params.setType(colSpecialType.getString(null));
			params.setStatsSupplier(colSpecialStat.getEnum(PrimitiveAttributeSupplier::fromString, null));
			params.setAmount(colSpecialAmount.getString(null));
			params.setDuration(colSpecialDuration.getString(null));
			params.setCooldown(colSpecialCd.getString(null));
			params.setProcChance(colSpecialProcChance.getString(null));
			params.setProcCooldown(colSpecialProcCd.getString(null));
		}

		return params;
	}
}
