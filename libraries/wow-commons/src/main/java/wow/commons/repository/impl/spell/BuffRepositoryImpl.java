
package wow.commons.repository.impl.spell;

import org.springframework.stereotype.Component;
import wow.commons.model.buff.Buff;
import wow.commons.model.buff.BuffId;
import wow.commons.model.buff.BuffIdAndRank;
import wow.commons.model.pve.PhaseId;
import wow.commons.repository.impl.parser.spell.BuffExcelParser;
import wow.commons.repository.spell.BuffRepository;
import wow.commons.util.PhaseMap;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import static wow.commons.util.PhaseMap.addEntryForEveryPhase;

/**
 * User: POlszewski
 * Date: 2020-09-28
 */
@Component
public class BuffRepositoryImpl implements BuffRepository {
	private final PhaseMap<BuffIdAndRank, List<Buff>> buffsById = new PhaseMap<>();

	public BuffRepositoryImpl(BuffExcelParser parser) throws IOException {
		parser.readFromXls();
		parser.getBuffs().forEach(this::addBuff);
	}

	@Override
	public List<Buff> getAvailableBuffs(PhaseId phaseId) {
		return buffsById.values(phaseId).stream()
				.flatMap(Collection::stream)
				.toList();
	}

	@Override
	public List<Buff> getBuff(BuffId buffId, int rank, PhaseId phaseId) {
		return buffsById.getOrDefault(phaseId, new BuffIdAndRank(buffId, rank), List.of());
	}

	private void addBuff(Buff buff) {
		addEntryForEveryPhase(buffsById, buff.getId(), buff);
	}
}
