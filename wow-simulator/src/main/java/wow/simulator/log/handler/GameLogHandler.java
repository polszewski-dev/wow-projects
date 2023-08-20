package wow.simulator.log.handler;

import wow.commons.model.spells.ResourceType;
import wow.commons.model.spells.Spell;
import wow.simulator.model.action.Action;
import wow.simulator.model.cooldown.Cooldown;
import wow.simulator.model.effect.Effect;
import wow.simulator.model.unit.Unit;

/**
 * User: POlszewski
 * Date: 2023-08-07
 */
public interface GameLogHandler {
	void beginGcd(Unit caster, Action sourceAction);

	void endGcd(Unit caster, Action sourceAction);

	void beginCast(Unit caster, Spell spell, Unit target, Action action);

	void endCast(Unit caster, Spell spell, Unit target, Action action);

	void canNotBeCasted(Unit caster, Spell spell, Unit target, Action action);

	void castInterrupted(Unit caster, Spell spell, Unit target, Action action);

	void spellMissed(Unit caster, Spell spell, Unit target, Action action);

	void increasedResource(ResourceType type, Spell spell, Unit target, int amount, int current, int previous, boolean crit);

	void decreasedResource(ResourceType type, Spell spell, Unit target, int amount, int current, int previous, boolean crit);

	void effectApplied(Effect effect);

	void effectStacked(Effect effect);

	void effectExpired(Effect effect);

	void effectRemoved(Effect effect);

	void cooldownStarted(Cooldown cooldown);

	void cooldownExpired(Cooldown cooldown);

	void simulationStarted();

	void simulationEnded();
}
