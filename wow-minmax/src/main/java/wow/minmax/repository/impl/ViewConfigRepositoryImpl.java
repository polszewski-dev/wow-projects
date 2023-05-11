package wow.minmax.repository.impl;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import wow.commons.model.categorization.PveRole;
import wow.commons.model.character.CharacterClassId;
import wow.commons.model.pve.GameVersionId;
import wow.minmax.model.ViewConfig;
import wow.minmax.repository.ViewConfigRepository;
import wow.minmax.repository.impl.parsers.view.ViewConfigExcelParser;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * User: POlszewski
 * Date: 2023-05-11
 */
@Repository
public class ViewConfigRepositoryImpl implements ViewConfigRepository {
	private final Map<String, ViewConfig> viewConfigByKey = new HashMap<>();

	@Value("${view.config.xls.file.path}")
	private String xlsFilePath;

	@Override
	public Optional<ViewConfig> getViewConfig(CharacterClassId characterClassId, PveRole role, GameVersionId gameVersionId) {
		String key = getKey(characterClassId, role, gameVersionId);
		return Optional.ofNullable(viewConfigByKey.get(key));
	}

	@PostConstruct
	public void init() throws IOException, InvalidFormatException {
		var excelParser = new ViewConfigExcelParser(xlsFilePath, this);
		excelParser.readFromXls();
	}

	public void add(ViewConfig viewConfig) {
		String key = getKey(viewConfig.getCharacterClassId(), viewConfig.getPveRole(), viewConfig.getGameVersionId());
		viewConfigByKey.put(key, viewConfig);
	}

	private static String getKey(CharacterClassId characterClassId, PveRole pveRole, GameVersionId gameVersionId) {
		return characterClassId + "#" + pveRole + "#" + gameVersionId;
	}
}
