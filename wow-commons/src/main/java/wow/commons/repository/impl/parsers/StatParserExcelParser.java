package wow.commons.repository.impl.parsers;

import wow.commons.model.spells.SpellId;
import wow.commons.repository.impl.parsers.setters.*;
import wow.commons.util.ExcelParser;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * User: POlszewski
 * Date: 2022-10-13
 */
public class StatParserExcelParser extends ExcelParser {
	private final List<StatParser> itemStatParsers;
	private final List<StatParser> gemStatParsers;
	private final List<StatParser> socketBonusStatParsers;
	private final Map<String, List<StatParser>> aliases;

	public StatParserExcelParser(List<StatParser> itemStatParsers, List<StatParser> gemStatParsers, List<StatParser> socketBonusStatParsers, Map<String, List<StatParser>> aliases) {
		this.itemStatParsers = itemStatParsers;
		this.gemStatParsers = gemStatParsers;
		this.socketBonusStatParsers = socketBonusStatParsers;
		this.aliases = aliases;
	}

	@Override
	protected InputStream getExcelInputStream() {
		return fromResourcePath("/xls/stat_parsers.xls");
	}

	@Override
	protected Stream<SheetReader> getSheetReaders() {
		return Stream.of(
				new SheetReader(SHEET_ITEM, () -> readParsers(itemStatParsers), COL_PATTERN),
				new SheetReader(SHEET_ITEM2, () -> readParsers(itemStatParsers), COL_PATTERN),
				new SheetReader(SHEET_ITEM3, () -> readParsers(itemStatParsers), COL_PATTERN),
				new SheetReader(SHEET_GEM, () -> readParsers(gemStatParsers), COL_PATTERN),
				new SheetReader(SHEET_SOCKET, () -> readParsers(socketBonusStatParsers), COL_PATTERN)
		);
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

	private void readParsers(List<StatParser> parsers) {
		var pattern = getString(COL_PATTERN);

		List<ParsedStatSetter> setters = getParsedStatSetters();

		StatSetterParams params = getParams(parsers);
		StatParser parser = new StatParser(pattern.trim(), setters, params);

		parsers.add(parser);

		getOptionalString(COL_ALIAS)
				.ifPresent(alias -> aliases.computeIfAbsent(alias, x -> new ArrayList<>()).add(parser));
	}

	private List<ParsedStatSetter> getParsedStatSetters() {
		var setters = Stream.of(
						getOptionalString(COL_STAT1),
						getOptionalString(COL_STAT2)
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

	private StatSetterParams getParams(List<StatParser> parsers) {
		StatSetterParams params = new StatSetterParams();

		if (parsers == itemStatParsers) {
			params.setSpecialType(getOptionalString(COL_SPECIAL_TYPE).orElse(null));
			params.setAttributeParser(getOptionalString(COL_SPECIAL_STAT).map(SimpleAttributeParser::new).orElse(null));
			params.setSpecialAmount(getOptionalString(COL_SPECIAL_AMOUNT).orElse(null));
			params.setSpecialDuration(getOptionalString(COL_SPECIAL_DURATION).orElse(null));
			params.setSpecialCd(getOptionalString(COL_SPECIAL_CD).orElse(null));
			params.setSpecialProcChance(getOptionalString(COL_SPECIAL_PROC_CHANCE).orElse(null));
			params.setSpecialProcCd(getOptionalString(COL_SPECIAL_PROC_CD).orElse(null));
			params.setSpecialSpell(getList(COL_SPECIAL_SPELL, SpellId::parse));
		}

		return params;
	}
}
