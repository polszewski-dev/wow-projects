package wow.commons.repository.impl.pve;

import org.springframework.stereotype.Component;
import wow.commons.model.pve.Faction;
import wow.commons.model.pve.PhaseId;
import wow.commons.repository.impl.parser.pve.FactionExcelParser;
import wow.commons.repository.pve.FactionRepository;
import wow.commons.util.GameVersionMap;

import java.io.IOException;
import java.util.Optional;

/**
 * User: POlszewski
 * Date: 2021-03-14
 */
@Component
public class FactionRepositoryImpl implements FactionRepository {
	private final GameVersionMap<String, Faction> factionByName = new GameVersionMap<>();

	public FactionRepositoryImpl(FactionExcelParser parser) throws IOException {
		parser.readFromXls();
		parser.getFactions().forEach(this::addFaction);
	}

	@Override
	public Optional<Faction> getFaction(String name, PhaseId phaseId) {
		return factionByName.getOptional(phaseId.getGameVersionId(), name);
	}

	private void addFaction(Faction faction) {
		factionByName.put(faction.getGameVersionId(), faction.getName(), faction);
	}
}
