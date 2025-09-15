package wow.commons.model.spell;

import lombok.SneakyThrows;
import polszewski.excel.reader.templates.ExcelParser;
import polszewski.excel.reader.templates.ExcelSheetParser;
import polszewski.excel.reader.templates.NullExcelSheetParser;

import java.io.InputStream;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static wow.commons.model.spell.SpellType.*;
import static wow.commons.repository.impl.parser.excel.CommonColumnNames.NAME;
import static wow.commons.repository.impl.parser.spell.SpellBaseExcelColumnNames.SPELL_TYPE;
import static wow.commons.repository.impl.parser.spell.SpellBaseExcelSheetNames.ABILITIES;

/**
 * User: POlszewski
 * Date: 2020-10-01
 */
public record AbilityId(String name) implements Comparable<AbilityId> {
	public static final AbilityId SHOOT;

	public AbilityId {
		Objects.requireNonNull(name);
	}

	public static AbilityId of(String name) {
		return Objects.requireNonNull(CACHE.get(name));
	}

	public static AbilityId parse(String value) {
		if (value == null) {
			return null;
		}
		return Objects.requireNonNull(tryParse(value));
	}

	public static AbilityId tryParse(String value) {
		if (value == null) {
			return null;
		}
		return CACHE.get(value);
	}

	@Override
	public int compareTo(AbilityId other) {
		return this.name.compareTo(other.name);
	}

	@Override
	public String toString() {
		return name;
	}

	public static Collection<AbilityId> values() {
		return Collections.unmodifiableCollection(CACHE.values());
	}

	private static final Map<String, AbilityId> CACHE = new HashMap<>();

	static {
		populateCache();
		SHOOT = CACHE.get("Shoot");
		Objects.requireNonNull(SHOOT);
	}

	@SneakyThrows
	private static void populateCache() {
		var parser = new AbilityNameExcelParser();

		parser.readFromXls();
	}

	private static class AbilityNameSheetParser extends ExcelSheetParser {
		protected final ExcelColumn colName = column(NAME);
		protected final ExcelColumn colType = column(SPELL_TYPE);

		AbilityNameSheetParser(String sheetName) {
			super(sheetName);
		}

		AbilityNameSheetParser(Pattern sheetNamePattern) {
			super(sheetNamePattern);
		}

		@Override
		protected ExcelColumn getColumnIndicatingOptionalRow() {
			return colName;
		}

		@Override
		protected void readSingleRow() {
			var name = colName.getString();
			var type = colType.getEnum(SpellType::parse, TRIGGERED_SPELL);

			if (type == CLASS_ABILITY || type == RACIAL_ABILITY || type == ACTIVATED_ABILITY) {
				CACHE.computeIfAbsent(name, AbilityId::new);
			}
		}
	}

	private static class AbilityNameExcelParser extends ExcelParser {
		@Override
		protected InputStream getExcelInputStream() {
			return fromResourcePath("/wow/commons/data/spell/spells.xls");
		}

		@Override
		protected Stream<ExcelSheetParser> getSheetParsers() {
			return Stream.of(
					new AbilityNameSheetParser(ABILITIES),
					new AbilityNameSheetParser(Pattern.compile(".+_spells")),
					new NullExcelSheetParser(Pattern.compile(".+"))
			);
		}
	}
}
