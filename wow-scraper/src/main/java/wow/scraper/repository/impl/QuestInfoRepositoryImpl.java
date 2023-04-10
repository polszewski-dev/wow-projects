package wow.scraper.repository.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import wow.commons.model.pve.GameVersionId;
import wow.scraper.config.ScraperConfig;
import wow.scraper.model.WowheadQuestInfo;
import wow.scraper.repository.QuestInfoRepository;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

/**
 * User: POlszewski
 * Date: 2023-04-11
 */
@Repository
@AllArgsConstructor
public class QuestInfoRepositoryImpl implements QuestInfoRepository {
	private static final ObjectMapper MAPPER = new ObjectMapper();

	private ScraperConfig config;

	@Override
	public boolean hasQuestInfo(GameVersionId gameVersion, int questId) {
		Path path = getPath(gameVersion, questId);
		return Files.exists(path);
	}

	@Override
	public void saveQuestInfo(GameVersionId gameVersion, int questId, WowheadQuestInfo questInfo) throws IOException {
		Path path = getPath(gameVersion, questId);
		path.toFile().getParentFile().mkdirs();
		MAPPER.writeValue(new FileOutputStream(path.toFile()), questInfo);
	}

	@Override
	public Optional<WowheadQuestInfo> getQuestInfo(GameVersionId gameVersion, int questId) throws IOException {
		Path path = getPath(gameVersion, questId);
		if (!Files.exists(path)) {
			return Optional.empty();
		}
		var questInfo = MAPPER.readValue(new FileInputStream(path.toFile()), WowheadQuestInfo.class);
		return Optional.of(questInfo);
	}

	private Path getPath(GameVersionId gameVersion, int questId) {
		return Paths.get(config.getDirectoryPath(), "quests", gameVersion.toString().toLowerCase(), Integer.toString(questId));
	}
}
