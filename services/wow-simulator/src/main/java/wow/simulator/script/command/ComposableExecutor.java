package wow.simulator.script.command;

import lombok.Getter;
import wow.character.model.script.ScriptCommandCondition;
import wow.character.model.script.ScriptCommandTarget;
import wow.commons.model.AnyDuration;
import wow.commons.model.Duration;
import wow.commons.model.spell.Ability;
import wow.simulator.model.effect.EffectInstance;
import wow.simulator.model.unit.Pet;
import wow.simulator.model.unit.Unit;
import wow.simulator.script.ScriptParams;

import static wow.character.model.script.ScriptCommand.*;

/**
 * User: POlszewski
 * Date: 2025-09-18
 */
public abstract class ComposableExecutor extends ScriptCommandExecutor {
	protected final ScriptCommandCondition commandCondition;
	protected final ScriptCommandTarget commandTarget;
	@Getter
	protected final boolean optional;

	protected ComposableExecutor(
			ScriptParams params,
			ScriptCommandCondition commandCondition,
			ScriptCommandTarget commandTarget,
			boolean optional
	) {
		super(params);
		this.commandCondition = commandCondition;
		this.commandTarget = commandTarget;
		this.optional = optional;
	}

	public static ComposableExecutor create(ComposableCommand command, ScriptParams params) {
		return switch (command) {
			case CastSpell castSpell -> CastSpellExecutor.create(castSpell, params);
			case CastSpellRank castSpellRank -> CastSpellRankExecutor.create(castSpellRank, params);
			case CastPetSpell castPetSpell -> CastPetSpellExecutor.create(castPetSpell, params);
			case UseItem useItem -> UseItemExecutor.create(useItem, params);
		};
	}

	protected abstract Ability getAbility();

	@Override
	public boolean allConditionsAreMet() {
		var target = getTarget(commandTarget);

		return isConditionMet(commandCondition, target) &&
				getActualCaster().canCast(getAbility(), target) &&
				shouldCast(target);
	}

	@Override
	public void execute() {
		var target = getTarget(commandTarget);

		getActualCaster().cast(getAbility(), target);
	}

	private boolean isConditionMet(ScriptCommandCondition condition, Unit target) {
		if (condition.isEmpty()) {
			return true;
		}

		var actualCaster = getActualCaster();
		var ability = getAbility();
		var primaryTarget = actualCaster.getPrimaryTarget(ability, target);
		var conditionChecker = new ScriptConditionChecker(actualCaster, ability, primaryTarget.requireSingleTarget());

		return conditionChecker.check(condition);
	}

	private boolean shouldCast(Unit target) {
		var actualCaster = getActualCaster();
		var ability = getAbility();
		var remainingSimulationTime = actualCaster.getSimulation().getRemainingTime();
		var castTime = Duration.seconds(actualCaster.getSpellCastSnapshot(ability, target).getCastTime());

		if (castTime.compareTo(remainingSimulationTime) > 0) {
			return false;
		}

		if (actualCaster instanceof Pet pet && castTime.compareTo(pet.getRemainingDuration()) > 0) {
			return false;
		}

		if (!ability.hasDamagingPeriodicComponent()) {
			return true;
		}

		var primaryTarget = actualCaster.getPrimaryTarget(ability, target);

		target = primaryTarget.requireSingleTarget();

		var remainingEffectDurationOnTarget = getRemainingEffectDuration(target);

		if (remainingEffectDurationOnTarget.compareTo(castTime) > 0) {
			return false;
		}

		var effectDuration = actualCaster.getEffectDurationSnapshot(ability, target).getDuration();

		return castTime.add(effectDuration).compareTo(remainingSimulationTime) <= 0;
	}

	private AnyDuration getRemainingEffectDuration(Unit target) {
		var actualCaster = getActualCaster();
		var ability = getAbility();

		return target.getEffect(ability.getAbilityId(), actualCaster)
				.map(EffectInstance::getRemainingDuration)
				.orElse(Duration.ZERO);
	}
}
