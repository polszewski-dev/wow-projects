package wow.simulator.log.handler;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import wow.commons.model.spells.ResourceType;
import wow.commons.model.spells.Spell;
import wow.simulator.model.action.ActionId;
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
	public void beginGcd(Unit caster, Spell spell, Unit target, ActionId actionId) {
		print("%s begin GCD %s", caster, spell);
	}

	@Override
	public void endGcd(Unit caster, Spell spell, Unit target, ActionId actionId) {
		print("%s end GCD %s", caster, spell);
	}

	@Override
	public void beginCast(Unit caster, Spell spell, Unit target, ActionId actionId) {
		print("%s begin cast %s", caster, spell);
	}

	@Override
	public void endCast(Unit caster, Spell spell, Unit target, ActionId actionId) {
		print("%s end cast %s", caster, spell);
	}

	@Override
	public void canNotBeCasted(Unit caster, Spell spell, Unit target, ActionId actionId) {
		print("%s can't cast %s on %s", caster, spell, target);
	}

	@Override
	public void spellMissed(Unit caster, Spell spell, Unit target) {
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
	public void simulationEnded() {
		print("Simulation ended");
	}

	private void print(String str, Object... args) {
		log.info("%7s> %s".formatted(clock.now(), str.formatted(args)));
	}
}