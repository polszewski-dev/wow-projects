package wow.minmax.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import wow.commons.model.attributes.Attribute;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.attributes.primitive.PrimitiveAttribute;
import wow.commons.model.attributes.primitive.PrimitiveAttributeId;
import wow.commons.model.unit.BaseStatInfo;
import wow.commons.model.unit.CombatRatingInfo;
import wow.commons.repository.PVERepository;
import wow.commons.util.AttributeEvaluator;
import wow.commons.util.Snapshot;
import wow.commons.util.SpellCalculations;
import wow.commons.util.SpellStatistics;
import wow.minmax.model.PlayerProfile;
import wow.minmax.model.Spell;
import wow.minmax.service.CalculationService;

import static wow.commons.model.attributes.primitive.PrimitiveAttributeId.SPELL_DAMAGE;
import static wow.commons.util.SpellCalculations.AVERAGE_CRIT;

/**
 * User: POlszewski
 * Date: 2021-12-15
 */
@Service
@AllArgsConstructor
public class CalculationServiceImpl implements CalculationService {
	private final PVERepository pveRepository;

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
		return getSpellStatistics(playerProfile, spell, (PrimitiveAttribute)null);
	}

	private SpellStatistics getSpellStatistics(PlayerProfile playerProfile, Spell spell, PrimitiveAttribute attribute) {
		AttributeEvaluator attributeEvaluator = AttributeEvaluator.of(spell.getSpellInfo())
				.addAttributes(playerProfile);

		if (attribute != null) {
			attributeEvaluator.addAttribute(attribute);
		}

		Attributes totalStats = attributeEvaluator
				.solveAllLeaveAbilities()
				.getAttributes();

		return getSpellStatistics(playerProfile, spell, totalStats);
	}

	@Override
	public SpellStatistics getSpellStatistics(PlayerProfile playerProfile, Spell spell, Attributes totalStats) {
		Snapshot snapshot = getSnapshot(playerProfile, spell, totalStats);

		return SpellCalculations.getDamage(
				snapshot.getSpellRankInfo().getMinDmg(),
				snapshot.getSpellRankInfo().getMaxDmg(),
				snapshot.getSpellRankInfo().getDotDmg(),
				snapshot,
				AVERAGE_CRIT
		);
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
}
