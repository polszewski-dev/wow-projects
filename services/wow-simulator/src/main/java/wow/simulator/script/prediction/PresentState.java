package wow.simulator.script.prediction;

import wow.character.model.script.ScriptCommandCondition;
import wow.commons.model.Duration;
import wow.commons.model.spell.Ability;
import wow.commons.model.spell.AbilityId;
import wow.simulator.model.unit.Pet;
import wow.simulator.model.unit.Unit;
import wow.simulator.script.command.ComposableExecutor;

import java.util.Comparator;
import java.util.regex.Pattern;

import static java.util.Comparator.comparing;

/**
 * User: POlszewski
 * Date: 2026-10-30
 */
public class PresentState implements PredictedState {
	private final Unit caster;
	private final Unit target;

	public PresentState(Unit caster) {
		this.caster = caster;
		this.target = caster.getTarget();
	}

	@Override
	public boolean allConditionsAreMet(ScriptCommandCondition commandCondition, Ability ability, Unit target) {
		return isConditionMet(commandCondition, ability, target) &&
				canCast(ability, target) &&
				shouldCast(ability, target);
	}

	private boolean isConditionMet(ScriptCommandCondition condition, Ability ability, Unit target) {
		if (condition.isEmpty()) {
			return true;
		}

		var primaryTarget = caster.getPrimaryTarget(ability, target);
		var conditionChecker = new ScriptConditionChecker(this, caster, ability, primaryTarget.requireSingleTarget());

		return conditionChecker.check(condition);
	}

	private boolean canCast(Ability ability, Unit target) {
		return caster.canCast(ability, target);
	}

	private boolean shouldCast(Ability ability, Unit target) {
		var remainingSimulationDuration = getRemainingSimulationDuration();
		var castTime = getCastTime(ability, target);

		if (castTime > remainingSimulationDuration) {
			return false;
		}

		if (caster instanceof Pet pet && castTime > pet.getRemainingDuration().getSeconds()) {
			return false;
		}

		if (!ability.hasDamagingPeriodicComponent() || canIgnorePeriodicComponent(ability)) {
			return true;
		}

		var primaryTarget = caster.getPrimaryTarget(ability, target);

		target = primaryTarget.requireSingleTarget();

		var remainingEffectDurationOnTarget = getRemainingEffectDuration(ability, target);

		if (remainingEffectDurationOnTarget > castTime) {
			return false;
		}

		var effectDuration = getEffectDuration(ability, target);

		return castTime + effectDuration <= remainingSimulationDuration;
	}

	private boolean canIgnorePeriodicComponent(Ability ability) {
		return ability.getName().equals(FIREBALL);
	}

	private double getEffectDuration(Ability ability, Unit target) {
		if (ability.getName().equals(SEED_OF_CORRUPTION)) {
			return EXPECTED_SOC_DURATION;
		}

		return getFullDuration(ability, target);
	}

	private static final String FIREBALL = "Fireball";
	private static final String SEED_OF_CORRUPTION = "Seed of Corruption";
	private static final int EXPECTED_SOC_DURATION = 2;

	private double getRemainingEffectDuration(Ability ability, Unit target) {
		return target.getRemainingEffectDuration(ability, caster)
				.orElse(Duration.ZERO)
				.getSeconds();
	}

	@Override
	public boolean casterHasEffect(Pattern namePattern) {
		return caster.hasEffect(namePattern);
	}

	@Override
	public boolean targetHasEffect(Pattern namePattern) {
		return target.hasEffect(namePattern, caster);
	}

	@Override
	public boolean canCastMoreBeforeSimulationEnds(AbilityId abilityId, Unit primaryTarget) {
		var ability = caster.getAbility(abilityId).orElse(null);

		if (ability == null) {
			return false;
		}

		var remainingCooldown = getRemainingCooldown(abilityId);
		var castTime = getCastTime(ability, primaryTarget);
		var fullDuration = getFullDuration(ability, primaryTarget);
		var remainingSimulationDuration = getRemainingSimulationDuration();

		return remainingCooldown + castTime + fullDuration <= remainingSimulationDuration;
	}

	private double getCastTime(Ability ability, Unit primaryTarget) {
		var snapshot = caster.getSpellCastSnapshot(ability, primaryTarget);

		return snapshot.getCastTime();
	}

	@Override
	public double getFullDuration(Ability ability, Unit primaryTarget) {
		var snapshot = caster.getEffectDurationSnapshot(ability, primaryTarget);

		return snapshot.getDuration().getSeconds();
	}

	@Override
	public double getRemainingEffectDurationOfAnyOnTarget(String effectName) {
		var effectDuration = target.getRemainingEffectDuration(effectName).orElse(Duration.ZERO);

		return effectDuration.getSeconds();
	}

	@Override
	public double getRemainingCooldown(AbilityId abilityId) {
		return caster.getRemainingCooldown(abilityId).getSeconds();
	}

	@Override
	public double getRemainingSimulationDuration() {
		return caster.getSimulation().getRemainingTime().getSeconds();
	}

	@Override
	public int getCasterHealth() {
		return caster.getCurrentHealth();
	}

	@Override
	public int getCasterMana() {
		return caster.getCurrentMana();
	}

	@Override
	public double getCasterHealthPct() {
		return caster.getHealthPct().value();
	}

	@Override
	public double getCasterManaPct() {
		return caster.getManaPct().value();
	}

	@Override
	public double getTargetHealthPct() {
		return target.getHealthPct().value();
	}

	@Override
	public double getTargetManaPct() {
		return target.getManaPct().value();
	}

	@Override
	public double getFocusHealthPct() {
		var focus = caster.getFocus();

		return focus != null ? focus.getHealthPct().value() : 0;
	}

	@Override
	public double getFocusManaPct() {
		var focus = caster.getFocus();

		return focus != null ? focus.getManaPct().value() : 0;
	}

	@Override
	public double getPetHealthPct() {
		var pet = caster.getActivePet();

		return pet != null ? pet.getHealthPct().value() : 0;
	}

	@Override
	public double getPetManaPct() {
		var pet = caster.getActivePet();

		return pet != null ? pet.getManaPct().value() : 0;
	}

	@Override
	public double getMasterHealthPct() {
		return caster.getMaster().getHealthPct().value();
	}

	@Override
	public double getMasterManaPct() {
		return caster.getMaster().getManaPct().value();
	}

	@Override
	public int getNumStacksOfAnyOnTarget(String effectName) {
		return target.getNumStacks(effectName);
	}

	@Override
	public Unit getEnemyWithShortestEffectDuration(Ability ability) {
		var allEnemies = caster.getAllEnemies();

		return allEnemies.stream()
				.min(byShortestEffectDuration(ability))
				.orElseThrow();
	}

	private Comparator<Unit> byShortestEffectDuration(Ability ability) {
		return comparing((Unit enemy) -> getRemainingEffectDuration(ability, enemy))
					.thenComparing(Unit::getId);
	}

	@Override
	public PredictedState afterExecuting(ComposableExecutor executor) {
		return this;
	}
}
