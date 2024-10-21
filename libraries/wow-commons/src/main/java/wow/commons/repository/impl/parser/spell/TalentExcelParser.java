package wow.commons.repository.impl.parser.spell;

import lombok.AllArgsConstructor;
import polszewski.excel.reader.templates.ExcelParser;
import polszewski.excel.reader.templates.ExcelSheetParser;
import wow.commons.repository.impl.spell.SpellRepositoryImpl;
import wow.commons.repository.impl.spell.TalentRepositoryImpl;

import java.io.InputStream;
import java.util.stream.Stream;

import static wow.commons.repository.impl.parser.spell.SpellBaseExcelSheetNames.TALENTS;

/**
 * User: POlszewski
 * Date: 2021-09-25
 */
@AllArgsConstructor
public class TalentExcelParser extends ExcelParser {
	private final String xlsFilePath;
	private final TalentRepositoryImpl talentRepository;
	private final SpellRepositoryImpl spellRepository;

	@Override
	protected InputStream getExcelInputStream() {
		return fromResourcePath(xlsFilePath);
	}

	@Override
	protected Stream<ExcelSheetParser> getSheetParsers() {
		return Stream.of(
				new TalentSheetParser(TALENTS, talentRepository, spellRepository)
		);
	}
}
