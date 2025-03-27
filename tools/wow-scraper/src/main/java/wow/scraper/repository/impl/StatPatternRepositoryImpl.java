package wow.scraper.repository.impl;

import org.springframework.stereotype.Repository;
import wow.commons.model.pve.GameVersionId;
import wow.scraper.parser.stat.StatParser;
import wow.scraper.parser.stat.StatPattern;
import wow.scraper.repository.StatPatternRepository;
import wow.scraper.repository.impl.excel.stat.StatPatternExcelParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * User: POlszewski
 * Date: 2021-04-04
 */
@Repository
public class StatPatternRepositoryImpl implements StatPatternRepository {
	private final List<StatPattern> itemStatPatterns = new ArrayList<>();
	private final List<StatPattern> enchantStatPatterns = new ArrayList<>();
	private final List<StatPattern> gemStatPatterns = new ArrayList<>();
	private final List<StatPattern> socketBonusStatPatterns = new ArrayList<>();

	public StatPatternRepositoryImpl(StatPatternExcelParser parser) throws IOException {
		parser.readFromXls();
		itemStatPatterns.addAll(parser.getItemStatPatterns());
		enchantStatPatterns.addAll(parser.getEnchantStatPatterns());
		gemStatPatterns.addAll(parser.getGemStatPatterns());
		socketBonusStatPatterns.addAll(parser.getSocketBonusStatPatterns());
	}

	@Override
	public StatParser getItemStatParser(GameVersionId gameVersion) {
		return getStatParser(itemStatPatterns, gameVersion);
	}

	@Override
	public StatParser getEnchantStatParser(GameVersionId gameVersion) {
		return getStatParser(enchantStatPatterns, gameVersion);
	}

	@Override
	public StatParser getGemStatParser(GameVersionId gameVersion) {
		return getStatParser(gemStatPatterns, gameVersion);
	}

	@Override
	public StatParser getSocketBonusStatParser(GameVersionId gameVersion) {
		return getStatParser(socketBonusStatPatterns, gameVersion);
	}

	private StatParser getStatParser(List<StatPattern> patterns, GameVersionId gameVersion) {
		return new StatParser(
				patterns.stream()
						.filter(x -> x.supports(gameVersion))
						.toList()
		);
	}
}
