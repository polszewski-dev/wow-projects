package wow.simulator.log.handler;

import wow.commons.model.spell.ResourceType;
import wow.commons.model.spell.Spell;
import wow.simulator.model.cooldown.CooldownInstance;
import wow.simulator.model.effect.EffectInstance;
import wow.simulator.model.unit.Unit;
import wow.simulator.model.unit.action.CastSpellAction;
import wow.simulator.model.unit.action.ChannelSpellAction;
import wow.simulator.model.unit.action.UnitAction;

/**
 * User: POlszewski
 * Date: 2025-02-13
 */
public class DefaultGameLogHandler implements GameLogHandler {
	@Override
	public void beginGcd(UnitAction sourceAction) {
		// void
	}

	@Override
	public void endGcd(UnitAction sourceAction) {
		// void
	}

	@Override
	public void beginCast(CastSpellAction action) {
		// void
	}

	@Override
	public void endCast(CastSpellAction action) {
		// void
	}

	@Override
	public void beginChannel(ChannelSpellAction action) {
		// void
	}

	@Override
	public void endChannel(ChannelSpellAction action) {
		// void
	}

	@Override
	public void canNotBeCasted(CastSpellAction action) {
		// void
	}

	@Override
	public void spellHit(CastSpellAction action, Unit target) {
		// void
	}

	@Override
	public void spellResisted(CastSpellAction action, Unit target) {
		// void
	}

	@Override
	public void increasedResource(ResourceType type, Spell spell, Unit target, int amount, int current, int previous, boolean crit) {
		// void
	}

	@Override
	public void decreasedResource(ResourceType type, Spell spell, Unit target, int amount, int current, int previous, boolean crit) {
		// void
	}

	@Override
	public void effectApplied(EffectInstance effect) {
		// void
	}

	@Override
	public void effectStacked(EffectInstance effect) {
		// void
	}

	@Override
	public void effectStacksIncreased(EffectInstance effect) {
		// void
	}

	@Override
	public void effectStacksDecreased(EffectInstance effect) {
		// void
	}

	@Override
	public void effectChargesIncreased(EffectInstance effect) {
		// void
	}

	@Override
	public void effectChargesDecreased(EffectInstance effect) {
		// void
	}

	@Override
	public void effectExpired(EffectInstance effect) {
		// void
	}

	@Override
	public void effectRemoved(EffectInstance effect) {
		// void
	}

	@Override
	public void cooldownStarted(CooldownInstance cooldown) {
		// void
	}

	@Override
	public void cooldownExpired(CooldownInstance cooldown) {
		// void
	}

	@Override
	public void simulationStarted() {
		// void
	}

	@Override
	public void simulationEnded() {
		// void
	}
}
