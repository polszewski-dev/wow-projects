package wow.simulator.log.handler;

import wow.commons.model.spells.ResourceType;
import wow.commons.model.spells.Spell;
import wow.commons.model.spells.SpellId;
import wow.simulator.model.action.Action;
import wow.simulator.model.effect.Effect;
import wow.simulator.model.unit.Unit;

/**
 * User: POlszewski
 * Date: 2023-08-07
 */
public interface GameLogHandler {
	void beginGcd(Unit caster, Action action);

	void endGcd(Unit caster, Action action);

	void beginCast(Unit caster, Spell spell, Unit target, Action action);

	void endCast(Unit caster, Spell spell, Unit target, Action action);

	void canNotBeCasted(Unit caster, Spell spell, Unit target, Action action);

	void castInterrupted(Unit caster, Spell spell, Unit target, Action action);

	void spellMissed(Unit caster, Spell spell, Unit target);

	void increasedResource(ResourceType type, Spell spell, Unit target, int amount, int current, int previous, boolean crit);

	void decreasedResource(ResourceType type, Spell spell, Unit target, int amount, int current, int previous, boolean crit);

	void effectApplied(Effect effect);

	void effectStacked(Effect effect);

	void effectExpired(Effect effect);

	void effectRemoved(Effect effect);

	void cooldownStarted(Unit caster, SpellId spellId);

	void cooldownExpired(Unit caster, SpellId spellId);

	void simulationEnded();
}
