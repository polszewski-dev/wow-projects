package wow.scraper.repository;

import wow.commons.model.pve.GameVersionId;
import wow.scraper.model.WowheadQuestInfo;

import java.io.IOException;
import java.util.Optional;

/**
 * User: POlszewski
 * Date: 2023-04-11
 */
public interface QuestInfoRepository {
	boolean hasQuestInfo(GameVersionId gameVersion, int questId);

	void saveQuestInfo(GameVersionId gameVersion, int questId, WowheadQuestInfo questInfo) throws IOException;

	Optional<WowheadQuestInfo> getQuestInfo(GameVersionId gameVersion, int questId) throws IOException;
}
