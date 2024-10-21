package wow.simulator.model.effect;

import wow.commons.model.Duration;
import wow.commons.model.effect.Effect;
import wow.commons.model.spell.Ability;
import wow.commons.model.spell.AbilityId;
import wow.simulator.model.unit.Unit;
import wow.simulator.model.update.Updateable;
import wow.simulator.simulation.SimulationContextSource;

/**
 * User: POlszewski
 * Date: 2023-08-17
 */
public interface UnitEffect extends Effect, Updateable, SimulationContextSource {
	UnitEffectId getId();

	Unit getOwner();

	Unit getTarget();

	Ability getSourceAbility();

	default AbilityId getSourceAbilityId() {
		return getSourceAbility().getAbilityId();
	}

	boolean matches(AbilityId abilityId, Unit owner);

	Duration getRemainingDuration();

	void removeSelf();

	Duration getDuration();

	int getNumStacks();

	void setOnEffectFinished(Runnable onEffectFinished);
}
