package wow.scraper.exporter.spell;

import wow.commons.model.effect.Effect;
import wow.commons.model.effect.impl.EffectImpl;
import wow.commons.model.spell.*;
import wow.commons.model.spell.impl.ClassAbilityImpl;
import wow.commons.model.spell.impl.SpellImpl;
import wow.scraper.exporter.spell.excel.SpellBaseExcelBuilder;
import wow.scraper.model.JsonSpellDetails;
import wow.scraper.model.WowheadSpellCategory.Type;
import wow.scraper.parser.tooltip.AbilityTooltipParser;
import wow.scraper.parser.tooltip.AbstractTooltipParser;
import wow.scraper.util.SpellTraverser;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static wow.scraper.util.ExportUtil.getDescription;
import static wow.scraper.util.ExportUtil.getId;

/**
 * User: POlszewski
 * Date: 2023-08-24
 */
public class AbilityExporter extends MultiPageSpellBaseExporter {
	private final List<ClassAbility> abilities = new ArrayList<>();
	private final List<Spell> abilitySpells = new ArrayList<>();
	private final List<Effect> abilityEffects = new ArrayList<>();
	private final List<Effect> triggeredEffects = new ArrayList<>();

	@Override
	protected void prepareData() {
		getParsedAbilityTooltips().forEach(this::createExportedSpellsAndEffects);
	}

	@Override
	protected void exportPreparedData(SpellBaseExcelBuilder builder) {
		exportAbilities(builder);
		exportAbilitySpells(builder);
		exportSpellEffects(builder);
	}

	private void exportAbilities(SpellBaseExcelBuilder builder) {
		builder.addAbilityHeader();
		abilities.forEach(builder::addAbility);
	}

	private void exportAbilitySpells(SpellBaseExcelBuilder builder) {
		builder.addAbilitySpellHeader();
		abilitySpells.forEach(builder::addAbilitySpell);
	}

	private void exportSpellEffects(SpellBaseExcelBuilder builder) {
		builder.addAbilityEffectHeader();
		abilityEffects.forEach(builder::addAbilityEffect);
		triggeredEffects.forEach(builder::addAbilityEffect);
	}

	private void createExportedSpellsAndEffects(AbilityTooltipParser parser) {
		var ability = getAbility(parser);

		new SpellTraverser()
				.setSpellHandler((spell, level, index) -> onSpell(spell, level, index, ability))
				.setEffectHandler((effect, level, index) -> onEffect(effect, level, index, ability))
				.traverse(ability);
	}

	private ClassAbility getAbility(AbilityTooltipParser parser) {
		var ability = newAbility(parser);
		fillAbility((ClassAbilityImpl) ability, parser);
		return ability;
	}

	private ClassAbility newAbility(AbilityTooltipParser parser) {
		var matcher = parser.getAbilityMatcher();
		if (matcher != null) {
			return matcher.getAbility();
		}
		var ability = new ClassAbilityImpl();
		ability.setDirectComponents(List.of());
		return ability;
	}

	private void onSpell(Spell spell, int level, int index, ClassAbility ability) {
		if (index == 0) {
			abilities.add(ability);
		} else {
			fillTriggeredSpell((SpellImpl) spell, level, index, ability);
			abilitySpells.add(spell);
		}
	}

	private void onEffect(Effect effect, int level, int index, Ability ability) {
		fillEffectInfo((EffectImpl) effect, level, index, ability);
		if (index == 0) {
			abilityEffects.add(effect);
		} else {
			triggeredEffects.add(effect);
		}
	}

	private void fillAbility(ClassAbilityImpl ability, AbilityTooltipParser parser) {
		ability.setId(parser.getSpellId());
		ability.setDescription(parser.getSpellDescription());
		ability.setTimeRestriction(parser.getTimeRestriction());
		ability.setCastInfo(getCastInfo(parser.getCastInfo(), ability.getCastInfo()));
		ability.setCooldown(parser.getCooldown());
		ability.setRange(parser.getRange());
		ability.setRank(parser.getRank());
		ability.setTalentTree(parser.getTalentTree());
		ability.setCategory(parser.getAbilityCategory());
		ability.setCharacterRestriction(parser.getCharacterRestriction());
		ability.setCost(getCost(parser.getCost(), ability.getCost()));
	}

	private void fillTriggeredSpell(SpellImpl spell, int level, int index, Ability ability) {
		spell.setId(getId(ability.getId(), null, 0, level, index));
		spell.setDescription(getDescription(ability, level));
		spell.setTimeRestriction(ability.getTimeRestriction());
	}

	private void fillEffectInfo(EffectImpl effect, int level, int index, Ability ability) {
		effect.setEffectId(getId(ability.getId(), null, 0, level, index));
		effect.setDescription(getDescription(ability, level));
		effect.setTimeRestriction(ability.getTimeRestriction());
	}

	private Cost getCost(Cost tooltipCost, Cost matchedCost) {
		if (tooltipCost != null && matchedCost == null) {
			return tooltipCost;
		}

		if (tooltipCost == null && matchedCost != null) {
			return matchedCost;
		}

		if (tooltipCost == null) {
			return Cost.NONE;
		}

		throw new IllegalArgumentException("Can't have both costs specified: '%s' & '%s'".formatted(tooltipCost, matchedCost));
	}

	private CastInfo getCastInfo(CastInfo tooltipCastInfo, CastInfo matchedCastInfo) {
		if (matchedCastInfo != null) {
			if (!matchedCastInfo.castTime().equals(tooltipCastInfo.castTime()) || matchedCastInfo.channeled() != tooltipCastInfo.channeled()) {
				throw new IllegalArgumentException("Can't have both casts specified: '%s' & '%s'".formatted(tooltipCastInfo, matchedCastInfo));
			}
			return matchedCastInfo;
		}

		return tooltipCastInfo;
	}

	private List<AbilityTooltipParser> getParsedAbilityTooltips() {
		var parsers = getAllData().stream()
				.map(this::createParser)
				.collect(Collectors.toList());

		parsers.forEach(AbstractTooltipParser::parse);
		parsers.sort(getComparator());
		return parsers;
	}

	private List<JsonSpellDetails> getAllData() {
		var abilityDetails = getData(Type.ABILITY).stream();

		var talentSpellDetails = getData(Type.TALENT).stream()
				.filter(this::isTalentSpell);

		return Stream.concat(abilityDetails, talentSpellDetails).toList();
	}

	private Comparator<AbilityTooltipParser> getComparator() {
		return Comparator
				.comparing(AbilityTooltipParser::getCharacterClass)
				.thenComparing(AbilityTooltipParser::getTalentTree)
				.thenComparing(AbilityTooltipParser::getName)
				.thenComparing(AbilityTooltipParser::getGameVersion)
				.thenComparing(AbilityTooltipParser::getRank);
	}

	private AbilityTooltipParser createParser(JsonSpellDetails details) {
		return new AbilityTooltipParser(details, getScraperContext());
	}
}
