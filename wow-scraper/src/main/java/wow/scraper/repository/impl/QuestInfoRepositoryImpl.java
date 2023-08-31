package wow.scraper.repository.impl;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Repository;
import wow.commons.model.pve.GameVersionId;
import wow.scraper.fetcher.WowheadFetcher;
import wow.scraper.importer.parser.QuestInfoParser;
import wow.scraper.model.WowheadQuestInfo;
import wow.scraper.repository.QuestInfoRepository;

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
	public Optional<WowheadQuestInfo> getQuestInfo(GameVersionId gameVersion, int questId) {
		var map = questsById.computeIfAbsent(gameVersion, x -> new HashMap<>());
		var questInfo = map.computeIfAbsent(questId, x -> getWowheadQuestInfo(gameVersion, questId));

		return Optional.of(questInfo);
	}

	@SneakyThrows
	private WowheadQuestInfo getWowheadQuestInfo(GameVersionId gameVersion, int questId) {
		String questHtml = wowheadFetcher.fetchRaw(gameVersion, "quest=" + questId);
		return new QuestInfoParser(questHtml).parse();
	}
}
