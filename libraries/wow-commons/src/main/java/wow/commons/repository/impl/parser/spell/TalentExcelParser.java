package wow.commons.repository.impl.parser.spell;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import polszewski.excel.reader.templates.ExcelParser;
import polszewski.excel.reader.templates.ExcelSheetParser;
import wow.commons.model.talent.Talent;
import wow.commons.repository.impl.spell.SpellRepositoryImpl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static wow.commons.repository.impl.parser.spell.SpellBaseExcelSheetNames.TALENTS;

/**
 * User: POlszewski
 * Date: 2021-09-25
 */
@Component
@Scope("prototype")
@RequiredArgsConstructor
public class TalentExcelParser extends ExcelParser {
	@Value("${talents.xls.file.path}")
	private final String xlsFilePath;

	private final SpellRepositoryImpl spellRepository;

	@Getter
	private final List<Talent> talents = new ArrayList<>();

	@Override
	protected InputStream getExcelInputStream() {
		return fromResourcePath(xlsFilePath);
	}

	@Override
	protected Stream<ExcelSheetParser> getSheetParsers() {
		return Stream.of(
				new TalentSheetParser(TALENTS, spellRepository, this)
		);
	}

	void addTalent(Talent talent) {
		talents.add(talent);
	}
}
