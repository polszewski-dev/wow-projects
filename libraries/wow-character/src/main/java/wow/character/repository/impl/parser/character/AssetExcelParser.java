package wow.character.repository.impl.parser.character;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import polszewski.excel.reader.templates.ExcelParser;
import polszewski.excel.reader.templates.ExcelSheetParser;
import wow.character.model.asset.Asset;

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
public class AssetExcelParser extends ExcelParser {
	@Value("${assets.xls.file.path}")
	private final String xlsFilePath;

	@Getter
	private final List<Asset> assets = new ArrayList<>();

	@Override
	protected InputStream getExcelInputStream() {
		return fromResourcePath(xlsFilePath);
	}

	@Override
	protected Stream<ExcelSheetParser> getSheetParsers() {
		return Stream.of(
				new AssetSheetParser("assets", this)
		);
	}

	void addAsset(Asset asset) {
		assets.add(asset);
	}
}
