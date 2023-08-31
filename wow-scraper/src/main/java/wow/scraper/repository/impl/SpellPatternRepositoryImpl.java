package wow.scraper.repository.impl;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import wow.commons.model.pve.GameVersionId;
import wow.commons.model.spell.SpellId;
import wow.scraper.parser.spell.SpellParser;
import wow.scraper.parser.spell.SpellPattern;
import wow.scraper.repository.SpellPatternRepository;
import wow.scraper.repository.impl.excel.spell.SpellPatternExcelParser;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * User: POlszewski
 * Date: 2023-08-29
 */
@Repository
public class SpellPatternRepositoryImpl implements SpellPatternRepository {
	private final Map<SpellId, List<SpellPattern>> spellPatterns = new EnumMap<>(SpellId.class);

	@Value("${spell.parsers.xls.file.path}")
	private String xlsFilePath;

	@SneakyThrows
	@PostConstruct
	public void init() {
		var spellPatternExcelParser = new SpellPatternExcelParser(xlsFilePath, this);
		spellPatternExcelParser.readFromXls();
	}

	@Override
	public SpellParser getSpellParser(SpellId spellId, GameVersionId gameVersion) {
		var patterns = spellPatterns.getOrDefault(spellId, List.of()).stream()
				.filter(x -> x.supports(gameVersion))
				.toList();

		return new SpellParser(patterns);
	}

	public void add(SpellId spellId, SpellPattern pattern) {
		spellPatterns.computeIfAbsent(spellId, x -> new ArrayList<>()).add(pattern);
	}
}
