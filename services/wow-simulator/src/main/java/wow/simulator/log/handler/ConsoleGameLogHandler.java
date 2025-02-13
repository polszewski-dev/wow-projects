package wow.simulator.log.handler;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import wow.commons.model.spell.*;
import wow.simulator.model.cooldown.CooldownInstance;
import wow.simulator.model.effect.EffectInstance;
import wow.simulator.model.time.Clock;
import wow.simulator.model.unit.Unit;
import wow.simulator.model.unit.action.CastSpellAction;
import wow.simulator.model.unit.action.ChannelSpellAction;
import wow.simulator.simulation.TimeAware;

/**
 * User: POlszewski
 * Date: 2023-08-07
 */
@Setter
@Slf4j
public class ConsoleGameLogHandler extends DefaultGameLogHandler implements TimeAware {
	private Clock clock;

	@Override
	public void beginCast(CastSpellAction action) {
		print("%s begin cast %s", action.getOwner(), action.getAbility());
	}

	@Override
	public void endCast(CastSpellAction action) {
		print("%s end cast %s", action.getOwner(), action.getAbility());
	}

	@Override
	public void beginChannel(ChannelSpellAction action) {
		print("%s begin channel %s", action.getOwner(), action.getAbility());
	}

	@Override
	public void endChannel(ChannelSpellAction action) {
		print("%s end channel %s", action.getOwner(), action.getAbility());
	}

	@Override
	public void canNotBeCasted(CastSpellAction action) {
		print("%s can't cast %s", action.getOwner(), action.getAbility());
	}

	@Override
	public void castInterrupted(CastSpellAction action) {
		print("%s's s% cast interrupted", action.getOwner(), action.getAbility());
	}

	@Override
	public void channelInterrupted(ChannelSpellAction action) {
		print("%s's s% channel interrupted", action.getOwner(), action.getAbility());
	}

	@Override
	public void spellResisted(CastSpellAction action, Unit target) {
		print("%s's %s resisted by %s", action.getOwner(), action.getAbility(), target);
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
	public void effectApplied(EffectInstance effect) {
		print("Effect of %s applied", effect);
	}

	@Override
	public void effectStacked(EffectInstance effect) {
		print("Effect of %s stacked", effect);
	}

	@Override
	public void effectStacksIncreased(EffectInstance effect) {
		print("Effect of %s stacks increased", effect);
	}

	@Override
	public void effectStacksDecreased(EffectInstance effect) {
		print("Effect of %s stacks decreased", effect);
	}

	@Override
	public void effectChargesIncreased(EffectInstance effect) {
		print("Effect of %s charges increased", effect);
	}

	@Override
	public void effectChargesDecreased(EffectInstance effect) {
		print("Effect of %s charges decreased", effect);
	}

	@Override
	public void effectExpired(EffectInstance effect) {
		print("Effect of %s expired", effect);
	}

	@Override
	public void effectRemoved(EffectInstance effect) {
		print("Effect of %s removed", effect);
	}

	@Override
	public void cooldownStarted(CooldownInstance cooldown) {
		print("%s's %s cooldown started", cooldown.getOwner(), toString(cooldown));
	}

	@Override
	public void cooldownExpired(CooldownInstance cooldown) {
		print("%s's %s cooldown expired", cooldown.getOwner(), toString(cooldown));
	}

	private String toString(CooldownInstance cooldown) {
		return switch (cooldown.getCooldownId()) {
			case AbilityCooldownId c -> c.abilityId().toString();
			case GroupCooldownId c -> c.group().toString();
			case SpellCooldownId c -> c.spellId() + "";
			case GcdCooldownId ignored -> "GCD";
		};
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
