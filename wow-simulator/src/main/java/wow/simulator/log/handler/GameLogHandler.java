package wow.simulator.log.handler;

import wow.commons.model.spells.ResourceType;
import wow.commons.model.spells.Spell;
import wow.simulator.model.cooldown.Cooldown;
import wow.simulator.model.effect.Effect;
import wow.simulator.model.unit.Unit;
import wow.simulator.model.unit.action.CastSpellAction;
import wow.simulator.model.unit.action.UnitAction;

/**
 * User: POlszewski
 * Date: 2023-08-07
 */
public interface GameLogHandler {
	void beginGcd(UnitAction sourceAction);

	void endGcd(UnitAction sourceAction);

	void beginCast(CastSpellAction action);

	void endCast(CastSpellAction action);

	void canNotBeCasted(CastSpellAction action);

	void castInterrupted(CastSpellAction action);

	void spellResisted(CastSpellAction action);

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
