package wow.scraper.repository.impl.excel.spell;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import polszewski.excel.reader.templates.ExcelParser;
import polszewski.excel.reader.templates.ExcelSheetParser;
import wow.commons.model.pve.GameVersionId;
import wow.commons.model.spell.AbilityId;
import wow.scraper.parser.scraper.ScraperPattern;
import wow.scraper.parser.spell.SpellPattern;
import wow.scraper.parser.spell.ability.AbilityPattern;
import wow.scraper.parser.spell.activated.ActivatedAbilityPattern;
import wow.scraper.parser.spell.misc.MiscEffectPattern;
import wow.scraper.parser.spell.proc.ProcPattern;

import java.io.InputStream;
import java.util.*;
import java.util.stream.Stream;

import static wow.scraper.parser.scraper.ScraperPattern.assertNoDuplicates;

/**
 * User: POlszewski
 * Date: 2023-08-29
 */
@Component
@Scope("prototype")
@RequiredArgsConstructor
public class SpellPatternExcelParser extends ExcelParser {
	@Value("${spell.parsers.xls.file.path}")
	private final String xlsFilePath;

	private final Map<AbilityId, List<AbilityPattern>> spellPatterns = new EnumMap<>(AbilityId.class);
	private final List<ActivatedAbilityPattern> activatedAbilityPatterns = new ArrayList<>();
	private final List<ProcPattern> procPatterns = new ArrayList<>();
	private final List<MiscEffectPattern> miscEffectPatterns = new ArrayList<>();

	@Override
	protected InputStream getExcelInputStream() {
		return fromResourcePath(xlsFilePath);
	}

	@Override
	protected Stream<ExcelSheetParser> getSheetParsers() {
		return Stream.of(
				new AbilityPatternSheetParser("abilities", this),
				new ActivatedAbilitySheetParser("activated", this),
				new ProcSheetParser("procs", this),
				new MiscEffectsSheetParser("misc_effects", this)
		);
	}

	public Map<AbilityId, List<AbilityPattern>> getSpellPatterns() {
		spellPatterns.values().forEach(ScraperPattern::assertNoDuplicates);
		return spellPatterns;
	}

	public List<ActivatedAbilityPattern> getActivatedAbilityPatterns() {
		assertNoDuplicates(activatedAbilityPatterns);
		return activatedAbilityPatterns;
	}

	public List<ProcPattern> getProcPatterns() {
		assertNoDuplicates(procPatterns);
		return procPatterns;
	}

	public List<MiscEffectPattern> getMiscEffectPatterns() {
		assertNoDuplicates(miscEffectPatterns);
		return miscEffectPatterns;
	}

	Optional<AbilityPattern> getAbilityPattern(AbilityId abilityId, String pattern, Set<GameVersionId> reqVersion) {
		return getMatching(spellPatterns.getOrDefault(abilityId, List.of()), pattern, reqVersion);
	}

	Optional<ActivatedAbilityPattern> getActivatedAbilityPattern(String pattern, Set<GameVersionId> reqVersion) {
		return getMatching(activatedAbilityPatterns, pattern, reqVersion);
	}

	Optional<ProcPattern> getProcPattern(String pattern, Set<GameVersionId> reqVersion) {
		return getMatching(procPatterns, pattern, reqVersion);
	}

	Optional<MiscEffectPattern> getMiscEffectPattern(String pattern, Set<GameVersionId> reqVersion) {
		return getMatching(miscEffectPatterns, pattern, reqVersion);
	}

	private <T extends SpellPattern<?>> Optional<T> getMatching(List<T> list, String pattern, Set<GameVersionId> reqVersion) {
		return list.stream()
				.filter(x -> x.matches(pattern, reqVersion))
				.findFirst();
	}

	void add(AbilityId abilityId, AbilityPattern pattern) {
		var list = spellPatterns.computeIfAbsent(abilityId, x -> new ArrayList<>());

		list.add(pattern);
	}

	void add(ActivatedAbilityPattern pattern) {
		activatedAbilityPatterns.add(pattern);
	}

	void add(ProcPattern pattern) {
		procPatterns.add(pattern);
	}

	void add(MiscEffectPattern pattern) {
		miscEffectPatterns.add(pattern);
	}
}
