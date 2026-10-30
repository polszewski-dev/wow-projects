package wow.simulator.script.prediction;

import wow.character.model.script.ScriptCommandCondition;
import wow.commons.model.spell.Ability;
import wow.commons.model.spell.AbilityId;
import wow.simulator.model.unit.Unit;
import wow.simulator.script.command.ComposableExecutor;

import java.util.regex.Pattern;

/**
 * User: POlszewski
 * Date: 2026-10-30
 */
public interface PredictedState {
	boolean allConditionsAreMet(ScriptCommandCondition commandCondition, Ability ability, Unit target);

	boolean casterHasEffect(Pattern namePattern);

	boolean targetHasEffect(Pattern namePattern);

	boolean canCastMoreBeforeSimulationEnds(AbilityId abilityId, Unit primaryTarget);

	double getFullDuration(Ability ability, Unit primaryTarget);

	double getRemainingEffectDurationOfAnyOnTarget(String effectName);

	double getRemainingCooldown(AbilityId abilityId);

	double getRemainingSimulationDuration();

	int getCasterHealth();

	int getCasterMana();

	double getCasterHealthPct();

	double getCasterManaPct();

	double getTargetHealthPct();

	double getTargetManaPct();

	double getFocusHealthPct();

	double getFocusManaPct();

	double getPetHealthPct();

	double getPetManaPct();

	double getMasterHealthPct();

	double getMasterManaPct();

	int getNumStacksOfAnyOnTarget(String effectName);

	Unit getEnemyWithShortestEffectDuration(Ability ability);

	PredictedState afterExecuting(ComposableExecutor executor);
}
