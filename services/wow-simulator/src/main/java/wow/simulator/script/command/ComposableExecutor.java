package wow.simulator.script.command;

import lombok.Getter;
import wow.character.model.script.ScriptCommandCondition;
import wow.character.model.script.ScriptCommandTarget;
import wow.commons.model.AnyDuration;
import wow.commons.model.Duration;
import wow.commons.model.spell.Ability;
import wow.simulator.model.effect.EffectInstance;
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
	protected final Ability ability;
	@Getter
	protected final boolean optional;

	protected ComposableExecutor(
			ScriptParams params,
			ScriptCommandCondition commandCondition,
			Ability ability,
			ScriptCommandTarget commandTarget,
			boolean optional
	) {
		super(params);
		this.commandCondition = commandCondition;
		this.ability = ability;
		this.commandTarget = commandTarget;
		this.optional = optional;
	}

	public static ComposableExecutor create(ComposableCommand command, ScriptParams params) {
		return switch (command) {
			case CastSpell castSpell -> CastSpellExecutor.create(castSpell, params);
			case CastSpellRank castSpellRank -> CastSpellRankExecutor.create(castSpellRank, params);
			case UseItem useItem -> UseItemExecutor.create(useItem, params);
		};
	}

	@Override
	public boolean isValid() {
		return ability != null;
	}

	@Override
	public boolean allConditionsAreMet() {
		var target = getTarget(commandTarget);

		return isConditionMet(commandCondition, target) &&
				caster.canCast(ability, target) &&
				shouldCast(target);
	}

	@Override
	public void execute() {
		var target = getTarget(commandTarget);

		caster.cast(ability.getAbilityId(), target);
	}

	private boolean isConditionMet(ScriptCommandCondition condition, Unit target) {
		if (condition.isEmpty()) {
			return true;
		}

		var primaryTarget = caster.getPrimaryTarget(ability, target);
		var conditionChecker = new ScriptConditionChecker(caster, ability, primaryTarget.requireSingleTarget());

		return conditionChecker.check(condition);
	}

	private boolean shouldCast(Unit target) {
		var remainingSimulationTime = caster.getSimulation().getRemainingTime();
		var castTime = Duration.seconds(caster.getSpellCastSnapshot(ability).getCastTime());

		if (castTime.compareTo(remainingSimulationTime) > 0) {
			return false;
		}

		if (!ability.hasDamagingPeriodicComponent()) {
			return true;
		}

		var primaryTarget = caster.getPrimaryTarget(ability, target);

		target = primaryTarget.requireSingleTarget();

		var remainingEffectDurationOnTarget = getRemainingEffectDuration(target);

		if (remainingEffectDurationOnTarget.compareTo(castTime) > 0) {
			return false;
		}

		var effectDuration = caster.getEffectDurationSnapshot(ability, target).getDuration();

		return castTime.add(effectDuration).compareTo(remainingSimulationTime) <= 0;
	}

	private AnyDuration getRemainingEffectDuration(Unit target) {
		return target.getEffect(ability.getAbilityId(), caster)
				.map(EffectInstance::getRemainingDuration)
				.orElse(Duration.ZERO);
	}
}
