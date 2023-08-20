package wow.simulator.log.handler;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import wow.commons.model.spells.ResourceType;
import wow.commons.model.spells.Spell;
import wow.simulator.model.action.Action;
import wow.simulator.model.cooldown.Cooldown;
import wow.simulator.model.effect.Effect;
import wow.simulator.model.time.Clock;
import wow.simulator.model.unit.Unit;
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
	public void beginGcd(Unit caster, Action sourceAction) {
		// ignored
	}

	@Override
	public void endGcd(Unit caster, Action sourceAction) {
		// ignored
	}

	@Override
	public void beginCast(Unit caster, Spell spell, Unit target, Action action) {
		print("%s begin cast %s", caster, spell);
	}

	@Override
	public void endCast(Unit caster, Spell spell, Unit target, Action action) {
		print("%s end cast %s", caster, spell);
	}

	@Override
	public void canNotBeCasted(Unit caster, Spell spell, Unit target, Action action) {
		print("%s can't cast %s on %s", caster, spell, target);
	}

	@Override
	public void castInterrupted(Unit caster, Spell spell, Unit target, Action action) {
		print("%s's s% cast on %s interrupted", caster, spell, target);
	}

	@Override
	public void spellMissed(Unit caster, Spell spell, Unit target, Action action) {
		print("%s's %s missed %s", caster, spell, target);
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
