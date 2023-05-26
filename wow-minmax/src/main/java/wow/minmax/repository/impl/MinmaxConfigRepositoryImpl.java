package wow.minmax.repository.impl;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import wow.commons.model.categorization.PveRole;
import wow.commons.model.character.CharacterClassId;
import wow.commons.model.pve.GameVersionId;
import wow.minmax.model.config.FindUpgradesConfig;
import wow.minmax.model.config.ViewConfig;
import wow.minmax.repository.MinmaxConfigRepository;
import wow.minmax.repository.impl.parsers.config.MinMaxConfigExcelParser;

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
public class MinmaxConfigRepositoryImpl implements MinmaxConfigRepository {
	private final Map<String, ViewConfig> viewConfigByKey = new HashMap<>();
	private final Map<String, FindUpgradesConfig> findUpgradesConfigByKey = new HashMap<>();

	@Value("${config.xls.file.path}")
	private String xlsFilePath;

	@Override
	public Optional<ViewConfig> getViewConfig(CharacterClassId characterClassId, PveRole role, GameVersionId gameVersionId) {
		String key = getKey(characterClassId, role, gameVersionId);
		return Optional.ofNullable(viewConfigByKey.get(key));
	}

	@Override
	public Optional<FindUpgradesConfig> getFindUpgradesConfig(CharacterClassId characterClassId, PveRole role, GameVersionId gameVersionId) {
		String key = getKey(characterClassId, role, gameVersionId);
		return Optional.ofNullable(findUpgradesConfigByKey.get(key));
	}

	@PostConstruct
	public void init() throws IOException, InvalidFormatException {
		var excelParser = new MinMaxConfigExcelParser(xlsFilePath, this);
		excelParser.readFromXls();
	}

	public void add(ViewConfig config) {
		String key = getKey(config.getCharacterClassId(), config.getPveRole(), config.getGameVersionId());
		viewConfigByKey.put(key, config);
	}

	public void add(FindUpgradesConfig config) {
		String key = getKey(config.getCharacterClassId(), config.getPveRole(), config.getGameVersionId());
		findUpgradesConfigByKey.put(key, config);
	}

	private static String getKey(CharacterClassId characterClassId, PveRole pveRole, GameVersionId gameVersionId) {
		return characterClassId + "#" + pveRole + "#" + gameVersionId;
	}
}
