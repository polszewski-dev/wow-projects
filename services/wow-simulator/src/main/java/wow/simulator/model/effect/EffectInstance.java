package wow.simulator.model.effect;

import wow.commons.model.Duration;
import wow.commons.model.effect.Effect;
import wow.commons.model.spell.AbilityId;
import wow.commons.model.spell.Spell;
import wow.simulator.model.unit.Unit;
import wow.simulator.model.update.Updateable;
import wow.simulator.simulation.SimulationContextSource;

/**
 * User: POlszewski
 * Date: 2023-08-17
 */
public interface EffectInstance extends Effect, Updateable, SimulationContextSource {
	EffectInstanceId getId();

	Unit getOwner();

	Unit getTarget();

	boolean matches(AbilityId abilityId, Unit owner);

	Duration getRemainingDuration();

	void removeSelf();

	Duration getDuration();

	int getNumStacks();

	void setOnEffectFinished(Runnable onEffectFinished);

	Spell getSourceSpell();
}