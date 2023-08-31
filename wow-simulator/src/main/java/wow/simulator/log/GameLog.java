package wow.simulator.log;

import wow.commons.model.spell.Ability;
import wow.commons.model.spell.ResourceType;
import wow.simulator.log.handler.GameLogHandler;
import wow.simulator.model.cooldown.Cooldown;
import wow.simulator.model.effect.UnitEffect;
import wow.simulator.model.unit.Unit;
import wow.simulator.model.unit.action.CastSpellAction;
import wow.simulator.model.unit.action.UnitAction;

import java.util.ArrayList;
import java.util.List;

/**
 * User: POlszewski
 * Date: 2023-08-07
 */
public class GameLog implements GameLogHandler {
	private final List<GameLogHandler> handlers = new ArrayList<>();

	public void addHandler(GameLogHandler handler) {
		handlers.add(handler);
	}

	@Override
	public void beginGcd(UnitAction sourceAction) {
		handlers.forEach(handler -> handler.beginGcd(sourceAction));
	}

	@Override
	public void endGcd(UnitAction sourceAction) {
		handlers.forEach(handler -> handler.endGcd(sourceAction));
	}

	@Override
	public void beginCast(CastSpellAction action) {
		handlers.forEach(handler -> handler.beginCast(action));
	}

	@Override
	public void endCast(CastSpellAction action) {
		handlers.forEach(handler -> handler.endCast(action));
	}

	@Override
	public void beginChannel(CastSpellAction action) {
		handlers.forEach(handler -> handler.beginChannel(action));
	}

	@Override
	public void endChannel(CastSpellAction action) {
		handlers.forEach(handler -> handler.endChannel(action));
	}

	@Override
	public void canNotBeCasted(CastSpellAction action) {
		handlers.forEach(handler -> handler.canNotBeCasted(action));
	}

	@Override
	public void castInterrupted(CastSpellAction action) {
		handlers.forEach(handler -> handler.castInterrupted(action));
	}

	@Override
	public void channelInterrupted(CastSpellAction action) {
		handlers.forEach(handler -> handler.channelInterrupted(action));
	}

	@Override
	public void spellResisted(CastSpellAction action, Unit target) {
		handlers.forEach(handler -> handler.spellResisted(action, target));
	}

	@Override
	public void increasedResource(ResourceType type, Ability ability, Unit target, int amount, int current, int previous, boolean crit) {
		handlers.forEach(handler -> handler.increasedResource(type, ability, target, amount, current, previous, crit));
	}

	@Override
	public void decreasedResource(ResourceType type, Ability ability, Unit target, int amount, int current, int previous, boolean crit) {
		handlers.forEach(handler -> handler.decreasedResource(type, ability, target, amount, current, previous, crit));
	}

	@Override
	public void effectApplied(UnitEffect effect) {
		handlers.forEach(handler -> handler.effectApplied(effect));
	}

	@Override
	public void effectStacked(UnitEffect effect) {
		handlers.forEach(handler -> handler.effectStacked(effect));
	}

	@Override
	public void effectExpired(UnitEffect effect) {
		handlers.forEach(handler -> handler.effectExpired(effect));
	}

	@Override
	public void effectRemoved(UnitEffect effect) {
		handlers.forEach(handler -> handler.effectRemoved(effect));
	}

	@Override
	public void cooldownStarted(Cooldown cooldown) {
		handlers.forEach(handler -> handler.cooldownStarted(cooldown));
	}

	@Override
	public void cooldownExpired(Cooldown cooldown) {
		handlers.forEach(handler -> handler.cooldownExpired(cooldown));
	}

	@Override
	public void simulationStarted() {
		handlers.forEach(GameLogHandler::simulationStarted);
	}

	@Override
	public void simulationEnded() {
		handlers.forEach(GameLogHandler::simulationEnded);
	}
}
