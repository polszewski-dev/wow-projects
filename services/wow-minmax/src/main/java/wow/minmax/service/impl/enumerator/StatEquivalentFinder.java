package wow.minmax.service.impl.enumerator;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import wow.character.model.build.Rotation;
import wow.minmax.model.AccumulatedRotationStats;
import wow.minmax.model.PlayerCharacter;
import wow.minmax.model.SpecialAbility;
import wow.minmax.service.CalculationService;
import wow.minmax.util.EffectList;

import java.util.function.DoubleUnaryOperator;

/**
 * User: POlszewski
 * Date: 2023-03-20
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class StatEquivalentFinder {
	private final double targetDps;
	private final PlayerCharacter character;
	private final Rotation rotation;

	private final AccumulatedRotationStats rotationStats;

	private final CalculationService calculationService;

	public static StatEquivalentFinder forAdditionalSpecialAbility(
			SpecialAbility specialAbility,
			PlayerCharacter character,
			Rotation rotation,
			CalculationService calculationService
	) {
		var totalStats = EffectList.createSolved(character);
		var baseEffectList = totalStats.copy();
		var targetEffectList = EffectList.createSolvedForTarget(character);
		var targetStats = withAbility(specialAbility, totalStats);
		var targetDps = calculationService.getRotationDps(character, rotation, targetStats, EffectList.createSolvedForTarget(character));
		var rotationStats = calculationService.getAccumulatedRotationStats(character, rotation, baseEffectList, targetEffectList);

		return new StatEquivalentFinder(targetDps, character, rotation, rotationStats, calculationService);
	}

	public static StatEquivalentFinder forReplacedSpecialAbility(
			SpecialAbility specialAbility,
			PlayerCharacter character,
			Rotation rotation,
			CalculationService calculationService
	) {
		var totalStats = EffectList.createSolved(character);
		var baseEffectList = withoutAbility(specialAbility, totalStats);
		var targetEffectList = EffectList.createSolvedForTarget(character);
		var targetStats =  totalStats.copy();
		var targetDps = calculationService.getRotationDps(character, rotation, targetStats, EffectList.createSolvedForTarget(character));
		var rotationStats = calculationService.getAccumulatedRotationStats(character, rotation, baseEffectList, targetEffectList);

		return new StatEquivalentFinder(targetDps, character, rotation, rotationStats, calculationService);
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
			PlayerCharacter character,
			Rotation rotation,
			CalculationService calculationService
	) {
		var targetEffectList = EffectList.createSolvedForTarget(character);
		var rotationStats = calculationService.getAccumulatedRotationStats(character, rotation, baseEffectList, targetEffectList);

		return new StatEquivalentFinder(targetDps, character, rotation, rotationStats, calculationService);
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
		return calculationService.getRotationDps(character, rotation, copy);
	}
}
