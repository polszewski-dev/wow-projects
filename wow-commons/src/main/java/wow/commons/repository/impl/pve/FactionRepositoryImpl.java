package wow.commons.repository.impl.pve;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import wow.commons.model.pve.Faction;
import wow.commons.model.pve.PhaseId;
import wow.commons.repository.impl.parser.pve.FactionExcelParser;
import wow.commons.repository.pve.FactionRepository;
import wow.commons.util.GameVersionMap;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Optional;

/**
 * User: POlszewski
 * Date: 2021-03-14
 */
@Repository
@RequiredArgsConstructor
public class FactionRepositoryImpl implements FactionRepository {
	private final GameVersionMap<String, Faction> factionByName = new GameVersionMap<>();

	@Value("${factions.xls.file.path}")
	private String xlsFilePath;

	@Override
	public Optional<Faction> getFaction(String name, PhaseId phaseId) {
		return factionByName.getOptional(phaseId.getGameVersionId(), name);
	}

	@PostConstruct
	public void init() throws IOException {
		var pveExcelParser = new FactionExcelParser(xlsFilePath, this);
		pveExcelParser.readFromXls();
	}

	public void addFactionByName(Faction faction) {
		factionByName.put(faction.getTimeRestriction().getGameVersionId(), faction.getName(), faction);
	}
}
