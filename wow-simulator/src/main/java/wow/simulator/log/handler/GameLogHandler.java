package wow.simulator.log.handler;

import wow.commons.model.spells.ResourceType;
import wow.commons.model.spells.Spell;
import wow.simulator.model.action.ActionId;
import wow.simulator.model.unit.Unit;

/**
 * User: POlszewski
 * Date: 2023-08-07
 */
public interface GameLogHandler {
	void beginGcd(Unit caster, Spell spell, Unit target, ActionId actionId);

	void endGcd(Unit caster, Spell spell, Unit target, ActionId actionId);

	void beginCast(Unit caster, Spell spell, Unit target, ActionId actionId);

	void endCast(Unit caster, Spell spell, Unit target, ActionId actionId);

	void canNotBeCasted(Unit caster, Spell spell, Unit target, ActionId actionId);

	void increasedResource(ResourceType type, Spell spell, Unit target, int amount, int current, int previous);

	void decreasedResource(ResourceType type, Spell spell, Unit target, int amount, int current, int previous);

	void simulationEnded();
}
