package wow.commons.repository.impl.parser.pve;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import polszewski.excel.reader.templates.ExcelParser;
import polszewski.excel.reader.templates.ExcelSheetParser;
import wow.commons.model.pve.GameVersion;

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
public class GameVersionExcelParser extends ExcelParser {
	@Value("${game.versions.xls.file.path}")
	private final String xlsFilePath;

	@Getter
	private final List<GameVersion> gameVersions = new ArrayList<>();

	@Override
	protected InputStream getExcelInputStream() {
		return fromResourcePath(xlsFilePath);
	}

	@Override
	protected Stream<ExcelSheetParser> getSheetParsers() {
		return Stream.of(
				new GameVersionSheetParser("versions", this)
		);
	}

	void addGameVersion(GameVersion gameVersion) {
		gameVersions.add(gameVersion);
	}
}
