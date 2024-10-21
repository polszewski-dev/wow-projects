package wow.character.repository.impl.parser.character;

import lombok.AllArgsConstructor;
import polszewski.excel.reader.templates.ExcelParser;
import polszewski.excel.reader.templates.ExcelSheetParser;
import wow.character.repository.impl.CombatRatingInfoRepositoryImpl;
import wow.commons.repository.pve.GameVersionRepository;

import java.io.InputStream;
import java.util.stream.Stream;

/**
 * User: POlszewski
 * Date: 2022-11-30
 */
@AllArgsConstructor
public class CombatRatingInfoExcelParser extends ExcelParser {
	private final String xlsFilePath;
	private final GameVersionRepository gameVersionRepository;
	private final CombatRatingInfoRepositoryImpl combatRatingInfoRepository;

	@Override
	protected InputStream getExcelInputStream() {
		return fromResourcePath(xlsFilePath);
	}

	@Override
	protected Stream<ExcelSheetParser> getSheetParsers() {
		return Stream.of(
				new CombatRatingInfoSheetParser("combat_ratings", gameVersionRepository, combatRatingInfoRepository)
		);
	}
}
