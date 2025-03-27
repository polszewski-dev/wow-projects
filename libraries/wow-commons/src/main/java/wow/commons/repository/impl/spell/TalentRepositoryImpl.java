package wow.commons.repository.impl.spell;

import org.springframework.stereotype.Component;
import wow.commons.model.character.CharacterClassId;
import wow.commons.model.pve.PhaseId;
import wow.commons.model.talent.Talent;
import wow.commons.model.talent.TalentId;
import wow.commons.repository.impl.parser.spell.TalentExcelParser;
import wow.commons.repository.spell.TalentRepository;
import wow.commons.util.PhaseMap;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static wow.commons.util.PhaseMap.addEntryForEveryPhase;
import static wow.commons.util.PhaseMap.putForEveryPhase;

/**
 * User: POlszewski
 * Date: 2020-09-28
 */
@Component
public class TalentRepositoryImpl implements TalentRepository {
	private record TalentIdAndRankKey(CharacterClassId characterClassId, TalentId talentId, int rank) {}
	private record CalcPositionKey(CharacterClassId characterClassId, int talentCalculatorPosition, int rank) {}

	private final PhaseMap<CharacterClassId, List<Talent>> talentsByClass = new PhaseMap<>();
	private final PhaseMap<TalentIdAndRankKey, Talent> talentByClassByIdByRank = new PhaseMap<>();
	private final PhaseMap<CalcPositionKey, Talent> talentByClassByCalcPosByRank = new PhaseMap<>();

	public TalentRepositoryImpl(TalentExcelParser parser) throws IOException {
		parser.readFromXls();
		parser.getTalents().forEach(this::addTalent);
	}

	@Override
	public List<Talent> getAvailableTalents(CharacterClassId characterClassId, PhaseId phaseId) {
		return talentsByClass.getOptional(phaseId, characterClassId).orElse(List.of()).stream()
				.toList();
	}

	@Override
	public Optional<Talent> getTalent(CharacterClassId characterClassId, TalentId talentId, int rank, PhaseId phaseId) {
		var key = new TalentIdAndRankKey(characterClassId, talentId, rank);
		return talentByClassByIdByRank.getOptional(phaseId, key);
	}

	@Override
	public Optional<Talent> getTalent(CharacterClassId characterClassId, int talentCalculatorPosition, int rank, PhaseId phaseId) {
		var key = new CalcPositionKey(characterClassId, talentCalculatorPosition, rank);
		return talentByClassByCalcPosByRank.getOptional(phaseId, key);
	}

	private void addTalent(Talent talent) {
		var key1 = new TalentIdAndRankKey(talent.getCharacterClass(), talent.getTalentId(), talent.getRank());
		var key2 = new CalcPositionKey(talent.getCharacterClass(), talent.getTalentCalculatorPosition(), talent.getRank());

		addEntryForEveryPhase(talentsByClass, talent.getCharacterClass(), talent);
		putForEveryPhase(talentByClassByIdByRank, key1, talent);
		putForEveryPhase(talentByClassByCalcPosByRank, key2, talent);
	}
}
