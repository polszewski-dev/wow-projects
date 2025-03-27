package wow.commons.repository.impl.parser.spell;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import polszewski.excel.reader.templates.ExcelParser;
import polszewski.excel.reader.templates.ExcelSheetParser;
import wow.commons.model.buff.Buff;
import wow.commons.repository.spell.SpellRepository;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static wow.commons.repository.impl.parser.spell.SpellBaseExcelSheetNames.BUFFS;

/**
 * User: POlszewski
 * Date: 2021-09-25
 */
@Component
@Scope("prototype")
@RequiredArgsConstructor
public class BuffExcelParser extends ExcelParser {
	@Value("${buffs.xls.file.path}")
	private final String xlsFilePath;

	private final SpellRepository spellRepository;

	@Getter
	private final List<Buff> buffs = new ArrayList<>();

	@Override
	protected InputStream getExcelInputStream() {
		return fromResourcePath(xlsFilePath);
	}

	@Override
	protected Stream<ExcelSheetParser> getSheetParsers() {
		return Stream.of(
				new BuffSheetParser(BUFFS, spellRepository, this)
		);
	}

	void addBuff(Buff buff) {
		buffs.add(buff);
	}
}
