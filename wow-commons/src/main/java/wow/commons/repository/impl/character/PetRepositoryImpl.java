package wow.commons.repository.impl.character;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import wow.commons.repository.character.PetRepository;
import wow.commons.repository.impl.parser.character.PetExcelParser;
import wow.commons.repository.pve.GameVersionRepository;

import javax.annotation.PostConstruct;
import java.io.IOException;

/**
 * User: POlszewski
 * Date: 27.09.2024
 */
@Repository
@RequiredArgsConstructor
public class PetRepositoryImpl implements PetRepository {
	private final GameVersionRepository gameVersionRepository;

	@Value("${pets.xls.file.path}")
	private String xlsFilePath;

	@PostConstruct
	public void init() throws IOException {
		var parser = new PetExcelParser(xlsFilePath, gameVersionRepository);
		parser.readFromXls();
	}
}
