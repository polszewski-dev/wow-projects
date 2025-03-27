package wow.scraper.repository.impl.excel.stat;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import polszewski.excel.reader.templates.ExcelParser;
import polszewski.excel.reader.templates.ExcelSheetParser;
import wow.scraper.parser.stat.StatPattern;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static wow.scraper.parser.scraper.ScraperPattern.assertNoDuplicates;

/**
 * User: POlszewski
 * Date: 2022-10-13
 */
@Component
@Scope("prototype")
@RequiredArgsConstructor
public class StatPatternExcelParser extends ExcelParser {
	@Value("${stat.parsers.xls.file.path}")
	private final String xlsFilePath;

	private final List<StatPattern> itemStatPatterns = new ArrayList<>();
	private final List<StatPattern> enchantStatPatterns = new ArrayList<>();
	private final List<StatPattern> gemStatPatterns = new ArrayList<>();
	private final List<StatPattern> socketBonusStatPatterns = new ArrayList<>();

	@Override
	protected InputStream getExcelInputStream() {
		return fromResourcePath(xlsFilePath);
	}

	@Override
	protected Stream<ExcelSheetParser> getSheetParsers() {
		return Stream.of(
				new StatPatternSheetParser("item", itemStatPatterns),
				new StatPatternSheetParser("enchant", enchantStatPatterns),
				new StatPatternSheetParser("gem", gemStatPatterns),
				new StatPatternSheetParser("socket", socketBonusStatPatterns)
		);
	}

	public List<StatPattern> getItemStatPatterns() {
		assertNoDuplicates(itemStatPatterns);
		return itemStatPatterns;
	}

	public List<StatPattern> getEnchantStatPatterns() {
		assertNoDuplicates(enchantStatPatterns);
		return enchantStatPatterns;
	}

	public List<StatPattern> getGemStatPatterns() {
		assertNoDuplicates(gemStatPatterns);
		return gemStatPatterns;
	}

	public List<StatPattern> getSocketBonusStatPatterns() {
		assertNoDuplicates(socketBonusStatPatterns);
		return socketBonusStatPatterns;
	}
}
