package wow.commons.repository.impl.character;

import org.springframework.stereotype.Component;
import wow.commons.model.profession.Profession;
import wow.commons.model.profession.ProfessionId;
import wow.commons.model.profession.ProfessionProficiency;
import wow.commons.model.profession.ProfessionProficiencyId;
import wow.commons.model.pve.GameVersionId;
import wow.commons.repository.character.ProfessionRepository;
import wow.commons.repository.impl.parser.character.ProfessionExcelParser;
import wow.commons.repository.pve.GameVersionRepository;

import java.io.IOException;
import java.util.Optional;

/**
 * User: POlszewski
 * Date: 27.09.2024
 */
@Component
public class ProfessionRepositoryImpl implements ProfessionRepository {
	private final GameVersionRepository gameVersionRepository;

	public ProfessionRepositoryImpl(GameVersionRepository gameVersionRepository, ProfessionExcelParser parser) throws IOException {
		this.gameVersionRepository = gameVersionRepository;
		parser.readFromXls();
	}

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
}
