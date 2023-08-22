package wow.simulator.log.handler;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import wow.commons.model.spells.ResourceType;
import wow.commons.model.spells.Spell;
import wow.simulator.model.cooldown.Cooldown;
import wow.simulator.model.effect.Effect;
import wow.simulator.model.time.Clock;
import wow.simulator.model.unit.Unit;
import wow.simulator.model.unit.action.CastSpellAction;
import wow.simulator.model.unit.action.UnitAction;
import wow.simulator.simulation.TimeAware;

/**
 * User: POlszewski
 * Date: 2023-08-07
 */
@Setter
@Slf4j
public class ConsoleGameLogHandler implements GameLogHandler, TimeAware {
	private Clock clock;

	@Override
	public void beginGcd(UnitAction sourceAction) {
		// ignored
	}

	@Override
	public void endGcd(UnitAction sourceAction) {
		// ignored
	}

	@Override
	public void beginCast(CastSpellAction action) {
		print("%s begin cast %s", action.getOwner(), action.getSpell());
	}

	@Override
	public void endCast(CastSpellAction action) {
		print("%s end cast %s", action.getOwner(), action.getSpell());
	}

	@Override
	public void canNotBeCasted(CastSpellAction action) {
		print("%s can't cast %s on %s", action.getOwner(), action.getSpell(), action.getTarget());
	}

	@Override
	public void castInterrupted(CastSpellAction action) {
		print("%s's s% cast on %s interrupted", action.getOwner(), action.getSpell(), action.getTarget());
	}

	@Override
	public void spellResisted(CastSpellAction action) {
		print("%s's %s resisted %s", action.getOwner(), action.getSpell(), action.getTarget());
	}

	@Override
	public void increasedResource(ResourceType type, Spell spell, Unit target, int amount, int current, int previous, boolean crit) {
		print("%s increased %s %s by %s%s", spell, target, type.toString().toLowerCase(), amount, crit ? " (crit)" : "");
	}

	@Override
	public void decreasedResource(ResourceType type, Spell spell, Unit target, int amount, int current, int previous, boolean crit) {
		print("%s decreased %s %s by %s%s", spell, target, type.toString().toLowerCase(), amount, crit ? " (crit)" : "");
	}

	@Override
	public void effectApplied(Effect effect) {
		print("Effect of %s applied", effect);
	}

	@Override
	public void effectStacked(Effect effect) {
		print("Effect of %s stacked", effect);
	}

	@Override
	public void effectExpired(Effect effect) {
		print("Effect of %s expired", effect);
	}

	@Override
	public void effectRemoved(Effect effect) {
		print("Effect of %s removed", effect);
	}

	@Override
	public void cooldownStarted(Cooldown cooldown) {
		print("%s's %s cooldown started", cooldown.getOwner(), cooldown.getSpellId());
	}

	@Override
	public void cooldownExpired(Cooldown cooldown) {
		print("%s's %s cooldown expired", cooldown.getOwner(), cooldown.getSpellId());
	}

	@Override
	public void simulationStarted() {
		print("Simulation started");
	}

	@Override
	public void simulationEnded() {
		print("Simulation ended");
	}

	private void print(String str, Object... args) {
		log.info("%7s> %s".formatted(clock.now(), str.formatted(args)));
	}
}
