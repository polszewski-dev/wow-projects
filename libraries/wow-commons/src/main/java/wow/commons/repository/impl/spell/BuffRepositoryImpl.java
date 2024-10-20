
package wow.commons.repository.impl.spell;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import wow.commons.model.buff.Buff;
import wow.commons.model.buff.BuffId;
import wow.commons.model.buff.BuffIdAndRank;
import wow.commons.model.pve.PhaseId;
import wow.commons.repository.impl.parser.spell.BuffExcelParser;
import wow.commons.repository.spell.BuffRepository;
import wow.commons.util.PhaseMap;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static wow.commons.util.PhaseMap.putForEveryPhase;

/**
 * User: POlszewski
 * Date: 2020-09-28
 */
@Component
@RequiredArgsConstructor
public class BuffRepositoryImpl implements BuffRepository {
	private final PhaseMap<BuffIdAndRank, Buff> buffsById = new PhaseMap<>();

	@Value("${buffs.xls.file.path}")
	private String xlsFilePath;

	@Override
	public List<Buff> getAvailableBuffs(PhaseId phaseId) {
		return buffsById.values(phaseId).stream().toList();
	}

	@Override
	public Optional<Buff> getBuff(BuffId buffId, int rank, PhaseId phaseId) {
		return buffsById.getOptional(phaseId, new BuffIdAndRank(buffId, rank));
	}

	@PostConstruct
	public void init() throws IOException {
		var parser = new BuffExcelParser(xlsFilePath, this);
		parser.readFromXls();
	}

	public void addBuff(Buff buff) {
		putForEveryPhase(buffsById, buff.getId(), buff);
	}
}
