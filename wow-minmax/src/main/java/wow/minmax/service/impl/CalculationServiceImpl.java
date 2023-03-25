package wow.minmax.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import wow.character.model.build.BuffSetId;
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
import wow.minmax.model.CharacterStats;
import wow.minmax.model.SpecialAbilityStats;
import wow.minmax.model.SpellStats;
import wow.minmax.service.CalculationService;
import wow.minmax.service.impl.enumerators.StatEquivalentFinder;

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
		return getDpsStatEquivalent(attributesToFindEquivalent, targetStat, mode, character, null, null);
	}

	@Override
	public Attributes getDpsStatEquivalent(
			Attributes attributesToFindEquivalent,
			PrimitiveAttributeId targetStat,
			EquivalentMode mode,
			Character character,
			Spell spell,
			Attributes totalStats
	) {
		spell = initOptional(character, spell);
		totalStats = initOptional(character, totalStats);

		var finder = new StatEquivalentFinder(
				attributesToFindEquivalent, targetStat, mode, character, spell, totalStats, this
		);

		return finder.getDpsStatEquivalent();
	}

	@Override
	public Attributes getAbilityEquivalent(SpecialAbility specialAbility, Character character, Spell spell, Attributes totalStats) {
		spell = initOptional(character, spell);
		totalStats = initOptional(character, totalStats);

		Snapshot snapshot = getSnapshot(
				character,
				spell,
				AttributesBuilder.removeAttributes(totalStats, Attributes.of(specialAbility))
		);

		return specialAbility.getStatEquivalent(snapshot);
	}

	@Override
	public double getSpellDps(Character character, Spell spell) {
		return getSpellDps(character, spell, null);
	}

	@Override
	public double getSpellDps(Character character, Spell spell, Attributes totalStats) {
		return getSnapshot(character, spell, totalStats).getDps(CritMode.AVERAGE, true);
	}

	@Override
	public Snapshot getSnapshot(Character character, Spell spell, Attributes totalStats) {
		spell = initOptional(character, spell);
		totalStats = initOptional(character, totalStats);

		return new Snapshot(spell, character, totalStats);
	}

	@Override
	public SpellStats getSpellStats(Character character, Spell spell) {
		SpellStatistics spellStatistics = getSnapshot(character, spell, null).getSpellStatistics(CritMode.AVERAGE, true);
		int amount = 10;
		double hitSpEqv = getSpEquivalent(SPELL_HIT_RATING, amount, character, spell);
		double critSpEqv = getSpEquivalent(SPELL_CRIT_RATING, amount, character, spell);
		double hasteSpEqv = getSpEquivalent(SPELL_HASTE_RATING, amount, character, spell);
		return new SpellStats(character, spellStatistics, hitSpEqv, critSpEqv, hasteSpEqv);
	}

	private double getSpEquivalent(PrimitiveAttributeId attributeId, int amount, Character character, Spell spell) {
		return getDpsStatEquivalent(
				Attributes.of(attributeId, amount),
				SPELL_POWER,
				ADDITIONAL,
				character, spell, character.getStats()
		).getSpellPower();
	}

	private Spell initOptional(Character character, Spell spell) {
		if (spell != null) {
			return spell;
		}
		return character.getDamagingSpell();
	}

	private Attributes initOptional(Character character, Attributes totalStats) {
		if (totalStats != null) {
			return totalStats;
		}
		return character.getStats();
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
		Snapshot snapshot = getSnapshot(character, character.getDamagingSpell(), totalStats);

		return new CharacterStats(
				totalStats.getTotalSpellDamage(),
				totalStats.getTotalSpellDamage(SpellSchool.SHADOW),
				totalStats.getTotalSpellDamage(SpellSchool.FIRE),
				totalStats.getSpellHitRating(),
				snapshot.getTotalHit(),
				totalStats.getSpellCritRating(),
				snapshot.getTotalCrit(),
				totalStats.getSpellHasteRating(),
				snapshot.getTotalHaste(),
				snapshot.getStamina(),
				snapshot.getIntellect(),
				snapshot.getSpirit()
		);
	}

	@Override
	public SpecialAbilityStats getSpecialAbilityStats(Character character, SpecialAbility specialAbility) {
		Attributes statEquivalent = getAbilityEquivalent(specialAbility, character, null, null);
		Attributes spEquivalent = getDpsStatEquivalent(Attributes.of(specialAbility), SPELL_POWER, REPLACEMENT, character);

		return new SpecialAbilityStats(
				specialAbility.getLine() != null ? specialAbility.getLine() : specialAbility.toString(),
				specialAbility.toString(),
				statEquivalent,
				spEquivalent.getSpellPower()
		);
	}
}
