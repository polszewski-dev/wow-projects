package wow.scraper.parser.tooltip;

import lombok.Getter;
import wow.commons.model.config.Description;
import wow.commons.model.effect.Effect;
import wow.commons.model.effect.EffectId;
import wow.commons.model.effect.impl.EffectImpl;
import wow.commons.model.pve.GameVersionId;
import wow.commons.model.pve.PhaseId;
import wow.commons.model.spell.ActivatedAbility;
import wow.commons.model.spell.impl.ActivatedAbilityImpl;
import wow.commons.model.spell.impl.SpellImpl;
import wow.commons.util.parser.ParsedMultipleValues;
import wow.commons.util.parser.Rule;
import wow.scraper.config.ScraperContext;
import wow.scraper.model.JsonCommonDetails;
import wow.scraper.model.JsonItemDetails;
import wow.scraper.model.WowheadItemCategory;
import wow.scraper.parser.scraper.ScraperMatcher;
import wow.scraper.parser.scraper.ScraperParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import static wow.scraper.util.ExportUtil.SourceType;
import static wow.scraper.util.ExportUtil.getId;

/**
 * User: POlszewski
 * Date: 2023-09-02
 */
@Getter
public class ItemEffectParser extends AbstractTooltipParser<JsonCommonDetails> {
	private ActivatedAbility activatedAbility;
	private final List<Effect> effects = new ArrayList<>();
	private final List<Effect> itemSetBonusEffects = new ArrayList<>();

	private String itemSetName;

	private final WowheadItemCategory category;

	public ItemEffectParser(
			JsonCommonDetails details,
			GameVersionId gameVersion,
			ScraperContext scraperContext,
			WowheadItemCategory category
	) {
		super(details, gameVersion, scraperContext);
		this.category = category;
	}

	@Override
	protected Rule[] getRules() {
		return new Rule[] {
				rulePhase,
				Rule.test(this::tryParseActivatedAbility, x -> {}),
				Rule.test(this::tryParseEffect, x -> {}),
				Rule.regex("(.*) \\(\\d/(\\d)\\)", this::parseItemSetName),
				Rule.regex("\\((\\d+)\\) Set ?: (.*)", this::parseItemSetBonus),
				ruleIgnoreEverything
		};
	}

	@Override
	protected String prepareLine(String line) {
		var tooltipCleaner = new EnchantTooltipCleaner(line);
		return tooltipCleaner.cleanEnchantTooltip();
	}

	@Override
	protected void beforeParse() {
		// void
	}

	@Override
	protected void afterParse() {
		// void
	}

	private boolean tryParseActivatedAbility(String line) {
		return parse(
				line,
				getSpellPatternRepository()::getActivatedAbilityParser,
				matcher -> addActivatedAbility((ActivatedAbilityImpl) matcher.getActivatedAbility(), line)
		);
	}

	private boolean tryParseEffect(String line) {
		return tryParseProc(line) || tryParseMiscEffect(line);
	}

	private boolean tryParseProc(String line) {
		return parse(
				line,
				getSpellPatternRepository()::getProcParser,
				matcher -> addEffect((EffectImpl) matcher.getEffect(), line)
		);
	}

	private boolean tryParseMiscEffect(String line) {
		return parse(
				line,
				getSpellPatternRepository()::getMiscEffectParser,
				matcher -> addEffect((EffectImpl) matcher.getEffect(), line)
		);
	}

	private void addActivatedAbility(ActivatedAbilityImpl activatedAbility, String line) {
		if (this.activatedAbility != null) {
			throw new IllegalStateException();
		}
		var effectIdx = getEffectIdx();
		var spellId = getId(details.getId(), SourceType.ITEM, effectIdx, 0, 0);
		var spellName = details.getName();
		initSpell(activatedAbility, spellId, spellName, line);
		this.activatedAbility = activatedAbility;
	}

	private void addEffect(EffectImpl effect, String line) {
		var effectIdx = getEffectIdx();
		var effectId = getId(details.getId(), SourceType.ITEM, effectIdx, 0, 0);
		var effectName = "%s - proc #%s".formatted(details.getName(), effects.size() + 1);
		initEffect(effect, effectId, effectName, line);
		this.effects.add(effect);
	}

	private int getEffectIdx() {
		return effects.size() + (activatedAbility != null ? 1 : 0);
	}

	private void parseItemSetBonus(ParsedMultipleValues parsedValues) {
		var numPieces = parsedValues.getInteger(0);
		var line = parsedValues.get(1);

		boolean procParsed = parse(
				line,
				getSpellPatternRepository()::getProcParser,
				matcher -> addSetBonusEffect((EffectImpl) matcher.getEffect(), numPieces, line)
		);

		if (procParsed) {
			return;
		}

		parse(
				line,
				getSpellPatternRepository()::getMiscEffectParser,
				matcher -> addSetBonusEffect((EffectImpl) matcher.getEffect(), numPieces, line)
		);
	}

	private void addSetBonusEffect(EffectImpl effect, int numPieces, String line) {
		var effectIdx = itemSetBonusEffects.size();
		var effectId = getId(details.getId(), SourceType.ITEM_SET, effectIdx, 0, 0);
		var effectName = "%s - P%s bonus".formatted(itemSetName, numPieces);
		initEffect(effect, effectId, effectName, line);
		this.itemSetBonusEffects.add(effect);
	}

	private void initSpell(SpellImpl spell, int spellId, String spellName, String line) {
		spell.setId(spellId);
		spell.setDescription(getDescription(spellName, line));
		spell.setTimeRestriction(getTimeRestriction());
	}

	private void initEffect(EffectImpl effect, int effectId, String effectName, String line) {
		effect.setId(EffectId.of(effectId));
		effect.setDescription(getDescription(effectName, line));
		effect.setTimeRestriction(getTimeRestriction());
	}

	private void parseItemSetName(ParsedMultipleValues parsedValues) {
		this.itemSetName = parsedValues.get(0);
	}

	private <M extends ScraperMatcher<?, ?, ?>, P extends ScraperParser<?, M, ?>> boolean parse(
			String line, Function<GameVersionId, P> parserFactory, Consumer<M> matcherConsumer
	) {
		var parser = parserFactory.apply(gameVersion);
		if (!parser.tryParse(line)) {
			return false;
		}
		matcherConsumer.accept(parser.getSuccessfulMatcher().orElseThrow());
		return true;
	}

	@Override
	protected PhaseId getPhaseOverride() {
		if (details instanceof JsonItemDetails) {
			return getScraperDatafixes().getItemPhaseOverrides().get(details.getId());
		} else {
			return getScraperDatafixes().getSpellPhaseOverrides().get(details.getId());
		}
	}

	public Optional<ActivatedAbility> getActivatedAbility() {
		return Optional.ofNullable(activatedAbility);
	}

	@Override
	public String getName() {
		return details.getName();
	}

	private Description getDescription(String name, String line) {
		return new Description(name, getIcon(), line);
	}
}
