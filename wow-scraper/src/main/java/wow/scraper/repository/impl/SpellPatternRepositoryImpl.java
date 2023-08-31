package wow.scraper.repository.impl;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import wow.commons.model.pve.GameVersionId;
import wow.commons.model.spell.AbilityId;
import wow.scraper.parser.scraper.ScraperPattern;
import wow.scraper.parser.spell.SpellPattern;
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

import javax.annotation.PostConstruct;
import java.util.*;

import static wow.scraper.parser.scraper.ScraperPattern.assertNoDuplicates;

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

	@Value("${spell.parsers.xls.file.path}")
	private String xlsFilePath;

	@SneakyThrows
	@PostConstruct
	public void init() {
		var spellPatternExcelParser = new SpellPatternExcelParser(xlsFilePath, this);
		spellPatternExcelParser.readFromXls();
		validateAll();
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

	public void add(AbilityId abilityId, AbilityPattern pattern) {
		var list = spellPatterns.computeIfAbsent(abilityId, x -> new ArrayList<>());

		list.add(pattern);
	}

	public void add(ActivatedAbilityPattern pattern) {
		activatedAbilityPatterns.add(pattern);
	}

	public void add(ProcPattern pattern) {
		procPatterns.add(pattern);
	}

	public void add(MiscEffectPattern pattern) {
		miscEffectPatterns.add(pattern);
	}

	private void validateAll() {
		spellPatterns.values().forEach(ScraperPattern::assertNoDuplicates);
		assertNoDuplicates(activatedAbilityPatterns);
		assertNoDuplicates(procPatterns);
		assertNoDuplicates(miscEffectPatterns);
	}

	public Optional<AbilityPattern> getAbilityPattern(AbilityId abilityId, String pattern, Set<GameVersionId> reqVersion) {
		return getMatching(spellPatterns.getOrDefault(abilityId, List.of()), pattern, reqVersion);
	}

	public Optional<ActivatedAbilityPattern> getActivatedAbilityPattern(String pattern, Set<GameVersionId> reqVersion) {
		return getMatching(activatedAbilityPatterns, pattern, reqVersion);
	}

	public Optional<ProcPattern> getProcPattern(String pattern, Set<GameVersionId> reqVersion) {
		return getMatching(procPatterns, pattern, reqVersion);
	}

	public Optional<MiscEffectPattern> getMiscEffectPattern(String pattern, Set<GameVersionId> reqVersion) {
		return getMatching(miscEffectPatterns, pattern, reqVersion);
	}

	private <T extends ScraperPattern<?>> List<T> getSupported(List<T> list, GameVersionId gameVersion) {
		return list.stream()
				.filter(x -> x.supports(gameVersion))
				.toList();
	}

	private <T extends SpellPattern<?>> Optional<T> getMatching(List<T> list, String pattern, Set<GameVersionId> reqVersion) {
		return list.stream()
				.filter(x -> x.matches(pattern, reqVersion))
				.findFirst();
	}
}
