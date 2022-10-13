package wow.commons.repository.impl.parsers;

import wow.commons.model.spells.SpellId;
import wow.commons.repository.impl.parsers.setters.*;
import wow.commons.util.ExcelParser;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
				new SheetReader("item", () -> readParsers(itemStatParsers), COL_PATTERN),
				new SheetReader("item2", () -> readParsers(itemStatParsers), COL_PATTERN),
				new SheetReader("item3", () -> readParsers(itemStatParsers), COL_PATTERN),
				new SheetReader("gem", () -> readParsers(gemStatParsers), COL_PATTERN),
				new SheetReader("socket", () -> readParsers(socketBonusStatParsers), COL_PATTERN)
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
	private final ExcelColumn COL_SPECIAL_SPELL = column("special:spell");

	private void readParsers(List<StatParser> parsers) {
		var pattern = COL_PATTERN.getString();

		List<ParsedStatSetter> setters = getParsedStatSetters();

		StatSetterParams params = getParams(parsers);
		StatParser parser = new StatParser(pattern.trim(), setters, params);

		parsers.add(parser);

		String alias = COL_ALIAS.getString(null);

		if (alias != null) {
			aliases.computeIfAbsent(alias, x -> new ArrayList<>()).add(parser);
		}
	}

	private List<ParsedStatSetter> getParsedStatSetters() {
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
			params.setSpecialType(COL_SPECIAL_TYPE.getString(null));
			params.setAttributeParser(COL_SPECIAL_STAT.getEnum(SimpleAttributeParser::new, null));
			params.setSpecialAmount(COL_SPECIAL_AMOUNT.getString(null));
			params.setSpecialDuration(COL_SPECIAL_DURATION.getString(null));
			params.setSpecialCd(COL_SPECIAL_CD.getString(null));
			params.setSpecialProcChance(COL_SPECIAL_PROC_CHANCE.getString(null));
			params.setSpecialProcCd(COL_SPECIAL_PROC_CD.getString(null));
			params.setSpecialSpell(COL_SPECIAL_SPELL.getList(SpellId::parse));
		}

		return params;
	}
}
