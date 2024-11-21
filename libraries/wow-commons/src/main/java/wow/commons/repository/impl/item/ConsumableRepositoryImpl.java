package wow.commons.repository.impl.item;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import wow.commons.model.item.Consumable;
import wow.commons.model.pve.PhaseId;
import wow.commons.repository.impl.parser.item.ConsumableExcelParser;
import wow.commons.repository.impl.parser.item.SourceParserFactory;
import wow.commons.repository.item.ConsumableRepository;
import wow.commons.repository.spell.SpellRepository;
import wow.commons.util.PhaseMap;

import javax.annotation.PostConstruct;
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
@RequiredArgsConstructor
public class ConsumableRepositoryImpl implements ConsumableRepository {
	private final SourceParserFactory sourceParserFactory;
	private final SpellRepository spellRepository;

	private final PhaseMap<Integer, Consumable> consumableById = new PhaseMap<>();
	private final PhaseMap<String, Consumable> consumableByName = new PhaseMap<>();

	@Value("${consumables.xls.file.path}")
	private String xlsFilePath;

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

	@PostConstruct
	public void init() throws IOException {
		var parser = new ConsumableExcelParser(xlsFilePath, sourceParserFactory, spellRepository, this);
		parser.readFromXls();
	}

	public void addConsumable(Consumable consumable) {
		putForEveryPhase(consumableById, consumable.getId(), consumable);
		putForEveryPhase(consumableByName, consumable.getName(), consumable);
	}
}
