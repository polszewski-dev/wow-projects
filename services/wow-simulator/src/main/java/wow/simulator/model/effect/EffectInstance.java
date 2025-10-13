package wow.simulator.model.effect;

import wow.commons.model.AnyDuration;
import wow.commons.model.Duration;
import wow.commons.model.effect.Effect;
import wow.commons.model.effect.EffectAugmentations;
import wow.commons.model.spell.AbilityId;
import wow.commons.model.spell.Spell;
import wow.commons.model.talent.TalentTree;
import wow.simulator.model.unit.Unit;
import wow.simulator.model.update.Updateable;
import wow.simulator.simulation.SimulationContextSource;

import java.util.regex.Pattern;

/**
 * User: POlszewski
 * Date: 2023-08-17
 */
public interface EffectInstance extends Effect, Updateable, SimulationContextSource {
	EffectInstanceId getInstanceId();

	Unit getOwner();

	Unit getTarget();

	boolean matches(AbilityId abilityId, Unit owner);

	boolean matches(AbilityId abilityId);

	boolean matches(TalentTree tree);

	boolean matches(Pattern effectNamePattern);

	boolean matches(Pattern effectNamePattern, Unit owner);

	AnyDuration getRemainingDuration();

	default double getRemainingDurationSeconds() {
		return ((Duration) getRemainingDuration()).getSeconds();
	}

	void removeSelf();

	void addStack();

	void removeStack();

	void addCharge();

	void removeCharge();

	AnyDuration getDuration();

	int getNumStacks();

	int getNumCharges();

	void setOnEffectFinished(Runnable onEffectFinished);

	Spell getSourceSpell();

	void augment(EffectAugmentations augmentations);

	void increaseEffect(double effectIncreasePct);

	boolean isTerminated();

	boolean isHidden();
}
