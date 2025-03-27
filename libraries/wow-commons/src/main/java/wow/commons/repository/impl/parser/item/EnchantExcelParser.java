package wow.commons.repository.impl.parser.item;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import polszewski.excel.reader.templates.ExcelParser;
import polszewski.excel.reader.templates.ExcelSheetParser;
import wow.commons.model.item.Enchant;
import wow.commons.repository.spell.SpellRepository;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static wow.commons.repository.impl.parser.item.ItemBaseExcelSheetNames.ENCHANT;

/**
 * User: POlszewski
 * Date: 2022-10-30
 */
@Component
@Scope("prototype")
@RequiredArgsConstructor
public class EnchantExcelParser extends ExcelParser {
	@Value("${enchants.xls.file.path}")
	private final String xlsFilePath;

	private final SourceParserFactory sourceParserFactory;
	private final SpellRepository spellRepository;

	@Getter
	private final List<Enchant> enchants = new ArrayList<>();

	@Override
	protected InputStream getExcelInputStream() {
		return fromResourcePath(xlsFilePath);
	}

	@Override
	protected Stream<ExcelSheetParser> getSheetParsers() {
		return Stream.of(
				new EnchantSheetParser(ENCHANT, sourceParserFactory, spellRepository, this)
		);
	}

	void addEnchant(Enchant enchant) {
		enchants.add(enchant);
	}
}
