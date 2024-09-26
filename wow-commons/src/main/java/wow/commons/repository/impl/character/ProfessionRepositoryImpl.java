package wow.commons.repository.impl.character;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import wow.commons.model.profession.Profession;
import wow.commons.model.profession.ProfessionId;
import wow.commons.model.profession.ProfessionProficiency;
import wow.commons.model.profession.ProfessionProficiencyId;
import wow.commons.model.pve.GameVersionId;
import wow.commons.repository.character.ProfessionRepository;
import wow.commons.repository.impl.parser.character.ProfessionExcelParser;
import wow.commons.repository.pve.GameVersionRepository;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Optional;

/**
 * User: POlszewski
 * Date: 27.09.2024
 */
@Repository
@RequiredArgsConstructor
public class ProfessionRepositoryImpl implements ProfessionRepository {
	private final GameVersionRepository gameVersionRepository;

	@Override
	public Optional<Profession> getProfession(ProfessionId professionId, GameVersionId gameVersionId) {
		var gameVersion = gameVersionRepository.getGameVersion(gameVersionId).orElseThrow();

		return gameVersion.getProfession(professionId);
	}

	@Override
	public Optional<ProfessionProficiency> getProficiency(ProfessionProficiencyId professionProficiencyId, GameVersionId gameVersionId) {
		var gameVersion = gameVersionRepository.getGameVersion(gameVersionId).orElseThrow();

		return gameVersion.getProficiency(professionProficiencyId);
	}

	@Value("${professions.xls.file.path}")
	private String xlsFilePath;

	@PostConstruct
	public void init() throws IOException {
		var parser = new ProfessionExcelParser(xlsFilePath, gameVersionRepository);
		parser.readFromXls();
	}
}
