package wow.character.repository.impl.parser.character;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import polszewski.excel.reader.templates.ExcelParser;
import polszewski.excel.reader.templates.ExcelSheetParser;
import wow.character.model.character.CharacterTemplate;
import wow.commons.repository.spell.TalentRepository;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * User: POlszewski
 * Date: 2022-11-30
 */
@Component
@Scope("prototype")
@RequiredArgsConstructor
public class CharacterTemplateExcelParser extends ExcelParser {
	@Value("${character.templates.xls.file.path}")
	private final String xlsFilePath;

	private final TalentRepository talentRepository;

	@Getter
	private final List<CharacterTemplate> characterTemplates = new ArrayList<>();

	@Override
	protected InputStream getExcelInputStream() {
		return fromResourcePath(xlsFilePath);
	}

	@Override
	protected Stream<ExcelSheetParser> getSheetParsers() {
		return Stream.of(
				new CharacterTemplateSheetParser("templates", talentRepository, this)
		);
	}

	void addCharacterTemplate(CharacterTemplate characterTemplate) {
		characterTemplates.add(characterTemplate);
	}
}
