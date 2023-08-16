package wow.simulator.log;

import wow.commons.model.spells.ResourceType;
import wow.commons.model.spells.Spell;
import wow.simulator.log.handler.GameLogHandler;
import wow.simulator.model.action.ActionId;
import wow.simulator.model.unit.Unit;

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
	public void beginGcd(Unit caster, Spell spell, Unit target, ActionId actionId) {
		handlers.forEach(handler -> handler.beginGcd(caster, spell, target, actionId));
	}

	@Override
	public void endGcd(Unit caster, Spell spell, Unit target, ActionId actionId) {
		handlers.forEach(handler -> handler.endGcd(caster, spell, target, actionId));
	}

	@Override
	public void beginCast(Unit caster, Spell spell, Unit target, ActionId actionId) {
		handlers.forEach(handler -> handler.beginCast(caster, spell, target, actionId));
	}

	@Override
	public void endCast(Unit caster, Spell spell, Unit target, ActionId actionId) {
		handlers.forEach(handler -> handler.endCast(caster, spell, target, actionId));
	}

	@Override
	public void canNotBeCasted(Unit caster, Spell spell, Unit target, ActionId actionId) {
		handlers.forEach(handler -> handler.canNotBeCasted(caster, spell, target, actionId));
	}

	@Override
	public void spellMissed(Unit caster, Spell spell, Unit target) {
		handlers.forEach(handler -> handler.spellMissed(caster, spell, target));
	}

	@Override
	public void increasedResource(ResourceType type, Spell spell, Unit target, int amount, int current, int previous, boolean crit) {
		handlers.forEach(handler -> handler.increasedResource(type, spell, target, amount, current, previous, crit));
	}

	@Override
	public void decreasedResource(ResourceType type, Spell spell, Unit target, int amount, int current, int previous, boolean crit) {
		handlers.forEach(handler -> handler.decreasedResource(type, spell, target, amount, current, previous, crit));
	}

	@Override
	public void simulationEnded() {
		handlers.forEach(GameLogHandler::simulationEnded);
	}
}
