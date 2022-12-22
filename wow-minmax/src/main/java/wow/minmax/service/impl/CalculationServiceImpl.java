package wow.minmax.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import wow.character.model.character.Character;
import wow.character.model.snapshot.CritMode;
import wow.character.model.snapshot.Snapshot;
import wow.character.model.snapshot.SpellStatistics;
import wow.commons.model.attributes.Attribute;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.attributes.complex.SpecialAbility;
import wow.commons.model.attributes.primitive.PrimitiveAttribute;
import wow.commons.model.attributes.primitive.PrimitiveAttributeId;
import wow.commons.model.spells.Spell;
import wow.commons.util.AttributesBuilder;
import wow.minmax.model.PlayerSpellStats;
import wow.minmax.service.CalculationService;

import static wow.commons.model.attributes.primitive.PrimitiveAttributeId.*;

/**
 * User: POlszewski
 * Date: 2021-12-15
 */
@Service
@AllArgsConstructor
public class CalculationServiceImpl implements CalculationService {
	private static final double PRECISION = 0.0001;

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

		Attributes baseStats = getBaseAttributes(attributesToFindEquivalent, totalStats, mode);
		double targetDps = getTargetDps(attributesToFindEquivalent, totalStats, character, spell, mode);

		double equivalentValue = 0;
		double increase = 1;

		while (true) {
			PrimitiveAttribute equivalentStat = Attribute.of(targetStat, equivalentValue + increase);
			Attributes equivalentStats = AttributesBuilder.addAttribute(baseStats, equivalentStat);
			double equivalentDps = getSpellStatistics(character, spell, equivalentStats).getDps();

			if (Math.abs(equivalentDps - targetDps) <= PRECISION) {
				return Attributes.of(targetStat, equivalentValue);
			}

			if (equivalentDps < targetDps) {
				equivalentValue += increase;
			} else {
				increase /= 2;
			}
		}
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
	public SpellStatistics getSpellStatistics(Character character, Spell spell) {
		return getSpellStatistics(character, spell, null);
	}

	@Override
	public SpellStatistics getSpellStatistics(Character character, Spell spell, Attributes totalStats) {
		return getSnapshot(character, spell, totalStats)
				.getSpellStatistics(CritMode.AVERAGE, false);
	}

	@Override
	public Snapshot getSnapshot(Character character, Spell spell, Attributes totalStats) {
		spell = initOptional(character, spell);
		totalStats = initOptional(character, totalStats);

		return new Snapshot(spell, character, totalStats);
	}

	@Override
	public PlayerSpellStats getPlayerSpellStats(Character character, Spell spell) {
		SpellStatistics spellStatistics = getSpellStatistics(character, spell);
		double hitSpEqv = getSpEquivalent(SPELL_HIT_RATING, 10, character, spell);
		double critSpEqv = getSpEquivalent(SPELL_CRIT_RATING, 10, character, spell);
		double hasteSpEqv = getSpEquivalent(SPELL_HASTE_RATING, 10, character, spell);
		return new PlayerSpellStats(character, spellStatistics, hitSpEqv, critSpEqv, hasteSpEqv);
	}

	private double getSpEquivalent(PrimitiveAttributeId attributeId, int amount, Character character, Spell spell) {
		return getDpsStatEquivalent(
				Attributes.of(attributeId, amount),
				SPELL_POWER,
				EquivalentMode.ADDITIONAL,
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

	private Attributes getBaseAttributes(Attributes sourceAttributes, Attributes totalStats, EquivalentMode mode) {
		if (mode == EquivalentMode.REPLACEMENT) {
			return AttributesBuilder.removeAttributes(totalStats, sourceAttributes);
		} else {
			return totalStats;
		}
	}

	private double getTargetDps(Attributes attributesToFindEquivalent, Attributes totalStats, Character character, Spell spell, EquivalentMode mode) {
		if (mode == EquivalentMode.REPLACEMENT) {
			return getSpellStatistics(character, spell, totalStats).getDps();
		} else {
			return getSpellStatistics(
					character,
					spell,
					AttributesBuilder.addAttributes(totalStats, attributesToFindEquivalent)
			).getDps();
		}
	}
}
