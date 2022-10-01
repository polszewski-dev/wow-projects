package wow.commons.repository.impl.parsers;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import polszewski.excel.reader.ExcelReader;
import polszewski.excel.reader.PoiExcelReader;
import wow.commons.model.spells.SpellId;
import wow.commons.repository.impl.parsers.setters.*;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static wow.commons.util.ExcelUtil.*;

/**
 * User: POlszewski
 * Date: 2021-04-04
 */
class StatParserRepository {
	private final List<StatParser> itemStatParsers = new ArrayList<>();
	private final List<StatParser> gemStatParsers = new ArrayList<>();
	private final List<StatParser> socketBonusStatParsers = new ArrayList<>();
	private final Map<String, List<StatParser>> aliases = new HashMap<>();

	private static StatParserRepository instance;

	public static StatParserRepository getInstance() {
		if (instance == null) {
			instance = new StatParserRepository();
			try {
				instance.init();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return instance;
	}

	private void init() throws IOException, InvalidFormatException {
		readFromXls();
	}

	private void readFromXls() throws IOException, InvalidFormatException {
		try (ExcelReader excelReader = new PoiExcelReader(StatParserRepository.class.getResourceAsStream("/xls/stat_parsers.xls"))) {
			while (excelReader.nextSheet()) {
				if (!excelReader.nextRow()) {
					continue;
				}

				switch (excelReader.getCurrentSheetName()) {
					case SHEET_ITEM:
					case SHEET_ITEM2:
					case SHEET_ITEM3:
						readParsers(excelReader, itemStatParsers);
						break;
					case SHEET_GEM:
						readParsers(excelReader, gemStatParsers);
						break;
					case SHEET_SOCKET:
						readParsers(excelReader, socketBonusStatParsers);
						break;
					default:
						throw new IllegalArgumentException("Unknown sheet: " + excelReader.getCurrentSheetName());
				}
			}
		}
	}

	private static final String SHEET_ITEM = "item";
	private static final String SHEET_ITEM2 = "item2";
	private static final String SHEET_ITEM3 = "item3";
	private static final String SHEET_GEM = "gem";
	private static final String SHEET_SOCKET = "socket";

	private static final String COL_PATTERN = "pattern";
	private static final String COL_STAT1 = "stat1";
	private static final String COL_STAT2 = "stat2";
	private static final String COL_ALIAS = "alias";

	private static final String COL_SPECIAL_TYPE = "special:type";
	private static final String COL_SPECIAL_STAT = "special:stat";
	private static final String COL_SPECIAL_AMOUNT = "special:amount";
	private static final String COL_SPECIAL_DURATION = "special:duration";
	private static final String COL_SPECIAL_CD = "special:cd";
	private static final String COL_SPECIAL_PROC_CHANCE = "special:proc chance";
	private static final String COL_SPECIAL_PROC_CD = "special:proc cd";
	private static final String COL_SPECIAL_SPELL = "special:spell";

	private void readParsers(ExcelReader excelReader, List<StatParser> parsers) {
		Map<String, Integer> header = getHeader(excelReader);

		while (excelReader.nextRow()) {
			String pattern = getString(COL_PATTERN, excelReader, header);

			if (pattern == null) {
				continue;
			}

			ParsedStatSetter stat1 = parseStatSetter(getString(COL_STAT1, excelReader, header));
			ParsedStatSetter stat2 = parseStatSetter(getString(COL_STAT2, excelReader, header));
			String alias = getString(COL_ALIAS, excelReader, header);

			if (stat1 == null && stat2 == null) {
				stat1 = ParsedStatSetter.group(0, MiscStatSetter.INSTANCE);
			}

			StatSetterParams params = getParams(excelReader, parsers, header);
			List<ParsedStatSetter> setters = Stream.of(stat1, stat2).filter(Objects::nonNull).collect(Collectors.toList());
			StatParser parser = new StatParser(pattern.trim(), setters, params);

			parsers.add(parser);

			if (alias != null) {
				aliases.computeIfAbsent(alias, x -> new ArrayList<>()).add(parser);
			}
		}
	}

	private ParsedStatSetter parseStatSetter(String line) {
		if (line == null) {
			return null;
		}

		line = line.trim();

		int groupNo = -1;

		if (line.contains(":")) {
			int pos = line.indexOf(":");
			groupNo = Integer.parseInt(line.substring(pos + 1));
			line = line.substring(0, pos);
		}

		switch (line) {
			case "Proc":
				return ParsedStatSetter.group(Math.max(groupNo, 0), ProcStatSetter.INSTANCE);
			case "OnUse":
				return ParsedStatSetter.group(Math.max(groupNo, 0), OnUseStatSetter.INSTANCE);
			case "Equivalent":
				return ParsedStatSetter.group(Math.max(groupNo, 0), EquivalentStatSetter.INSTANCE);
			case "Ignored":
				return ParsedStatSetter.group(Math.max(groupNo, 0), IgnoreStatSetter.INSTANCE);
			default:
				return ParsedStatSetter.group(Math.max(groupNo, 1), new SimpleAttributeParser(line));
		}
	}

	private StatSetterParams getParams(ExcelReader excelReader, List<StatParser> parsers, Map<String, Integer> header) {
		StatSetterParams params = new StatSetterParams();

		if (parsers == itemStatParsers) {
			params.setSpecialType(getString(COL_SPECIAL_TYPE, excelReader, header));
			String statStr = getString(COL_SPECIAL_STAT, excelReader, header);
			if (statStr != null) {
				params.setAttributeParser(new SimpleAttributeParser(statStr));
			}
			params.setSpecialAmount(getString(COL_SPECIAL_AMOUNT, excelReader, header));
			params.setSpecialDuration(getString(COL_SPECIAL_DURATION, excelReader, header));
			params.setSpecialCd(getString(COL_SPECIAL_CD, excelReader, header));
			params.setSpecialProcChance(getString(COL_SPECIAL_PROC_CHANCE, excelReader, header));
			params.setSpecialProcCd(getString(COL_SPECIAL_PROC_CD, excelReader, header));
			params.setSpecialSpell(getList(COL_SPECIAL_SPELL, SpellId::parse, excelReader, header));
		}

		return params;
	}

	public List<StatParser> getItemStatParsers() {
		return itemStatParsers;
	}

	public List<StatParser> getGemStatParsers() {
		return gemStatParsers;
	}

	public List<StatParser> getSocketBonusStatParsers() {
		return socketBonusStatParsers;
	}

	public void resetAllItemParsers() {
		for (StatParser pattern : itemStatParsers) {
			pattern.reset();
		}
	}

	public void resetAllGemParsers() {
		for (StatParser pattern : gemStatParsers) {
			pattern.reset();
		}
	}

	public void resetAllSocketBonusParsers() {
		for (StatParser pattern : socketBonusStatParsers) {
			pattern.reset();
		}
	}

	public StatParser getByAlias(String alias) {
		List<StatParser> parsers = aliases.get(alias);
		if (parsers.size() == 1) {
			return parsers.get(0);
		}
		if (parsers.stream().noneMatch(StatParser::hasMatch)) {
			return parsers.get(0);
		}
		List<StatParser> parsersWithMatch = parsers.stream()
												   .filter(StatParser::hasMatch)
												   .collect(Collectors.toList());
		if (parsersWithMatch.size() == 1) {
			return parsersWithMatch.get(0);
		}
		throw new IllegalArgumentException(alias);
	}
}
