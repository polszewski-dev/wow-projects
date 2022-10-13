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
			readParsers(excelReader, parsers, header);
		}
	}

	private void readParsers(ExcelReader excelReader, List<StatParser> parsers, Map<String, Integer> header) {
		var pattern = getOptionalString(COL_PATTERN, excelReader, header);

		if (pattern.isPresent()) {
			List<ParsedStatSetter> setters = getParsedStatSetters(excelReader, header);

			StatSetterParams params = getParams(excelReader, parsers, header);
			StatParser parser = new StatParser(pattern.get().trim(), setters, params);

			parsers.add(parser);

			getOptionalString(COL_ALIAS, excelReader, header)
					.ifPresent(alias -> aliases.computeIfAbsent(alias, x -> new ArrayList<>()).add(parser));
		}
	}

	private List<ParsedStatSetter> getParsedStatSetters(ExcelReader excelReader, Map<String, Integer> header) {
		var setters = Stream.of(
						getOptionalString(COL_STAT1, excelReader, header),
						getOptionalString(COL_STAT2, excelReader, header)
				)
				.map(line -> line.map(this::parseStatSetter))
				.flatMap(Optional::stream)
				.collect(Collectors.toList());

		if (!setters.isEmpty()) {
			return setters;
		}

		return List.of(ParsedStatSetter.group(0, MiscStatSetter.INSTANCE));
	}

	private ParsedStatSetter parseStatSetter(String line) {
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
			params.setSpecialType(getOptionalString(COL_SPECIAL_TYPE, excelReader, header).orElse(null));
			params.setAttributeParser(getOptionalString(COL_SPECIAL_STAT, excelReader, header).map(SimpleAttributeParser::new).orElse(null));
			params.setSpecialAmount(getOptionalString(COL_SPECIAL_AMOUNT, excelReader, header).orElse(null));
			params.setSpecialDuration(getOptionalString(COL_SPECIAL_DURATION, excelReader, header).orElse(null));
			params.setSpecialCd(getOptionalString(COL_SPECIAL_CD, excelReader, header).orElse(null));
			params.setSpecialProcChance(getOptionalString(COL_SPECIAL_PROC_CHANCE, excelReader, header).orElse(null));
			params.setSpecialProcCd(getOptionalString(COL_SPECIAL_PROC_CD, excelReader, header).orElse(null));
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
