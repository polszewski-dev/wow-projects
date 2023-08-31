package wow.simulator.log.handler;

import wow.commons.model.spell.Ability;
import wow.commons.model.spell.ResourceType;
import wow.simulator.model.cooldown.Cooldown;
import wow.simulator.model.effect.UnitEffect;
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

	void beginChannel(CastSpellAction action);

	void endChannel(CastSpellAction action);

	void canNotBeCasted(CastSpellAction action);

	default void castInterrupted(CastSpellAction action) {
		endCast(action);
	}

	default void channelInterrupted(CastSpellAction action) {
		endChannel(action);
	}

	void spellResisted(CastSpellAction action, Unit target);

	void increasedResource(ResourceType type, Ability ability, Unit target, int amount, int current, int previous, boolean crit);

	void decreasedResource(ResourceType type, Ability ability, Unit target, int amount, int current, int previous, boolean crit);

	void effectApplied(UnitEffect effect);

	void effectStacked(UnitEffect effect);

	void effectExpired(UnitEffect effect);

	void effectRemoved(UnitEffect effect);

	void cooldownStarted(Cooldown cooldown);

	void cooldownExpired(Cooldown cooldown);

	void simulationStarted();

	void simulationEnded();
}
