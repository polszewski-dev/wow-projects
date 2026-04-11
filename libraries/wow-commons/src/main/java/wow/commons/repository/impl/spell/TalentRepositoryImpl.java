package wow.commons.repository.impl.spell;

import org.springframework.stereotype.Component;
import wow.commons.model.character.CharacterClassId;
import wow.commons.model.pve.PhaseId;
import wow.commons.model.talent.Talent;
import wow.commons.model.talent.TalentId;
import wow.commons.repository.impl.parser.spell.TalentExcelParser;
import wow.commons.repository.spell.TalentRepository;
import wow.commons.util.GameVersionMap;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static wow.commons.util.GameVersionMap.addEntryForGameVersion;
import static wow.commons.util.GameVersionMap.putForGameVersion;
import static wow.commons.util.TimeMap.compactLists;

/**
 * User: POlszewski
 * Date: 2020-09-28
 */
@Component
public class TalentRepositoryImpl implements TalentRepository {
	private record NameRankKey(CharacterClassId characterClassId, String name, int rank) {}
	private record CalcPositionKey(CharacterClassId characterClassId, int talentCalculatorPosition, int rank) {}

	private final GameVersionMap<TalentId, Talent> talentById = new GameVersionMap<>();
	private final GameVersionMap<CharacterClassId, List<Talent>> talentsByClass = new GameVersionMap<>();
	private final GameVersionMap<NameRankKey, Talent> talentByClassByIdByRank = new GameVersionMap<>();
	private final GameVersionMap<CalcPositionKey, Talent> talentByClassByCalcPosByRank = new GameVersionMap<>();

	public TalentRepositoryImpl(TalentExcelParser parser) throws IOException {
		parser.readFromXls();
		parser.getTalents().forEach(this::addTalent);
		compactLists(talentsByClass);
	}

	@Override
	public List<Talent> getAvailableTalents(CharacterClassId characterClassId, PhaseId phaseId) {
		return talentsByClass.getOptional(phaseId, characterClassId).orElse(List.of()).stream()
				.toList();
	}

	@Override
	public Optional<Talent> getTalent(TalentId talentId, PhaseId phaseId) {
		return talentById.getOptional(phaseId, talentId);
	}

	@Override
	public Optional<Talent> getTalent(CharacterClassId characterClassId, String name, int rank, PhaseId phaseId) {
		var key = new NameRankKey(characterClassId, name, rank);
		return talentByClassByIdByRank.getOptional(phaseId, key);
	}

	@Override
	public Optional<Talent> getTalent(CharacterClassId characterClassId, int talentCalculatorPosition, int rank, PhaseId phaseId) {
		var key = new CalcPositionKey(characterClassId, talentCalculatorPosition, rank);
		return talentByClassByCalcPosByRank.getOptional(phaseId, key);
	}

	private void addTalent(Talent talent) {
		var key1 = new NameRankKey(talent.getCharacterClass(), talent.getName(), talent.getRank());
		var key2 = new CalcPositionKey(talent.getCharacterClass(), talent.getTalentCalculatorPosition(), talent.getRank());

		addEntryForGameVersion(talentsByClass, talent.getCharacterClass(), talent);
		putForGameVersion(talentById, talent.getId(), talent);
		putForGameVersion(talentByClassByIdByRank, key1, talent);
		putForGameVersion(talentByClassByCalcPosByRank, key2, talent);
	}
}
