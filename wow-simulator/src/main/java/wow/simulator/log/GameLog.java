package wow.simulator.log;

import wow.commons.model.spells.ResourceType;
import wow.commons.model.spells.Spell;
import wow.simulator.log.handler.GameLogHandler;
import wow.simulator.model.action.Action;
import wow.simulator.model.effect.Effect;
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
	public void beginGcd(Unit caster, Action action) {
		handlers.forEach(handler -> handler.beginGcd(caster, action));
	}

	@Override
	public void endGcd(Unit caster, Action action) {
		handlers.forEach(handler -> handler.endGcd(caster, action));
	}

	@Override
	public void beginCast(Unit caster, Spell spell, Unit target, Action action) {
		handlers.forEach(handler -> handler.beginCast(caster, spell, target, action));
	}

	@Override
	public void endCast(Unit caster, Spell spell, Unit target, Action action) {
		handlers.forEach(handler -> handler.endCast(caster, spell, target, action));
	}

	@Override
	public void canNotBeCasted(Unit caster, Spell spell, Unit target, Action action) {
		handlers.forEach(handler -> handler.canNotBeCasted(caster, spell, target, action));
	}

	@Override
	public void castInterrupted(Unit caster, Spell spell, Unit target, Action action) {
		handlers.forEach(handler -> handler.castInterrupted(caster, spell, target, action));
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
	public void effectApplied(Effect effect) {
		handlers.forEach(handler -> handler.effectApplied(effect));
	}

	@Override
	public void effectStacked(Effect effect) {
		handlers.forEach(handler -> handler.effectStacked(effect));
	}

	@Override
	public void effectExpired(Effect effect) {
		handlers.forEach(handler -> handler.effectExpired(effect));
	}

	@Override
	public void effectRemoved(Effect effect) {
		handlers.forEach(handler -> handler.effectRemoved(effect));
	}

	@Override
	public void simulationEnded() {
		handlers.forEach(GameLogHandler::simulationEnded);
	}
}
