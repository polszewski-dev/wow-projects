package wow.scraper.repository;

import wow.commons.model.pve.GameVersionId;
import wow.scraper.model.WowheadQuestInfo;

import java.util.Optional;

/**
 * User: POlszewski
 * Date: 2023-04-11
 */
public interface QuestInfoRepository {
	Optional<WowheadQuestInfo> getQuestInfo(GameVersionId gameVersion, int questId);
}
