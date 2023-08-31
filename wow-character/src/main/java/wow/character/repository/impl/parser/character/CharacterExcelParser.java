package wow.character.repository.impl.parser.character;

import lombok.AllArgsConstructor;
import polszewski.excel.reader.templates.ExcelParser;
import polszewski.excel.reader.templates.ExcelSheetParser;
import wow.character.repository.impl.CharacterRepositoryImpl;

import java.io.InputStream;
import java.util.stream.Stream;

/**
 * User: POlszewski
 * Date: 2022-11-30
 */
@AllArgsConstructor
public class CharacterExcelParser  extends ExcelParser {
	private final String xlsFilePath;
	private final CharacterRepositoryImpl characterRepository;

	@Override
	protected InputStream getExcelInputStream() {
		return fromResourcePath(xlsFilePath);
	}

	@Override
	protected Stream<ExcelSheetParser> getSheetParsers() {
		return Stream.of(
				new GameVersionSheetParser("versions", characterRepository),
				new PhaseSheetParser("phases", characterRepository),
				new CharacterClassSheetParser("classes", characterRepository),
				new RaceSheetParser("races", characterRepository),
				new ProfessionSheetParser("professions", characterRepository),
				new ProfessionSpecSheetParser("profession_specs", characterRepository),
				new ProfessionProficiencySheetParser("profession_proficiencies", characterRepository),
				new PetSheetParser("pets", characterRepository),
				new BaseStatsSheetParser("base_stats", characterRepository),
				new CombatRatingsSheetParser("combat_ratings", characterRepository),
				new TemplateSheetParser("templates", characterRepository)
		);
	}
}
