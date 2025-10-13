package wow.simulator.util;

import lombok.Getter;
import lombok.Setter;
import wow.commons.model.effect.AbilitySource;
import wow.commons.model.item.ItemSetSource;
import wow.commons.model.item.ItemSource;
import wow.commons.model.spell.ResourceType;
import wow.commons.model.spell.Spell;
import wow.commons.model.talent.TalentSource;
import wow.simulator.log.handler.GameLogHandler;
import wow.simulator.model.cooldown.CooldownInstance;
import wow.simulator.model.effect.EffectInstance;
import wow.simulator.model.time.Clock;
import wow.simulator.model.time.Time;
import wow.simulator.model.unit.Unit;
import wow.simulator.model.unit.action.CastSpellAction;
import wow.simulator.model.unit.action.ChannelSpellAction;
import wow.simulator.model.unit.action.UnitAction;
import wow.simulator.simulation.TimeAware;

import java.util.ArrayList;
import java.util.List;

import static wow.simulator.WowSimulatorSpringTest.DummyTestSource;
import static wow.simulator.util.TestEvent.*;

/**
 * User: POlszewski
 * Date: 2025-01-30
 */
@Getter
@Setter
public class TestEventCollectingHandler implements GameLogHandler, TimeAware {
	List<TestEvent> events = new ArrayList<>();
	Clock clock;
	boolean ignoreRegen = true;

	@Override
	public void beginGcd(UnitAction sourceAction) {
		addEvent(new BeginGcd(now(), sourceAction.getOwner()));
	}

	@Override
	public void endGcd(UnitAction sourceAction) {
		addEvent(new EndGcd(now(), sourceAction.getOwner()));
	}

	@Override
	public void beginCast(CastSpellAction action) {
		addEvent(new BeginCast(now(), action.getOwner(), action.getAbilityName(), action.getCastTime()));
	}

	@Override
	public void endCast(CastSpellAction action) {
		addEvent(new EndCast(now(), action.getOwner(), action.getAbilityName()));
	}

	@Override
	public void beginChannel(ChannelSpellAction action) {
		addEvent(new BeginChannel(now(), action.getOwner(), action.getAbilityName(), action.getChannelTime()));
	}

	@Override
	public void endChannel(ChannelSpellAction action) {
		addEvent(new EndChannel(now(), action.getOwner(), action.getAbilityName()));
	}

	@Override
	public void canNotBeCasted(CastSpellAction action) {
		addEvent(new CanNotBeCasted(now(), action.getOwner(), action.getAbilityName()));
	}

	@Override
	public void castInterrupted(CastSpellAction action) {
		addEvent(new CastInterrupted(now(), action.getOwner(), action.getAbilityName()));
	}

	@Override
	public void channelInterrupted(ChannelSpellAction action) {
		addEvent(new ChannelInterrupted(now(), action.getOwner(), action.getAbilityName()));
	}

	@Override
	public void spellResisted(Unit caster, Unit target, Spell spell) {
		addEvent(new SpellResisted(now(), caster, spell.getName(), target));
	}

	@Override
	public void increasedResource(ResourceType type, Spell spell, Unit target, int amount, int current, int previous, boolean crit) {
		if (spell == null && ignoreRegen) {
			return;
		}
		addEvent(new IncreasedResource(now(), amount, type, crit, target, getAbilityId(spell)));
	}

	@Override
	public void decreasedResource(ResourceType type, Spell spell, Unit target, int amount, int current, int previous, boolean crit) {
		addEvent(new DecreasedResource(now(), amount, type, crit, target, getAbilityId(spell)));
	}

	private static String getAbilityId(Spell spell) {
		return spell != null ? spell.getName() : null;
	}

	@Override
	public void effectApplied(EffectInstance effect) {
		var time = now();
		var sourceName = effect.getSource().getName();
		var type = getEffectType(effect);
		var target = effect.getTarget();
		var duration = effect.getDuration();

		addEvent(new EffectApplied(time, sourceName, type, target, duration));
	}

	@Override
	public void effectStacked(EffectInstance effect) {
		var time = now();
		var sourceName = effect.getSource().getName();
		var type = getEffectType(effect);
		var target = effect.getTarget();
		var numStacks = effect.getNumStacks();

		addEvent(new EffectStacked(time, sourceName, type, target, numStacks));
	}

	@Override
	public void effectStacksIncreased(EffectInstance effect) {
		var time = now();
		var sourceName = effect.getSource().getName();
		var type = getEffectType(effect);
		var target = effect.getTarget();
		var numStacks = effect.getNumStacks();

		addEvent(new EffectStacksIncreased(time, sourceName, type, target, numStacks));
	}

	@Override
	public void effectStacksDecreased(EffectInstance effect) {
		var time = now();
		var sourceName = effect.getSource().getName();
		var type = getEffectType(effect);
		var target = effect.getTarget();
		var numStacks = effect.getNumStacks();

		addEvent(new EffectStacksDecreased(time, sourceName, type, target, numStacks));
	}

	@Override
	public void effectChargesIncreased(EffectInstance effect) {
		var time = now();
		var sourceName = effect.getSource().getName();
		var type = getEffectType(effect);
		var target = effect.getTarget();
		var numCharges = effect.getNumCharges();

		addEvent(new EffectChargesIncreased(time, sourceName, type, target, numCharges));
	}

	@Override
	public void effectChargesDecreased(EffectInstance effect) {
		var time = now();
		var sourceName = effect.getSource().getName();
		var type = getEffectType(effect);
		var target = effect.getTarget();
		var numCharges = effect.getNumCharges();

		addEvent(new EffectChargesDecreased(time, sourceName, type, target, numCharges));
	}

	@Override
	public void effectExpired(EffectInstance effect) {
		var time = now();
		var sourceName = effect.getSource().getName();
		var type = getEffectType(effect);
		var target = effect.getTarget();

		addEvent(new EffectExpired(time, sourceName, type, target));
	}

	@Override
	public void effectRemoved(EffectInstance effect) {
		var time = now();
		var sourceName = effect.getSource().getName();
		var type = getEffectType(effect);
		var target = effect.getTarget();

		addEvent(new EffectRemoved(time, sourceName, type, target));
	}

	private EffectType getEffectType(EffectInstance effect) {
		return switch (effect.getSource()) {
			case AbilitySource ignored -> EffectType.ABILITY;
			case TalentSource ignored -> EffectType.TALENT;
			case ItemSource ignored -> EffectType.ITEM;
			case ItemSetSource ignored -> EffectType.ITEM_SET;
			case DummyTestSource ignored -> null;
			default -> throw new IllegalArgumentException();
		};
	}

	@Override
	public void cooldownStarted(CooldownInstance cooldown) {
		addEvent(new CooldownStarted(now(), cooldown.getOwner(), cooldown.getCooldownId(), cooldown.getDuration()));
	}

	@Override
	public void cooldownExpired(CooldownInstance cooldown) {
		addEvent(new CooldownExpired(now(), cooldown.getOwner(), cooldown.getCooldownId()));
	}

	private void addEvent(TestEvent event) {
		events.add(event);
	}

	private Time now() {
		return clock.now();
	}
}
