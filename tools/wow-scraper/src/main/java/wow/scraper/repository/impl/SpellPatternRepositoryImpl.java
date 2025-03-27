package wow.scraper.repository.impl;

import org.springframework.stereotype.Repository;
import wow.commons.model.pve.GameVersionId;
import wow.commons.model.spell.AbilityId;
import wow.scraper.parser.scraper.ScraperPattern;
import wow.scraper.parser.spell.ability.AbilityParser;
import wow.scraper.parser.spell.ability.AbilityPattern;
import wow.scraper.parser.spell.activated.ActivatedAbilityParser;
import wow.scraper.parser.spell.activated.ActivatedAbilityPattern;
import wow.scraper.parser.spell.misc.MiscEffectParser;
import wow.scraper.parser.spell.misc.MiscEffectPattern;
import wow.scraper.parser.spell.proc.ProcParser;
import wow.scraper.parser.spell.proc.ProcPattern;
import wow.scraper.repository.SpellPatternRepository;
import wow.scraper.repository.impl.excel.spell.SpellPatternExcelParser;

import java.io.IOException;
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
	private final Map<AbilityId, List<AbilityPattern>> spellPatterns = new EnumMap<>(AbilityId.class);
	private final List<ActivatedAbilityPattern> activatedAbilityPatterns = new ArrayList<>();
	private final List<ProcPattern> procPatterns = new ArrayList<>();
	private final List<MiscEffectPattern> miscEffectPatterns = new ArrayList<>();

	public SpellPatternRepositoryImpl(SpellPatternExcelParser parser) throws IOException {
		parser.readFromXls();
		spellPatterns.putAll(parser.getSpellPatterns());
		activatedAbilityPatterns.addAll(parser.getActivatedAbilityPatterns());
		procPatterns.addAll(parser.getProcPatterns());
		miscEffectPatterns.addAll(parser.getMiscEffectPatterns());
	}

	@Override
	public AbilityParser getAbilityParser(AbilityId abilityId, GameVersionId gameVersion) {
		var patterns = spellPatterns.getOrDefault(abilityId, List.of());
		var supported = getSupported(patterns, gameVersion);

		return new AbilityParser(supported);
	}

	@Override
	public ActivatedAbilityParser getActivatedAbilityParser(GameVersionId gameVersion) {
		var supported = getSupported(activatedAbilityPatterns, gameVersion);

		return new ActivatedAbilityParser(supported);
	}

	@Override
	public ProcParser getProcParser(GameVersionId gameVersion) {
		var supported = getSupported(procPatterns, gameVersion);

		return new ProcParser(supported);
	}

	@Override
	public MiscEffectParser getMiscEffectParser(GameVersionId gameVersion) {
		var supported = getSupported(miscEffectPatterns, gameVersion);

		return new MiscEffectParser(supported);
	}

	private <T extends ScraperPattern<?>> List<T> getSupported(List<T> list, GameVersionId gameVersion) {
		return list.stream()
				.filter(x -> x.supports(gameVersion))
				.toList();
	}
}
