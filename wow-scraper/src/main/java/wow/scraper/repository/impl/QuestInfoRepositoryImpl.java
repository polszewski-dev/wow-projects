package wow.scraper.repository.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import wow.commons.model.pve.GameVersionId;
import wow.scraper.fetchers.WowheadFetcher;
import wow.scraper.importers.parsers.QuestInfoParser;
import wow.scraper.model.WowheadQuestInfo;
import wow.scraper.repository.QuestInfoRepository;

import java.io.IOException;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * User: POlszewski
 * Date: 2023-04-11
 */
@Repository
@AllArgsConstructor
public class QuestInfoRepositoryImpl implements QuestInfoRepository {
	private final WowheadFetcher wowheadFetcher;

	private final Map<GameVersionId, Map<Integer, WowheadQuestInfo>> questsById = new EnumMap<>(GameVersionId.class);

	@Override
	public Optional<WowheadQuestInfo> getQuestInfo(GameVersionId gameVersion, int questId) throws IOException {
		var map = questsById.computeIfAbsent(gameVersion, x -> new HashMap<>());
		WowheadQuestInfo questInfo = map.get(questId);

		if (questInfo == null) {
			String questHtml = wowheadFetcher.fetchRaw(gameVersion, "quest=" + questId);
			questInfo = new QuestInfoParser(questHtml).parse();

			map.put(questId, questInfo);
		}

		return Optional.of(questInfo);
	}
}
