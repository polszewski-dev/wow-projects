package wow.commons.repository.impl.pve;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import wow.commons.model.pve.Phase;
import wow.commons.model.pve.PhaseId;
import wow.commons.repository.impl.parser.pve.PhaseExcelParser;
import wow.commons.repository.pve.GameVersionRepository;
import wow.commons.repository.pve.PhaseRepository;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

/**
 * User: POlszewski
 * Date: 2022-11-30
 */
@Component
@RequiredArgsConstructor
public class PhaseRepositoryImpl implements PhaseRepository {
	private final Map<PhaseId, Phase> phaseById = new TreeMap<>();

	private final GameVersionRepository gameVersionRepository;

	@Value("${phases.xls.file.path}")
	private String xlsFilePath;

	@Override
	public Optional<Phase> getPhase(PhaseId phaseId) {
		return Optional.ofNullable(phaseById.get(phaseId));
	}

	@PostConstruct
	public void init() throws IOException {
		var parser = new PhaseExcelParser(xlsFilePath, gameVersionRepository, this);
		parser.readFromXls();
	}

	public void addPhase(Phase phase) {
		phaseById.put(phase.getPhaseId(), phase);
	}
}
