package wow.commons.repository.impl.item;

import org.springframework.stereotype.Component;
import wow.commons.model.item.Gem;
import wow.commons.model.item.SocketType;
import wow.commons.model.pve.PhaseId;
import wow.commons.repository.impl.parser.item.GemExcelParser;
import wow.commons.repository.item.GemRepository;
import wow.commons.util.CollectionUtil;
import wow.commons.util.PhaseMap;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static wow.commons.util.PhaseMap.addEntryForEveryPhase;
import static wow.commons.util.PhaseMap.putForEveryPhase;

/**
 * User: POlszewski
 * Date: 2021-03-02
 */
@Component
public class GemRepositoryImpl implements GemRepository {
	private final PhaseMap<Integer, Gem> gemById = new PhaseMap<>();
	private final PhaseMap<String, List<Gem>> gemByName = new PhaseMap<>();
	private final PhaseMap<SocketType, List<Gem>> gemBySocketType = new PhaseMap<>();

	public GemRepositoryImpl(GemExcelParser parser) throws IOException {
		parser.readFromXls();
		parser.getGems().forEach(this::addGem);
	}

	@Override
	public Optional<Gem> getGem(int gemId, PhaseId phaseId) {
		return gemById.getOptional(phaseId, gemId);
	}

	@Override
	public Optional<Gem> getGem(String name, PhaseId phaseId) {
		return gemByName.getOptional(phaseId, name)
				.flatMap(CollectionUtil::getUniqueResult);
	}

	@Override
	public List<Gem> getGems(SocketType socketType, PhaseId phaseId) {
		return gemBySocketType.getOptional(phaseId, socketType)
				.orElse(List.of());
	}

	private void addGem(Gem gem) {
		putForEveryPhase(gemById, gem.getId(), gem);
		addEntryForEveryPhase(gemByName, gem.getName(), gem);
		for (var socketType : SocketType.values()) {
			if (socketType.accepts(gem.getColor())) {
				addEntryForEveryPhase(gemBySocketType, socketType, gem);
			}
		}
	}
}
