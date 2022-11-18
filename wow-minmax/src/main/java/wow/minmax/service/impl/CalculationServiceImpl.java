package wow.minmax.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import wow.commons.model.attributes.Attribute;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.attributes.complex.SpecialAbility;
import wow.commons.model.attributes.primitive.PrimitiveAttribute;
import wow.commons.model.attributes.primitive.PrimitiveAttributeId;
import wow.commons.model.spells.Spell;
import wow.commons.model.unit.BaseStatInfo;
import wow.commons.model.unit.CombatRatingInfo;
import wow.commons.repository.PVERepository;
import wow.commons.util.AttributesBuilder;
import wow.commons.util.Snapshot;
import wow.commons.util.SpellStatistics;
import wow.minmax.model.PlayerProfile;
import wow.minmax.model.PlayerSpellStats;
import wow.minmax.service.CalculationService;

import static wow.commons.model.attributes.primitive.PrimitiveAttributeId.*;
import static wow.commons.util.Snapshot.CritMode;

/**
 * User: POlszewski
 * Date: 2021-12-15
 */
@Service
@AllArgsConstructor
public class CalculationServiceImpl implements CalculationService {
	private final PVERepository pveRepository;

	private static final double PRECISION = 0.0001;

	@Override
	public Attributes getDpsStatEquivalent(Attributes attributesToFindEquivalent, PrimitiveAttributeId targetStat, EquivalentMode mode, PlayerProfile playerProfile) {
		return getDpsStatEquivalent(attributesToFindEquivalent, targetStat, mode, playerProfile, null, null);
	}

	@Override
	public Attributes getDpsStatEquivalent(
			Attributes attributesToFindEquivalent,
			PrimitiveAttributeId targetStat,
			EquivalentMode mode,
			PlayerProfile playerProfile,
			Spell spell,
			Attributes totalStats
	) {
		spell = initOptional(playerProfile, spell);
		totalStats = initOptional(playerProfile, totalStats);

		Attributes baseStats = getBaseAttributes(attributesToFindEquivalent, totalStats, mode);
		double targetDps = getTargetDps(attributesToFindEquivalent, totalStats, playerProfile, spell, mode);

		double equivalentValue = 0;
		double increase = 1;

		while (true) {
			PrimitiveAttribute equivalentStat = Attribute.of(targetStat, equivalentValue + increase);
			Attributes equivalentStats = AttributesBuilder.addAttribute(baseStats, equivalentStat);
			double equivalentDps = getSpellStatistics(playerProfile, spell, equivalentStats).getDps();

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
	public Attributes getAbilityEquivalent(SpecialAbility specialAbility, PlayerProfile playerProfile, Spell spell, Attributes totalStats) {
		spell = initOptional(playerProfile, spell);
		totalStats = initOptional(playerProfile, totalStats);

		Snapshot snapshot = getSnapshot(
				playerProfile,
				spell,
				AttributesBuilder.removeAttributes(totalStats, Attributes.of(specialAbility))
		);

		return specialAbility.getStatEquivalent(snapshot);
	}

	@Override
	public SpellStatistics getSpellStatistics(PlayerProfile playerProfile, Spell spell) {
		return getSpellStatistics(playerProfile, spell, null);
	}

	@Override
	public SpellStatistics getSpellStatistics(PlayerProfile playerProfile, Spell spell, Attributes totalStats) {
		return getSnapshot(playerProfile, spell, totalStats)
				.getSpellStatistics(CritMode.AVERAGE, false);
	}

	@Override
	public Snapshot getSnapshot(PlayerProfile playerProfile, Spell spell, Attributes totalStats) {
		spell = initOptional(playerProfile, spell);
		totalStats = initOptional(playerProfile, totalStats);

		BaseStatInfo baseStats = pveRepository.getBaseStats(playerProfile.getCharacterClass(), playerProfile.getRace(), playerProfile.getLevel()).orElseThrow();
		CombatRatingInfo cr = pveRepository.getCombatRatings(playerProfile.getLevel()).orElseThrow();

		return new Snapshot(
				spell,
				baseStats,
				cr,
				totalStats,
				playerProfile.getActivePet(),
				playerProfile.getEnemyType()
		);
	}

	@Override
	public PlayerSpellStats getPlayerSpellStats(PlayerProfile playerProfile, Spell spell) {
		SpellStatistics spellStatistics = getSpellStatistics(playerProfile, spell);
		double hitSpEqv = getSpEquivalent(SPELL_HIT_RATING, 10, playerProfile, spell);
		double critSpEqv = getSpEquivalent(SPELL_CRIT_RATING, 10, playerProfile, spell);
		double hasteSpEqv = getSpEquivalent(SPELL_HASTE_RATING, 10, playerProfile, spell);
		return new PlayerSpellStats(playerProfile, spellStatistics, hitSpEqv, critSpEqv, hasteSpEqv);
	}

	private double getSpEquivalent(PrimitiveAttributeId attributeId, int amount, PlayerProfile playerProfile, Spell spell) {
		return getDpsStatEquivalent(
				Attributes.of(attributeId, amount),
				SPELL_POWER,
				EquivalentMode.ADDITIONAL,
				playerProfile, spell, playerProfile.getStats()
		).getSpellPower();
	}

	private Spell initOptional(PlayerProfile playerProfile, Spell spell) {
		if (spell != null) {
			return spell;
		}
		return playerProfile.getDamagingSpell();
	}

	private Attributes initOptional(PlayerProfile playerProfile, Attributes totalStats) {
		if (totalStats != null) {
			return totalStats;
		}
		return playerProfile.getStats();
	}

	private Attributes getBaseAttributes(Attributes sourceAttributes, Attributes totalStats, EquivalentMode mode) {
		if (mode == EquivalentMode.REPLACEMENT) {
			return AttributesBuilder.removeAttributes(totalStats, sourceAttributes);
		} else {
			return totalStats;
		}
	}

	private double getTargetDps(Attributes attributesToFindEquivalent, Attributes totalStats, PlayerProfile playerProfile, Spell spell, EquivalentMode mode) {
		if (mode == EquivalentMode.REPLACEMENT) {
			return getSpellStatistics(playerProfile, spell, totalStats).getDps();
		} else {
			return getSpellStatistics(
					playerProfile,
					spell,
					AttributesBuilder.addAttributes(totalStats, attributesToFindEquivalent)
			).getDps();
		}
	}
}
