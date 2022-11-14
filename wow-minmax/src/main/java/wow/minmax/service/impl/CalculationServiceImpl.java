package wow.minmax.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import wow.commons.model.attributes.Attribute;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.attributes.complex.SpecialAbility;
import wow.commons.model.attributes.primitive.PrimitiveAttribute;
import wow.commons.model.attributes.primitive.PrimitiveAttributeId;
import wow.commons.model.unit.BaseStatInfo;
import wow.commons.model.unit.CombatRatingInfo;
import wow.commons.repository.PVERepository;
import wow.commons.util.AttributeEvaluator;
import wow.commons.util.AttributesBuilder;
import wow.commons.util.Snapshot;
import wow.commons.util.SpellStatistics;
import wow.minmax.model.PlayerProfile;
import wow.minmax.model.PlayerSpellStats;
import wow.minmax.model.Spell;
import wow.minmax.service.CalculationService;

import java.util.ArrayList;
import java.util.List;

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

	@Override
	public Attributes getStatEquivalent(SpecialAbility specialAbility, PlayerProfile playerProfile, Attributes totalStats) {
		List<SpecialAbility> withoutGivenSpecialAbility = new ArrayList<>(totalStats.getSpecialAbilities());

		withoutGivenSpecialAbility.remove(specialAbility);

		Attributes attributesWithoutGivenSpecialAbility = new AttributesBuilder()
				.addAttributeList(totalStats.getPrimitiveAttributeList())
				.addComplexAttributeList(withoutGivenSpecialAbility)
				.toAttributes();

		Snapshot snapshot = getSnapshot(playerProfile, playerProfile.getDamagingSpell(), attributesWithoutGivenSpecialAbility);

		return specialAbility.getStatEquivalent(snapshot);
	}

	@Override
	public double getSpEquivalent(PrimitiveAttributeId attributeId, int amount, PlayerProfile playerProfile, Spell spell) {
		playerProfile = playerProfile.copy();

		PrimitiveAttribute base = Attribute.of(attributeId, amount);
		double targetDps = getSpellStatistics(playerProfile, spell, base).getDps();
		double totalSp = 0;
		double increase = 1;

		while (true) {
			PrimitiveAttribute spEquivalent = Attribute.of(SPELL_DAMAGE, totalSp + increase);
			double spEqvDps = getSpellStatistics(playerProfile, spell, spEquivalent).getDps();

			if (Math.abs(spEqvDps - targetDps) <= 0.001) {
				return totalSp + increase;
			}

			if (spEqvDps > targetDps) {
				increase /= 2;
			} else {
				totalSp += increase;
			}
		}
	}

	@Override
	public SpellStatistics getSpellStatistics(PlayerProfile playerProfile, Spell spell) {
		Attributes totalStats = AttributeEvaluator.of()
				.addAttributes(playerProfile)
				.solveAllLeaveAbilities();

		return getSpellStatistics(playerProfile, spell, totalStats);
	}

	private SpellStatistics getSpellStatistics(PlayerProfile playerProfile, Spell spell, PrimitiveAttribute attribute) {
		Attributes totalStats = AttributeEvaluator.of()
				.addAttributes(playerProfile)
				.addAttribute(attribute)
				.solveAllLeaveAbilities();

		return getSpellStatistics(playerProfile, spell, totalStats);
	}

	@Override
	public SpellStatistics getSpellStatistics(PlayerProfile playerProfile, Spell spell, Attributes totalStats) {
		return getSnapshot(playerProfile, spell, totalStats)
				.getSpellStatistics(CritMode.AVERAGE, false);
	}

	@Override
	public Snapshot getSnapshot(PlayerProfile playerProfile, Spell spell, Attributes totalStats) {
		BaseStatInfo baseStats = pveRepository.getBaseStats(playerProfile.getCharacterClass(), playerProfile.getRace(), playerProfile.getLevel()).orElseThrow();
		CombatRatingInfo cr = pveRepository.getCombatRatings(playerProfile.getLevel()).orElseThrow();

		return new Snapshot(
				spell.getSpellInfo(),
				spell.getSpellRankInfo(playerProfile.getLevel()),
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
}
