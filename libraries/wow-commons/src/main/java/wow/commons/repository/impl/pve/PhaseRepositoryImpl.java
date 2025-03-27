package wow.commons.repository.impl.pve;

import org.springframework.stereotype.Component;
import wow.commons.model.pve.Phase;
import wow.commons.model.pve.PhaseId;
import wow.commons.repository.impl.parser.pve.PhaseExcelParser;
import wow.commons.repository.pve.PhaseRepository;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

/**
 * User: POlszewski
 * Date: 2022-11-30
 */
@Component
public class PhaseRepositoryImpl implements PhaseRepository {
	private final Map<PhaseId, Phase> phaseById = new TreeMap<>();

	public PhaseRepositoryImpl(PhaseExcelParser parser) throws IOException {
		parser.readFromXls();
		parser.getPhases().forEach(this::addPhase);
	}

	@Override
	public Optional<Phase> getPhase(PhaseId phaseId) {
		return Optional.ofNullable(phaseById.get(phaseId));
	}

	private void addPhase(Phase phase) {
		phaseById.put(phase.getPhaseId(), phase);
	}
}
