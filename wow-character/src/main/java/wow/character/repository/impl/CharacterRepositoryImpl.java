package wow.character.repository.impl;

import lombok.RequiredArgsConstructor;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import wow.character.model.build.BuildId;
import wow.character.model.build.BuildTemplate;
import wow.character.model.character.BaseStatInfo;
import wow.character.model.character.CombatRatingInfo;
import wow.character.model.character.GameVersion;
import wow.character.model.character.Phase;
import wow.character.repository.CharacterRepository;
import wow.character.repository.impl.parsers.character.CharacterExcelParser;
import wow.commons.model.character.CharacterClassId;
import wow.commons.model.pve.GameVersionId;
import wow.commons.model.pve.PhaseId;
import wow.commons.repository.impl.ExcelRepository;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.*;

/**
 * User: POlszewski
 * Date: 2022-11-30
 */
@Repository
@RequiredArgsConstructor
public class CharacterRepositoryImpl extends ExcelRepository implements CharacterRepository {
	private final Map<GameVersionId, GameVersion> gameVersionById = new TreeMap<>();
	private final Map<PhaseId, Phase> phaseById = new TreeMap<>();
	private final Map<String, List<BaseStatInfo>> baseStatInfos = new HashMap<>();
	private final Map<String, List<CombatRatingInfo>> combatRatingInfos = new HashMap<>();
	private final Map<String, List<BuildTemplate>> buildTemplateByKey = new HashMap<>();

	@Value("${character.xls.file.path}")
	private String xlsFilePath;

	@Override
	public Optional<GameVersion> getGameVersion(GameVersionId gameVersionId) {
		return Optional.ofNullable(gameVersionById.get(gameVersionId));
	}

	@Override
	public Optional<Phase> getPhase(PhaseId phaseId) {
		return Optional.ofNullable(phaseById.get(phaseId));
	}

	@Override
	public Optional<BuildTemplate> getBuildTemplate(BuildId buildId, CharacterClassId characterClassId, int level, PhaseId phaseId) {
		String key = getBuildTemplateKey(buildId, characterClassId, level);
		return getUnique(buildTemplateByKey, key, phaseId);
	}

	@PostConstruct
	public void init() throws IOException, InvalidFormatException {
		var excelParser = new CharacterExcelParser(xlsFilePath, this);
		excelParser.readFromXls();
	}

	public void addGameVersion(GameVersion gameVersion) {
		gameVersionById.put(gameVersion.getGameVersionId(), gameVersion);
	}

	public void addPhase(Phase phase) {
		phaseById.put(phase.getPhaseId(), phase);
	}

	public void addBuildTemplate(BuildTemplate buildTemplate) {
		String key = getBuildTemplateKey(buildTemplate.getBuildId(), buildTemplate.getCharacterClassId(), buildTemplate.getLevel());
		addEntry(buildTemplateByKey, key, buildTemplate);
	}

	private static String getBuildTemplateKey(BuildId buildId, CharacterClassId characterClassId, int level) {
		return buildId + "#" + characterClassId + "#" + level;
	}
}
