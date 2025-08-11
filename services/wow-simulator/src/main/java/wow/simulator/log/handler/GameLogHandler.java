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
 * Date: 2023-08-07
 */
public interface GameLogHandler {
	default void beginGcd(UnitAction sourceAction) {}

	default void endGcd(UnitAction sourceAction) {}

	default void beginCast(CastSpellAction action) {}

	default void endCast(CastSpellAction action) {}

	default void beginChannel(ChannelSpellAction action) {}

	default void endChannel(ChannelSpellAction action) {}

	default void canNotBeCasted(CastSpellAction action) {}

	default void castInterrupted(CastSpellAction action) {
		endCast(action);
	}

	default void channelInterrupted(ChannelSpellAction action) {
		endChannel(action);
	}

	default void spellHit(Unit caster, Unit target, Spell spell) {}

	default void spellResisted(Unit caster, Unit target, Spell spell) {}

	default void increasedResource(ResourceType type, Spell spell, Unit target, int amount, int current, int previous, boolean crit) {}

	default void decreasedResource(ResourceType type, Spell spell, Unit target, int amount, int current, int previous, boolean crit) {}

	default void effectApplied(EffectInstance effect) {}

	default void effectStacked(EffectInstance effect) {}

	default void effectStacksIncreased(EffectInstance effect) {}

	default void effectStacksDecreased(EffectInstance effect) {}

	default void effectChargesIncreased(EffectInstance effect) {}

	default void effectChargesDecreased(EffectInstance effect) {}

	default void effectExpired(EffectInstance effect) {}

	default void effectRemoved(EffectInstance effect) {}

	default void cooldownStarted(CooldownInstance cooldown) {}

	default void cooldownExpired(CooldownInstance cooldown) {}

	default void simulationStarted() {}

	default void simulationEnded() {}
}
