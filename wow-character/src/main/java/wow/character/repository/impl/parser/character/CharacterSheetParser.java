package wow.character.repository.impl.parser.character;

import wow.character.repository.impl.CharacterRepositoryImpl;
import wow.commons.model.pve.GameVersion;
import wow.commons.model.pve.GameVersionId;
import wow.commons.repository.impl.parser.excel.WowExcelSheetParser;

import java.util.List;
import java.util.Set;

/**
 * User: POlszewski
 * Date: 2023-03-31
 */
public abstract class CharacterSheetParser extends WowExcelSheetParser {
	protected final CharacterRepositoryImpl characterRepository;

	private final ExcelColumn colReqVersion = column("req_version");

	protected CharacterSheetParser(String sheetName, CharacterRepositoryImpl characterRepository) {
		super(sheetName);
		this.characterRepository = characterRepository;
	}

	protected List<GameVersion> getVersions() {
		var versionIds = colReqVersion.getSet(GameVersionId::parse);

		if (versionIds.isEmpty()) {
			versionIds = Set.of(GameVersionId.values());
		}

		return versionIds.stream()
				.map(x -> characterRepository.getGameVersion(x).orElseThrow())
				.toList();
	}
}
