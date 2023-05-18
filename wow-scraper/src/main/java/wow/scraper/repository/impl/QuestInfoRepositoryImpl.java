package wow.scraper.repository.impl;

import org.springframework.stereotype.Repository;
import wow.commons.model.pve.GameVersionId;
import wow.scraper.config.ScraperConfig;
import wow.scraper.model.WowheadQuestInfo;
import wow.scraper.repository.QuestInfoRepository;

import java.io.IOException;
import java.util.Optional;

/**
 * User: POlszewski
 * Date: 2023-04-11
 */
@Repository
public class QuestInfoRepositoryImpl implements QuestInfoRepository {
	private final JsonFileRepository<WowheadQuestInfo> repository;

	public QuestInfoRepositoryImpl(ScraperConfig scraperConfig) {
		this.repository = new JsonFileRepository<>(
				scraperConfig,
				WowheadQuestInfo.class
		);
	}

	@Override
	public boolean hasQuestInfo(GameVersionId gameVersion, int questId) {
		return repository.has(getIdParts(gameVersion, questId));
	}

	@Override
	public Optional<WowheadQuestInfo> getQuestInfo(GameVersionId gameVersion, int questId) throws IOException {
		return repository.get(getIdParts(gameVersion, questId));
	}

	@Override
	public void saveQuestInfo(GameVersionId gameVersion, int questId, WowheadQuestInfo questInfo) throws IOException {
		repository.save(getIdParts(gameVersion, questId), questInfo);
	}

	private String[] getIdParts(GameVersionId gameVersion, int questId) {
		return new String[] {
				"quests",
				gameVersion.toString().toLowerCase(),
				Integer.toString(questId)
		};
	}
}
