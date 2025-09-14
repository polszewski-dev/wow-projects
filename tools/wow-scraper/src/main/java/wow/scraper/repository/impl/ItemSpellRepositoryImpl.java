package wow.scraper.repository.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import wow.commons.model.config.TimeRestricted;
import wow.commons.model.effect.Effect;
import wow.commons.model.effect.EffectId;
import wow.commons.model.effect.impl.EffectImpl;
import wow.commons.model.pve.GameVersionId;
import wow.commons.model.spell.ActivatedAbility;
import wow.commons.model.spell.Spell;
import wow.commons.model.spell.SpellId;
import wow.commons.model.spell.impl.ActivatedAbilityImpl;
import wow.commons.model.spell.impl.SpellImpl;
import wow.commons.util.GameVersionMap;
import wow.scraper.config.ScraperConfig;
import wow.scraper.config.ScraperContext;
import wow.scraper.config.ScraperContextImpl;
import wow.scraper.config.ScraperDatafixes;
import wow.scraper.model.WowheadItemCategory;
import wow.scraper.parser.tooltip.AbstractTooltipParser;
import wow.scraper.parser.tooltip.ItemEffectParser;
import wow.scraper.repository.ItemDetailRepository;
import wow.scraper.repository.ItemSpellRepository;
import wow.scraper.repository.SpellDetailRepository;
import wow.scraper.repository.SpellPatternRepository;
import wow.scraper.util.SpellTraverser;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static wow.scraper.util.CommonAssertions.assertNoDuplicates;
import static wow.scraper.util.ExportUtil.getDescription;
import static wow.scraper.util.ExportUtil.getId;

/**
 * User: POlszewski
 * Date: 2023-10-07
 */
@Repository
@RequiredArgsConstructor
public class ItemSpellRepositoryImpl implements ItemSpellRepository {
	private final ScraperConfig scraperConfig;
	private final ScraperDatafixes scraperDatafixes;
	private final ItemDetailRepository itemDetailRepository;
	private final SpellDetailRepository spellDetailRepository;
	private final SpellPatternRepository spellPatternRepository;

	private final GameVersionMap<String, ActivatedAbility> spellsByTooltip = new GameVersionMap<>();
	private final GameVersionMap<String, Effect> effectsByTooltip = new GameVersionMap<>();
	private boolean prepared;

	@Override
	public List<ActivatedAbility> getActivatedAbilities() {
		prepareDataOnce();
		return spellsByTooltip.allValues().stream().toList();
	}

	@Override
	public List<Effect> getItemEffects() {
		prepareDataOnce();
		return effectsByTooltip.allValues().stream().toList();
	}

	@Override
	public Optional<ActivatedAbility> getActivatedAbility(GameVersionId gameVersion, String tooltip) {
		prepareDataOnce();
		return spellsByTooltip.getOptional(gameVersion, tooltip);
	}

	@Override
	public Optional<Effect> getItemEffect(GameVersionId gameVersion, String tooltip) {
		prepareDataOnce();
		return effectsByTooltip.getOptional(gameVersion, tooltip);
	}

	private void prepareDataOnce() {
		if (!prepared) {
			getParsedItemTooltips().forEach(this::createItemSpellsAndEffects);
			prepared = true;
			validateIds();
		}
	}

	private void validateIds() {
		assertNoDuplicates(spellsByTooltip.allValues(), x -> x.getId() + "#" + x.getGameVersionId());
		assertNoDuplicates(effectsByTooltip.allValues(), x -> x.getId() + "#" + x.getGameVersionId());
	}

	private void createItemSpellsAndEffects(ItemEffectParser parser) {
		traverseSpells(parser);
		traverseEffects(parser);
	}

	private void traverseSpells(ItemEffectParser parser) {
		parser.getActivatedAbility().ifPresent(
				spell -> onSpell((ActivatedAbilityImpl) spell, parser)
		);
	}

	private void traverseEffects(ItemEffectParser parser) {
		parser.getEffects().forEach(
				effect -> onEffect((EffectImpl) effect, parser)
		);
		parser.getItemSetBonusEffects().forEach(
				effect -> onEffect((EffectImpl) effect, parser)
		);
	}

	private void onSpell(ActivatedAbilityImpl rootSpell, ItemEffectParser parser) {
		rootSpell.setTimeRestriction(parser.getTimeRestriction());

		if (!addNewSpell(rootSpell)) {
			return;
		}

		new SpellTraverser()
				.setSpellHandler((spell, level, index) -> onSpellSpell((SpellImpl) spell, level, index, rootSpell))
				.setEffectHandler((effect, level, index) -> onSpellEffect((EffectImpl) effect, level, index, rootSpell))
				.traverse(rootSpell);
	}

	private void onEffect(EffectImpl rootEffect, ItemEffectParser parser) {
		rootEffect.setTimeRestriction(parser.getTimeRestriction());

		if (!addNewEffect(rootEffect)) {
			return;
		}

		new SpellTraverser()
				.setSpellHandler((spell, level, index) -> onEffectSpell((SpellImpl) spell, level, index, rootEffect))
				.setEffectHandler((effect, level, index) -> onEffectEffect((EffectImpl) effect, level, index, rootEffect))
				.traverse(rootEffect);
	}

	private void onSpellSpell(SpellImpl spell, int level, int index, Spell rootSpell) {
		if (spell != rootSpell) {
			var spellId = getId(rootSpell.getId().value(), null, 0, level, index, SpellId::of);

			spell.setId(spellId);
			spell.setDescription(getDescription(rootSpell, level));
			spell.setTimeRestriction(rootSpell.getTimeRestriction());
		}
	}

	private void onSpellEffect(EffectImpl effect, int level, int index, Spell rootSpell) {
		var effectId = getId(rootSpell.getId().value(), null, 0, level, index, EffectId::of);

		effect.setId(effectId);
		effect.setDescription(getDescription(rootSpell, level));
		effect.setTimeRestriction(rootSpell.getTimeRestriction());
	}

	private void onEffectSpell(SpellImpl spell, int level, int index, Effect rootEffect) {
		var spellId = getId(rootEffect.getId().value(), null, 0, level, index, SpellId::of);

		spell.setId(spellId);
		spell.setDescription(getDescription(rootEffect, level));
		spell.setTimeRestriction(rootEffect.getTimeRestriction());
	}

	private void onEffectEffect(EffectImpl effect, int level, int index, Effect rootEffect) {
		if (effect != rootEffect) {
			var effectId = getId(rootEffect.getId().value(), null, 0, level, index, EffectId::of);

			effect.setId(effectId);
			effect.setDescription(getDescription(rootEffect, level));
			effect.setTimeRestriction(rootEffect.getTimeRestriction());
		}
	}

	private boolean addNewSpell(ActivatedAbility spell) {
		if (!isToBeUpdated(spell)) {
			return false;
		}

		var version = spell.getGameVersionId();
		var tooltip = spell.getTooltip();

		spellsByTooltip.put(version, tooltip, spell);
		return true;
	}

	private boolean addNewEffect(Effect effect) {
		if (!isToBeUpdated(effect)) {
			return false;
		}

		var version = effect.getGameVersionId();
		var tooltip = effect.getTooltip();

		effectsByTooltip.put(version, tooltip, effect);
		return true;
	}

	private boolean isToBeUpdated(ActivatedAbility spell) {
		var version = spell.getGameVersionId();
		var tooltip = spell.getTooltip();

		var existingOptional = spellsByTooltip.getOptional(version, tooltip);

		return existingOptional.isEmpty() || hasEarlierPhase(spell, existingOptional.get());
	}

	private boolean isToBeUpdated(Effect effect) {
		var version = effect.getGameVersionId();
		var tooltip = effect.getTooltip();

		var existingOptional = effectsByTooltip.getOptional(version, tooltip);

		return existingOptional.isEmpty() || hasEarlierPhase(effect, existingOptional.get());
	}

	private boolean hasEarlierPhase(TimeRestricted newOne, TimeRestricted existingOne) {
		var newPhaseId = newOne.getEarliestPhaseId();
		var existingPhaseId = existingOne.getEarliestPhaseId();

		return newPhaseId.isEarlier(existingPhaseId);
	}

	private List<ItemEffectParser> getParsedItemTooltips() {
		var parsers = getItemEffectParsers();
		parsers.forEach(ItemEffectParser::parse);
		parsers.sort(getComparator());
		return parsers;
	}

	private List<ItemEffectParser> getItemEffectParsers() {
		var parsers = new ArrayList<ItemEffectParser>();

		for (var category : WowheadItemCategory.equipment()) {
			addItemParsers(category, parsers);
		}

		addItemParsers(WowheadItemCategory.GEMS, parsers);
		addEnchantParsers(parsers);

		return parsers;
	}

	private void addItemParsers(WowheadItemCategory category, List<ItemEffectParser> parsers) {
		for (var gameVersion : scraperConfig.getGameVersions()) {
			for (var detailId : itemDetailRepository.getDetailIds(gameVersion, category)) {
				var details = itemDetailRepository.getDetail(gameVersion, category, detailId).orElseThrow();
				var parser = new ItemEffectParser(details, gameVersion, getScraperContext(), category);
				parsers.add(parser);
			}
		}
	}

	private void addEnchantParsers(List<ItemEffectParser> parsers) {
		for (var gameVersion : scraperConfig.getGameVersions()) {
			for (var enchantId : spellDetailRepository.getEnchantDetailIds(gameVersion)) {
				var enchant = spellDetailRepository.getEnchantDetail(gameVersion, enchantId).orElseThrow();
				var parser = new ItemEffectParser(enchant, gameVersion, getScraperContext(), WowheadItemCategory.ENCHANTS_PERMANENT);
				parsers.add(parser);
			}
		}
	}

	private ScraperContext getScraperContext() {
		return new ScraperContextImpl(
				null, itemDetailRepository, spellDetailRepository, null, spellPatternRepository, null, null, null, null, scraperConfig, scraperDatafixes
		);
	}

	private static Comparator<ItemEffectParser> getComparator() {
		return Comparator
				.comparing(ItemEffectParser::getCategory)
				.thenComparing((ItemEffectParser parser) -> parser.getDetails().getName())
				.thenComparing((ItemEffectParser parser) -> parser.getDetails().getId())
				.thenComparing(AbstractTooltipParser::getGameVersion);
	}
}
