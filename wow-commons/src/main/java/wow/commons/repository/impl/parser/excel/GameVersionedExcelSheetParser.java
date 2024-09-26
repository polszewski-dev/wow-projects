package wow.commons.repository.impl.parser.excel;

import wow.commons.model.pve.GameVersion;
import wow.commons.model.pve.GameVersionId;
import wow.commons.repository.pve.GameVersionRepository;

import java.util.List;
import java.util.Set;

/**
 * User: POlszewski
 * Date: 2023-03-31
 */
public abstract class GameVersionedExcelSheetParser extends WowExcelSheetParser {
	protected final GameVersionRepository gameVersionRepository;

	private final ExcelColumn colReqVersion = column("req_version");

	protected GameVersionedExcelSheetParser(String sheetName, GameVersionRepository gameVersionRepository) {
		super(sheetName);
		this.gameVersionRepository = gameVersionRepository;
	}

	protected List<GameVersion> getVersions() {
		var versionIds = colReqVersion.getSet(GameVersionId::parse);

		if (versionIds.isEmpty()) {
			versionIds = Set.of(GameVersionId.values());
		}

		return versionIds.stream()
				.map(x -> gameVersionRepository.getGameVersion(x).orElseThrow())
				.toList();
	}
}
