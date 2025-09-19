package wow.estimator.util;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import wow.estimator.model.*;
import wow.estimator.service.CalculationService;

import java.util.function.DoubleUnaryOperator;

/**
 * User: POlszewski
 * Date: 2023-03-20
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class StatEquivalentFinder {
	private final double targetDps;
	private final Player player;
	private final Rotation rotation;

	private final AccumulatedRotationStats rotationStats;

	private final CalculationService calculationService;

	public static StatEquivalentFinder forAdditionalSpecialAbility(
			SpecialAbility specialAbility,
			Player player,
			Rotation rotation,
			CalculationService calculationService
	) {
		var totalStats = EffectList.createSolved(player);
		var baseEffectList = totalStats.copy();
		var targetEffectList = EffectList.createSolvedForTarget(player);
		var targetStats = withAbility(specialAbility, totalStats);
		var targetDps = calculationService.getRotationDps(player, rotation, targetStats, EffectList.createSolvedForTarget(player));
		var rotationStats = calculationService.getAccumulatedRotationStats(player, rotation, baseEffectList, targetEffectList);

		return new StatEquivalentFinder(targetDps, player, rotation, rotationStats, calculationService);
	}

	public static StatEquivalentFinder forReplacedSpecialAbility(
			SpecialAbility specialAbility,
			Player player,
			Rotation rotation,
			CalculationService calculationService
	) {
		var totalStats = EffectList.createSolved(player);
		var baseEffectList = withoutAbility(specialAbility, totalStats);
		var targetEffectList = EffectList.createSolvedForTarget(player);
		var targetStats =  totalStats.copy();
		var targetDps = calculationService.getRotationDps(player, rotation, targetStats, EffectList.createSolvedForTarget(player));
		var rotationStats = calculationService.getAccumulatedRotationStats(player, rotation, baseEffectList, targetEffectList);

		return new StatEquivalentFinder(targetDps, player, rotation, rotationStats, calculationService);
	}

	private static EffectList withAbility(SpecialAbility specialAbility, EffectList totalStats) {
		var copy = totalStats.copy();
		copy.addEffect(specialAbility);
		return copy;
	}

	private static EffectList withoutAbility(SpecialAbility specialAbility, EffectList totalStats) {
		var copy = totalStats.copy();
		copy.removeEffect(specialAbility);
		return copy;
	}

	public static StatEquivalentFinder forTargetDps(
			EffectList baseEffectList,
			double targetDps,
			Player player,
			Rotation rotation,
			CalculationService calculationService
	) {
		var targetEffectList = EffectList.createSolvedForTarget(player);
		var rotationStats = calculationService.getAccumulatedRotationStats(player, rotation, baseEffectList, targetEffectList);

		return new StatEquivalentFinder(targetDps, player, rotation, rotationStats, calculationService);
	}

	public double getDpsStatEquivalent() {
		return findPointA(targetDps, this::getRotationDps);
	}

	private double findPointA(double fa, DoubleUnaryOperator f) {
		var a1 = findPointA(fa, 10, f);
		var a2 = findPointA(fa, a1, f);

		var errorA1 = Math.abs(fa - f.applyAsDouble(a1));
		var errorA2 = Math.abs(fa - f.applyAsDouble(a2));

		return errorA1 < errorA2 ? a1 : a2;
	}

	private double findPointA(double fa, double b, DoubleUnaryOperator f) {
		var f0 = f.applyAsDouble(0);
		var fb = f.applyAsDouble(b);

		return b * (fa - f0) / (fb - f0);
	}

	private double getRotationDps(double increase) {
		var copy = rotationStats.copy();

		copy.increasePower(increase);
		return calculationService.getRotationDps(player, rotation, copy);
	}
}
