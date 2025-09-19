package wow.simulator.script.command;

import wow.character.model.script.ScriptCommandCondition;
import wow.character.model.script.ScriptCommandTarget;
import wow.commons.model.AnyDuration;
import wow.commons.model.Duration;
import wow.commons.model.spell.Ability;
import wow.simulator.model.effect.EffectInstance;
import wow.simulator.model.unit.Player;
import wow.simulator.model.unit.Unit;

import java.util.List;

import static wow.character.model.script.ScriptCommand.*;
import static wow.commons.model.effect.component.ComponentType.DAMAGE;

/**
 * User: POlszewski
 * Date: 2025-09-18
 */
public abstract class ComposableExecutor extends ScriptCommandExecutor {
	protected final List<ScriptCommandCondition> commandConditions;
	protected final ScriptCommandTarget commandTarget;
	protected final Ability ability;

	protected ComposableExecutor(
			Player player,
			List<ScriptCommandCondition> commandConditions,
			Ability ability,
			ScriptCommandTarget commandTarget
	) {
		super(player);
		this.commandConditions = commandConditions;
		this.ability = ability;
		this.commandTarget = commandTarget;
	}

	public static ComposableExecutor create(ComposableCommand command, Player player) {
		return switch (command) {
			case CastSpell castSpell -> CastSpellExecutor.create(castSpell, player);
			case UseItem useItem -> UseItemExecutor.create(useItem, player);
		};
	}

	@Override
	public boolean isValid() {
		return ability != null;
	}

	@Override
	public boolean allConditionsAreMet() {
		var target = getTarget(commandTarget);

		return commandConditions.stream().allMatch(condition -> isConditionMet(condition, target)) &&
				player.canCast(ability, target) &&
				shouldCast(target);
	}

	@Override
	public void execute() {
		var target = getTarget(commandTarget);

		player.cast(ability.getAbilityId(), target);
	}

	private boolean isConditionMet(ScriptCommandCondition condition, Unit target) {
		return true;//todo
	}

	private boolean shouldCast(Unit target) {
		var remainingSimulationTime = player.getSimulation().getRemainingTime();
		var castTime = Duration.seconds(player.getSpellCastSnapshot(ability).getCastTime());

		if (castTime.compareTo(remainingSimulationTime) > 0) {
			return false;
		}

		if (!ability.hasPeriodicComponent(DAMAGE)) {
			return true;
		}

		var primaryTarget = player.getPrimaryTarget(ability, target);

		target = primaryTarget.requireSingleTarget();

		var remainingEffectDurationOnTarget = getRemainingEffectDuration(target);

		if (remainingEffectDurationOnTarget.compareTo(castTime) > 0) {
			return false;
		}

		var effectDuration = player.getEffectDurationSnapshot(ability, target).getDuration();

		return castTime.add(effectDuration).compareTo(remainingSimulationTime) <= 0;
	}

	private AnyDuration getRemainingEffectDuration(Unit target) {
		return target.getEffect(ability.getAbilityId(), player)
				.map(EffectInstance::getRemainingDuration)
				.orElse(Duration.ZERO);
	}
}
