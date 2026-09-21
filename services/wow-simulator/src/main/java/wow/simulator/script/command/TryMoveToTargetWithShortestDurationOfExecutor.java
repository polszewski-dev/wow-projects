package wow.simulator.script.command;

import wow.character.model.script.ScriptCommandTarget;
import wow.commons.model.AnyDuration;
import wow.commons.model.Duration;
import wow.commons.model.spell.Ability;
import wow.simulator.model.unit.Unit;
import wow.simulator.script.ScriptParams;

import java.util.Comparator;

import static java.util.Comparator.comparing;
import static wow.character.model.script.ScriptCommand.TryMoveToTargetWithShortestDurationOf;

/**
 * User: POlszewski
 * Date: 2026-09-21
 */
public class TryMoveToTargetWithShortestDurationOfExecutor extends ComposableExecutor {
	private final Ability ability;

	private Unit newTarget;

	public TryMoveToTargetWithShortestDurationOfExecutor(TryMoveToTargetWithShortestDurationOf command, ScriptParams params) {
		super(params, command.condition(), ScriptCommandTarget.SELF, command.optional());
		this.ability = command.getAbility(params.caster());
	}

	public static TryMoveToTargetWithShortestDurationOfExecutor create(TryMoveToTargetWithShortestDurationOf command, ScriptParams params) {
		return new TryMoveToTargetWithShortestDurationOfExecutor(command, params);
	}

	@Override
	public boolean isValid() {
		return true;
	}

	@Override
	public boolean allConditionsAreMet() {
		newTarget = getEnemyWithShortestEffectDuration();

		return newTarget != caster.getTarget();
	}

	@Override
	public void execute() {
		caster.setTarget(newTarget);
		caster.emptyAction();
	}

	private Unit getEnemyWithShortestEffectDuration() {
		var allEnemies = caster.getAllEnemies();

		return allEnemies.stream()
				.min(byShortestEffectDuration())
				.orElseThrow();
	}

	private Comparator<Unit> byShortestEffectDuration() {
		return comparing(this::getRemainingEffectDuration).thenComparing(Unit::getId);
	}

	private AnyDuration getRemainingEffectDuration(Unit enemy) {
		return enemy.getRemainingEffectDuration(ability, caster).orElse(Duration.ZERO);
	}
}
