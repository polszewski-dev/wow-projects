package wow.commons.repository.impl.parser.item;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import polszewski.excel.reader.templates.ExcelParser;
import polszewski.excel.reader.templates.ExcelSheetParser;
import wow.commons.model.item.Consumable;
import wow.commons.repository.spell.SpellRepository;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static wow.commons.repository.impl.parser.item.ItemBaseExcelSheetNames.CONSUMABLE;

/**
 * User: POlszewski
 * Date: 2022-10-30
 */
@Component
@Scope("prototype")
@RequiredArgsConstructor
public class ConsumableExcelParser extends ExcelParser {
	@Value("${consumables.xls.file.path}")
	private final String xlsFilePath;

	private final ItemSourceParserFactory itemSourceParserFactory;
	private final SpellRepository spellRepository;

	@Getter
	private final List<Consumable> consumables = new ArrayList<>();

	@Override
	protected InputStream getExcelInputStream() {
		return fromResourcePath(xlsFilePath);
	}

	@Override
	protected Stream<ExcelSheetParser> getSheetParsers() {
		return Stream.of(
				new ConsumableSheetParser(CONSUMABLE, itemSourceParserFactory, spellRepository, this)
		);
	}

	void addConsumable(Consumable consumable) {
		consumables.add(consumable);
	}
}
