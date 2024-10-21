package wow.character.repository.impl.parser.character;

import lombok.AllArgsConstructor;
import polszewski.excel.reader.templates.ExcelParser;
import polszewski.excel.reader.templates.ExcelSheetParser;
import wow.character.repository.impl.CharacterTemplateRepositoryImpl;
import wow.commons.repository.pve.PhaseRepository;
import wow.commons.repository.spell.TalentRepository;

import java.io.InputStream;
import java.util.stream.Stream;

/**
 * User: POlszewski
 * Date: 2022-11-30
 */
@AllArgsConstructor
public class CharacterTemplateExcelParser extends ExcelParser {
	private final String xlsFilePath;
	private final CharacterTemplateRepositoryImpl characterTemplateRepository;
	private final TalentRepository talentRepository;
	private final PhaseRepository phaseRepository;

	@Override
	protected InputStream getExcelInputStream() {
		return fromResourcePath(xlsFilePath);
	}

	@Override
	protected Stream<ExcelSheetParser> getSheetParsers() {
		return Stream.of(
				new CharacterTemplateSheetParser("templates", characterTemplateRepository, talentRepository, phaseRepository)
		);
	}
}
