
package wow.commons.repository.impl.spell;

import org.springframework.stereotype.Component;
import wow.commons.model.buff.Buff;
import wow.commons.model.buff.BuffId;
import wow.commons.model.buff.BuffNameRank;
import wow.commons.model.pve.PhaseId;
import wow.commons.repository.impl.parser.spell.BuffExcelParser;
import wow.commons.repository.spell.BuffRepository;
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
public class BuffRepositoryImpl implements BuffRepository {
	private final PhaseMap<BuffId, Buff> buffsById = new PhaseMap<>();
	private final PhaseMap<BuffNameRank, List<Buff>> buffsByNameRank = new PhaseMap<>();

	public BuffRepositoryImpl(BuffExcelParser parser) throws IOException {
		parser.readFromXls();
		parser.getBuffs().forEach(this::addBuff);
	}

	@Override
	public List<Buff> getAvailableBuffs(PhaseId phaseId) {
		return List.copyOf(buffsById.values(phaseId));
	}

	@Override
	public Optional<Buff> getBuff(BuffId buffId, PhaseId phaseId) {
		return buffsById.getOptional(phaseId, buffId);
	}

	@Override
	public List<Buff> getBuff(String name, int rank, PhaseId phaseId) {
		var nameRank = new BuffNameRank(name, rank);

		return buffsByNameRank.getOrDefault(phaseId, nameRank, List.of());
	}

	private void addBuff(Buff buff) {
		putForEveryPhase(buffsById, buff.getId(), buff);
		addEntryForEveryPhase(buffsByNameRank, buff.getNameRank(), buff);
	}
}
