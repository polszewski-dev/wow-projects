package wow.commons.repository.impl.character;

import org.springframework.stereotype.Component;
import wow.commons.repository.character.PetRepository;
import wow.commons.repository.impl.parser.character.PetExcelParser;
import wow.commons.repository.pve.GameVersionRepository;

import java.io.IOException;

/**
 * User: POlszewski
 * Date: 27.09.2024
 */
@Component
public class PetRepositoryImpl implements PetRepository {
	private final GameVersionRepository gameVersionRepository;

	public PetRepositoryImpl(GameVersionRepository gameVersionRepository, PetExcelParser parser) throws IOException {
		this.gameVersionRepository = gameVersionRepository;
		parser.readFromXls();
	}
}
