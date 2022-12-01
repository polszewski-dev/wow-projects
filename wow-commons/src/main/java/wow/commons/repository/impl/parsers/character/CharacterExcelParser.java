package wow.commons.repository.impl.parsers.character;

import polszewski.excel.reader.templates.ExcelParser;
import polszewski.excel.reader.templates.ExcelSheetParser;
import wow.commons.repository.impl.CharacterRepositoryImpl;

import java.io.InputStream;
import java.util.stream.Stream;

/**
 * User: POlszewski
 * Date: 2022-11-30
 */
public class CharacterExcelParser  extends ExcelParser {
	private final CharacterRepositoryImpl characterRepository;

	public CharacterExcelParser(CharacterRepositoryImpl characterRepository) {
		this.characterRepository = characterRepository;
	}

	@Override
	protected InputStream getExcelInputStream() {
		return fromResourcePath("/xls/char_data.xls");
	}

	@Override
	protected Stream<ExcelSheetParser> getSheetParsers() {
		return Stream.of(
				new BaseStatsSheetParser("base_stats", characterRepository),
				new CombatRatingsSheetParser("combat_ratings", characterRepository),
				new BuildSheetParser("builds", characterRepository)
		);
	}
}
