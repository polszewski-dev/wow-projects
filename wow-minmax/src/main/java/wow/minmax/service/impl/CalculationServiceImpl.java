package wow.minmax.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import wow.character.model.build.BuffSetId;
import wow.character.model.build.Rotation;
import wow.character.model.character.Character;
import wow.character.model.snapshot.CritMode;
import wow.character.model.snapshot.Snapshot;
import wow.character.model.snapshot.SpellStatistics;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.attributes.complex.SpecialAbility;
import wow.commons.model.attributes.primitive.PrimitiveAttributeId;
import wow.commons.model.spells.Spell;
import wow.commons.model.spells.SpellSchool;
import wow.commons.util.AttributesBuilder;
import wow.minmax.model.*;
import wow.minmax.service.CalculationService;
import wow.minmax.service.impl.enumerators.RotationAbilityEquivalentCalculator;
import wow.minmax.service.impl.enumerators.RotationDpsCalculator;
import wow.minmax.service.impl.enumerators.RotationStatsCalculator;
import wow.minmax.service.impl.enumerators.StatEquivalentFinder;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static wow.commons.model.attributes.primitive.PrimitiveAttributeId.*;
import static wow.minmax.service.CalculationService.EquivalentMode.ADDITIONAL;
import static wow.minmax.service.CalculationService.EquivalentMode.REPLACEMENT;

/**
 * User: POlszewski
 * Date: 2021-12-15
 */
@Service
@AllArgsConstructor
public class CalculationServiceImpl implements CalculationService {
	@Override
	public Attributes getDpsStatEquivalent(Attributes attributesToFindEquivalent, PrimitiveAttributeId targetStat, EquivalentMode mode, Character character) {
		return getDpsStatEquivalent(attributesToFindEquivalent, targetStat, mode, character, character.getRotation(), character.getStats());
	}

	@Override
	public Attributes getDpsStatEquivalent(
			Attributes attributesToFindEquivalent,
			PrimitiveAttributeId targetStat,
			EquivalentMode mode,
			Character character,
			Rotation rotation,
			Attributes totalStats
	) {
		var finder = new StatEquivalentFinder(
				attributesToFindEquivalent, targetStat, mode, character, rotation, totalStats, this
		);

		return finder.getDpsStatEquivalent();
	}

	@Override
	public Attributes getAbilityEquivalent(SpecialAbility specialAbility, Character character) {
		return getAbilityEquivalent(specialAbility, character, character.getRotation(), character.getStats());
	}

	@Override
	public Attributes getAbilityEquivalent(SpecialAbility specialAbility, Character character, Rotation rotation, Attributes totalStats) {
		var calculator = new RotationAbilityEquivalentCalculator(
				character,
				rotation,
				AttributesBuilder.removeAttributes(totalStats, Attributes.of(specialAbility))
		);
		calculator.calculate();
		return calculator.getAbilityEquivalent(specialAbility);
	}

	@Override
	public double getRotationDps(Character character, Rotation rotation) {
		return getRotationDps(character, rotation, character.getStats());
	}

	@Override
	public double getRotationDps(Character character, Rotation rotation, Attributes totalStats) {
		var calculator = new RotationDpsCalculator(character, rotation, totalStats);
		calculator.calculate();
		return calculator.getDps();
	}

	@Override
	public RotationStats getRotationStats(Character character, Rotation rotation) {
		var calculator = new RotationStatsCalculator(character, rotation, character.getStats());
		calculator.calculate();
		return calculator.getStats();
	}

	@Override
	public Snapshot getSnapshot(Character character, Spell spell, Attributes totalStats) {
		return new Snapshot(spell, character, totalStats);
	}

	@Override
	public SpellStats getSpellStats(Character character, Spell spell) {
		SpellStatistics spellStatistics = getSnapshot(character, spell, character.getStats()).getSpellStatistics(CritMode.AVERAGE, true);
		SpellStatEquivalents statEquivalents = getStatEquivalents(character, spell);
		return new SpellStats(character, spellStatistics, statEquivalents);
	}

	private SpellStatEquivalents getStatEquivalents(Character character, Spell spell) {
		PrimitiveAttributeId hit;
		PrimitiveAttributeId crit;
		PrimitiveAttributeId haste;

		if (character.getGameVersion().isCombatRatings()) {
			hit = SPELL_HIT_RATING;
			crit = SPELL_CRIT_RATING;
			haste = SPELL_HASTE_RATING;
		} else {
			hit = SPELL_HIT_PCT;
			crit = SPELL_CRIT_PCT;
			haste = SPELL_HASTE_PCT;
		}

		double amount = character.getGameVersion().getEvivalentAmount();
		double hitSpEqv = getSpEquivalent(hit, amount, character, spell);
		double critSpEqv = getSpEquivalent(crit, amount, character, spell);
		double hasteSpEqv = getSpEquivalent(haste, amount, character, spell);
		return new SpellStatEquivalents(hitSpEqv, critSpEqv, hasteSpEqv);
	}

	private double getSpEquivalent(PrimitiveAttributeId attributeId, double amount, Character character, Spell spell) {
		return getDpsStatEquivalent(
				Attributes.of(attributeId, amount),
				SPELL_POWER,
				ADDITIONAL,
				character,
				Rotation.onlyFiller(spell),
				character.getStats()
		).getSpellPower();
	}

	@Override
	public CharacterStats getCurrentStats(Character character) {
		return getStats(character, character.getStats());
	}

	@Override
	public CharacterStats getStats(Character character, BuffSetId... buffSetIds) {
		Character copy = character.copy();
		copy.setBuffs(buffSetIds);
		return getStats(copy, copy.getStats());
	}

	@Override
	public CharacterStats getEquipmentStats(Character character) {
		return getStats(character, character.getEquipment().getStats());
	}

	private CharacterStats getStats(Character character, Attributes totalStats) {
		Snapshot snapshot = getSnapshot(character, null, totalStats);

		return new CharacterStats(
				totalStats.getTotalSpellDamage(),
				getSpellDamageBySchool(totalStats),
				totalStats.getSpellHitRating(),
				snapshot.getTotalHit(),
				totalStats.getSpellCritRating(),
				snapshot.getTotalCrit(),
				totalStats.getSpellHasteRating(),
				snapshot.getTotalHaste(),
				snapshot.getStamina(),
				snapshot.getIntellect(),
				snapshot.getSpirit(),
				snapshot.getMaxHealth(),
				snapshot.getMaxMana()
		);
	}

	private Map<SpellSchool, Double> getSpellDamageBySchool(Attributes totalStats) {
		return Stream.of(SpellSchool.values())
				.collect(Collectors.toMap(x -> x, totalStats::getTotalSpellDamage));
	}

	@Override
	public SpecialAbilityStats getSpecialAbilityStats(Character character, SpecialAbility specialAbility) {
		Attributes statEquivalent = getAbilityEquivalent(specialAbility, character);
		Attributes spEquivalent = getDpsStatEquivalent(Attributes.of(specialAbility), SPELL_POWER, REPLACEMENT, character);

		return new SpecialAbilityStats(
				specialAbility,
				statEquivalent,
				spEquivalent.getSpellPower()
		);
	}
}
