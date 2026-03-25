
package wow.commons.repository.impl.spell;

import org.springframework.stereotype.Component;
import wow.commons.model.buff.Buff;
import wow.commons.model.buff.BuffId;
import wow.commons.model.config.Described;
import wow.commons.model.pve.PhaseId;
import wow.commons.repository.impl.parser.spell.BuffExcelParser;
import wow.commons.repository.spell.BuffRepository;
import wow.commons.util.PhaseMap;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static wow.commons.util.PhaseMap.addEntryForEveryPhase;
import static wow.commons.util.PhaseMap.putForEveryPhase;

/**
 * User: POlszewski
 * Date: 2020-09-28
 */
@Component
public class BuffRepositoryImpl implements BuffRepository {
	private final PhaseMap<BuffId, Buff> buffsById = new PhaseMap<>();
	private final PhaseMap<String, List<Buff>> buffsByNameRank = new PhaseMap<>();

	public BuffRepositoryImpl(BuffExcelParser parser) throws IOException {
		parser.readFromXls();
		parser.getBuffs().forEach(this::addBuff);
	}

	@Override
	public List<Buff> getAvailableBuffs(PhaseId phaseId, Predicate<Buff> predicate) {
		return buffsById.values(phaseId).stream()
				.filter(predicate)
				.collect(groupingBy(Described::getName, LinkedHashMap::new, toList()))
				.values()
				.stream()
				.map(x -> x.stream().max(comparing(Buff::getId)))
				.flatMap(Optional::stream)
				.toList();
	}

	@Override
	public Optional<Buff> getBuff(BuffId buffId, PhaseId phaseId) {
		return buffsById.getOptional(phaseId, buffId);
	}

	@Override
	public List<Buff> getBuff(String name, PhaseId phaseId) {
		return buffsByNameRank.getOrDefault(phaseId, name, List.of());
	}

	private void addBuff(Buff buff) {
		putForEveryPhase(buffsById, buff.getId(), buff);
		addEntryForEveryPhase(buffsByNameRank, buff.getName(), buff);
	}
}
