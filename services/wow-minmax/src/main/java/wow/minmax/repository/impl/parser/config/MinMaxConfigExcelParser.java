package wow.minmax.repository.impl.parser.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import polszewski.excel.reader.templates.ExcelParser;
import polszewski.excel.reader.templates.ExcelSheetParser;
import wow.minmax.model.config.CharacterFeatureConfig;
import wow.minmax.model.config.FindUpgradesConfig;
import wow.minmax.model.config.ViewConfig;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * User: POlszewski
 * Date: 2023-05-11
 */
@Component
@Scope("prototype")
@RequiredArgsConstructor
public class MinMaxConfigExcelParser extends ExcelParser {
	@Value("${config.xls.file.path}")
	private final String xlsFilePath;

	@Getter
	private final List<CharacterFeatureConfig> characterFeatureConfigs = new ArrayList<>();
	@Getter
	private final List<FindUpgradesConfig> findUpgradesConfigs = new ArrayList<>();
	@Getter
	private final List<ViewConfig> viewConfigs = new ArrayList<>();

	@Override
	protected InputStream getExcelInputStream() {
		return fromResourcePath(xlsFilePath);
	}

	@Override
	protected Stream<ExcelSheetParser> getSheetParsers() {
		return Stream.of(
				new ViewConfigSheetParser("view", this),
				new CharacterFeatureConfigSheetParser("features", this),
				new FindUpgradesConfigSheetParser("find_upgrades", this)
		);
	}

	void add(CharacterFeatureConfig characterFeatureConfig) {
		characterFeatureConfigs.add(characterFeatureConfig);
	}

	void add(FindUpgradesConfig config) {
		findUpgradesConfigs.add(config);
	}

	void add(ViewConfig viewConfig) {
		viewConfigs.add(viewConfig);
	}
}
