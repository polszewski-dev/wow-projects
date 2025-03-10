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
	void beginGcd(UnitAction sourceAction);

	void endGcd(UnitAction sourceAction);

	void beginCast(CastSpellAction action);

	void endCast(CastSpellAction action);

	void beginChannel(ChannelSpellAction action);

	void endChannel(ChannelSpellAction action);

	void canNotBeCasted(CastSpellAction action);

	default void castInterrupted(CastSpellAction action) {
		endCast(action);
	}

	default void channelInterrupted(ChannelSpellAction action) {
		endChannel(action);
	}

	void spellHit(Unit caster, Unit target, Spell spell);

	void spellResisted(Unit caster, Unit target, Spell spell);

	void increasedResource(ResourceType type, Spell spell, Unit target, int amount, int current, int previous, boolean crit);

	void decreasedResource(ResourceType type, Spell spell, Unit target, int amount, int current, int previous, boolean crit);

	void effectApplied(EffectInstance effect);

	void effectStacked(EffectInstance effect);

	void effectStacksIncreased(EffectInstance effect);

	void effectStacksDecreased(EffectInstance effect);

	void effectChargesIncreased(EffectInstance effect);

	void effectChargesDecreased(EffectInstance effect);

	void effectExpired(EffectInstance effect);

	void effectRemoved(EffectInstance effect);

	void cooldownStarted(CooldownInstance cooldown);

	void cooldownExpired(CooldownInstance cooldown);

	void simulationStarted();

	void simulationEnded();
}
