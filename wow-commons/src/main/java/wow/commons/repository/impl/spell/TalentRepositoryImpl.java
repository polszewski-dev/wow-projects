package wow.commons.repository.impl.spell;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import wow.commons.model.character.CharacterClassId;
import wow.commons.model.pve.PhaseId;
import wow.commons.model.talent.Talent;
import wow.commons.model.talent.TalentId;
import wow.commons.repository.impl.parser.spell.TalentExcelParser;
import wow.commons.repository.spell.TalentRepository;
import wow.commons.util.PhaseMap;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static wow.commons.util.PhaseMap.addEntryForEveryPhase;
import static wow.commons.util.PhaseMap.putForEveryPhase;

/**
 * User: POlszewski
 * Date: 2020-09-28
 */
@Repository
@RequiredArgsConstructor
public class TalentRepositoryImpl implements TalentRepository {
	private final PhaseMap<CharacterClassId, List<Talent>> talentsByClass = new PhaseMap<>();
	private final PhaseMap<String, Talent> talentByClassByIdByRank = new PhaseMap<>();
	private final PhaseMap<String, Talent> talentByClassByCalcPosByRank = new PhaseMap<>();

	private final SpellRepositoryImpl spellRepository;

	@Value("${talents.xls.file.path}")
	private String xlsFilePath;

	@Override
	public List<Talent> getAvailableTalents(CharacterClassId characterClassId, PhaseId phaseId) {
		return talentsByClass.getOptional(phaseId, characterClassId).orElse(List.of()).stream()
				.toList();
	}

	@Override
	public Optional<Talent> getTalent(CharacterClassId characterClassId, TalentId talentId, int rank, PhaseId phaseId) {
		String key = getTalentKey(characterClassId, talentId, rank);

		return talentByClassByIdByRank.getOptional(phaseId, key);
	}

	@Override
	public Optional<Talent> getTalent(CharacterClassId characterClassId, int talentCalculatorPosition, int rank, PhaseId phaseId) {
		String key = getTalentKey(characterClassId, talentCalculatorPosition, rank);

		return talentByClassByCalcPosByRank.getOptional(phaseId, key);
	}

	@PostConstruct
	public void init() throws IOException {
		var parser = new TalentExcelParser(xlsFilePath, this, spellRepository);
		parser.readFromXls();
	}

	private String getTalentKey(CharacterClassId characterClassId, TalentId talentId, int rank) {
		return characterClassId + "#" + talentId + "#" + rank;
	}

	private String getTalentKey(CharacterClassId characterClassId, int talentCalculatorPosition, int rank) {
		return characterClassId + "#" + talentCalculatorPosition + "#" + rank;
	}

	public void addTalent(Talent talent) {
		String key1 = getTalentKey(talent.getCharacterClass(), talent.getTalentId(), talent.getRank());
		String key2 = getTalentKey(talent.getCharacterClass(), talent.getTalentCalculatorPosition(), talent.getRank());

		addEntryForEveryPhase(talentsByClass, talent.getCharacterClass(), talent);
		putForEveryPhase(talentByClassByIdByRank, key1, talent);
		putForEveryPhase(talentByClassByCalcPosByRank, key2, talent);
	}
}
