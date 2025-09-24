package wow.estimator.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import wow.commons.model.spell.AbilityId;
import wow.commons.model.talent.Talent;
import wow.estimator.model.*;
import wow.estimator.service.CalculationService;
import wow.estimator.service.StatsService;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static wow.commons.model.buff.BuffCategory.*;

/**
 * User: POlszewski
 * Date: 2025-03-17
 */
@Service
@AllArgsConstructor
public class StatsServiceImpl implements StatsService {
	private final CalculationService calculationService;

	@Override
	public List<AbilityStats> getAbilityStats(Player player, List<AbilityId> abilityIds, boolean usesCombatRatings, double equivalentAmount) {
		return abilityIds.stream()
				.map(player::getAbility)
				.flatMap(Optional::stream)
				.filter(player::canCast)
				.map(ability -> calculationService.getAbilityStats(player, ability, usesCombatRatings, equivalentAmount))
				.toList();
	}

	@Override
	public CharacterStats getCharacterStats(Player player, boolean worldBuffsAllowed) {
		var current = calculationService.getCurrentStats(player);
		var equipment = calculationService.getEquipmentStats(player);
		var noBuff = calculationService.getStats(player);
		var selfBuff = calculationService.getStats(player, SELF_BUFF);
		var partyBuff = calculationService.getStats(player, SELF_BUFF, PARTY_BUFF);
		var partyBuffAndConsumes = calculationService.getStats(player, SELF_BUFF, PARTY_BUFF, CONSUME);
		var raidBuffAndConsumes = calculationService.getStats(player, SELF_BUFF, PARTY_BUFF, RAID_BUFF, CONSUME);
		var worldBuffAndConsumes = worldBuffsAllowed ? calculationService.getStats(player, SELF_BUFF, PARTY_BUFF, RAID_BUFF, WORLD_BUFF, CONSUME) : null;

		return new CharacterStats(
				current,
				equipment,
				noBuff,
				selfBuff,
				partyBuff,
				partyBuffAndConsumes,
				raidBuffAndConsumes,
				worldBuffAndConsumes
		);
	}

	@Override
	public List<SpecialAbilityStats> getSpecialAbilityStats(Player player) {
		return getSpecialAbilities(player)
				.sorted(compareSources())
				.map(specialAbility -> calculationService.getSpecialAbilityStats(specialAbility, player))
				.toList();
	}

	private Stream<SpecialAbility> getSpecialAbilities(Player player) {
		var rotationStats = calculationService.getAccumulatedRotationStats(player, player.getRotation());

		return Stream.concat(
				rotationStats.getActivatedAbilities().stream().map(SpecialAbility::of),
				rotationStats.getNonModifierEffects().stream().map(SpecialAbility::of)
		);
	}

	private Comparator<SpecialAbility> compareSources() {
		return Comparator.comparingInt((SpecialAbility x) -> x.getSource().getPriority())
						.thenComparing((SpecialAbility x) -> (Comparable<Object>)x.getSource())
						.thenComparingInt(SpecialAbility::getPriority)
						.thenComparing(SpecialAbility::getTooltip);
	}

	@Override
	public RotationStats getRotationStats(Player player, Rotation rotation) {
		return calculationService.getRotationStats(player, rotation);
	}

	@Override
	public List<TalentStats> getTalentStats(Player player) {
		return player.getTalents().getStream()
				.map(x -> getTalentStats(x, player))
				.toList();
	}

	private TalentStats getTalentStats(Talent talent, Player player) {
		var spEquivalent = calculationService.getTalentSpEquivalent(talent.getName(), player);

		return new TalentStats(
				talent,
				talent.getEffect().getTooltip(),
				spEquivalent
		);
	}
}
