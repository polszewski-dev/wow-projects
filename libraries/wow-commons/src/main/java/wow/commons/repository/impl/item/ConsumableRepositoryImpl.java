package wow.commons.repository.impl.item;

import org.springframework.stereotype.Component;
import wow.commons.model.item.Consumable;
import wow.commons.model.pve.PhaseId;
import wow.commons.repository.impl.parser.item.ConsumableExcelParser;
import wow.commons.repository.item.ConsumableRepository;
import wow.commons.util.PhaseMap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static wow.commons.util.PhaseMap.putForEveryPhase;

/**
 * User: POlszewski
 * Date: 2021-03-02
 */
@Component
public class ConsumableRepositoryImpl implements ConsumableRepository {
	private final PhaseMap<Integer, Consumable> consumableById = new PhaseMap<>();
	private final PhaseMap<String, Consumable> consumableByName = new PhaseMap<>();

	public ConsumableRepositoryImpl(ConsumableExcelParser parser) throws IOException {
		parser.readFromXls();
		parser.getConsumables().forEach(this::addConsumable);
	}

	public Optional<Consumable> getConsumable(int consumableId, PhaseId phaseId) {
		return consumableById.getOptional(phaseId, consumableId);
	}

	public Optional<Consumable> getConsumable(String name, PhaseId phaseId) {
		return consumableByName.getOptional(phaseId, name);
	}

	@Override
	public List<Consumable> getAvailableConsumables(PhaseId phaseId) {
		return new ArrayList<>(consumableById.values(phaseId));
	}

	private void addConsumable(Consumable consumable) {
		putForEveryPhase(consumableById, consumable.getId(), consumable);
		putForEveryPhase(consumableByName, consumable.getName(), consumable);
	}
}
