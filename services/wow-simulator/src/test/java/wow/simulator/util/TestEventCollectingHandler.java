package wow.simulator.util;

import lombok.Getter;
import lombok.Setter;
import wow.commons.model.effect.AbilitySource;
import wow.commons.model.item.ItemSetSource;
import wow.commons.model.item.ItemSource;
import wow.commons.model.spell.Ability;
import wow.commons.model.spell.ResourceType;
import wow.commons.model.spell.Spell;
import wow.commons.model.talent.TalentSource;
import wow.simulator.log.handler.DefaultGameLogHandler;
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

import static wow.simulator.util.TestEvent.*;

/**
 * User: POlszewski
 * Date: 2025-01-30
 */
@Getter
@Setter
public class TestEventCollectingHandler extends DefaultGameLogHandler implements TimeAware {
	List<TestEvent> events = new ArrayList<>();
	Clock clock;

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
		addEvent(new BeginCast(now(), action.getOwner(), action.getAbilityId(), action.getCastTime()));
	}

	@Override
	public void endCast(CastSpellAction action) {
		addEvent(new EndCast(now(), action.getOwner(), action.getAbilityId()));
	}

	@Override
	public void beginChannel(ChannelSpellAction action) {
		addEvent(new BeginChannel(now(), action.getOwner(), action.getAbilityId(), action.getChannelTime()));
	}

	@Override
	public void endChannel(ChannelSpellAction action) {
		addEvent(new EndChannel(now(), action.getOwner(), action.getAbilityId()));
	}

	@Override
	public void canNotBeCasted(CastSpellAction action) {
		addEvent(new CanNotBeCasted(now(), action.getOwner(), action.getAbilityId()));
	}

	@Override
	public void castInterrupted(CastSpellAction action) {
		addEvent(new CastInterrupted(now(), action.getOwner(), action.getAbilityId()));
	}

	@Override
	public void channelInterrupted(ChannelSpellAction action) {
		addEvent(new ChannelInterrupted(now(), action.getOwner(), action.getAbilityId()));
	}

	@Override
	public void spellResisted(Unit caster, Unit target, Spell spell) {
		addEvent(new SpellResisted(now(), caster, ((Ability) spell).getAbilityId(), target));
	}

	@Override
	public void increasedResource(ResourceType type, Spell spell, Unit target, int amount, int current, int previous, boolean crit) {
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
		switch (effect.getSource()) {
			case AbilitySource s ->
					addEvent(new EffectApplied(now(), s.getAbilityId(), effect.getTarget(), effect.getDuration()));
			case TalentSource s ->
					addEvent(new TalentEffectApplied(now(), s.getTalentId(), effect.getTarget(), effect.getDuration()));
			case ItemSource s ->
					addEvent(new ItemEffectApplied(now(), s.getName(), effect.getTarget(), effect.getDuration()));
			case ItemSetSource s ->
					addEvent(new ItemEffectApplied(now(), s.getName(), effect.getTarget(), effect.getDuration()));
			default -> throw new IllegalArgumentException();
		}
	}

	@Override
	public void effectStacked(EffectInstance effect) {
		switch (effect.getSource()) {
			case AbilitySource s ->
					addEvent(new EffectStacked(now(), s.getAbilityId(), effect.getTarget(), effect.getNumStacks()));
			case TalentSource s ->
					addEvent(new TalentEffectStacked(now(), s.getTalentId(), effect.getTarget(), effect.getNumStacks()));
			case ItemSource s ->
					addEvent(new ItemEffectStacked(now(), s.getName(), effect.getTarget(), effect.getNumStacks()));
			default -> throw new IllegalArgumentException();
		}
	}

	@Override
	public void effectStacksIncreased(EffectInstance effect) {
		switch (effect.getSource()) {
			case AbilitySource s ->
					addEvent(new EffectStacksIncreased(now(), s.getAbilityId(), effect.getTarget(), effect.getNumStacks()));
			case TalentSource s ->
					addEvent(new TalentEffectStacksIncreased(now(), s.getTalentId(), effect.getTarget(), effect.getNumStacks()));
			case ItemSource s ->
					addEvent(new ItemEffectStacksIncreased(now(), s.getName(), effect.getTarget(), effect.getNumStacks()));
			default -> throw new IllegalArgumentException();
		}
	}

	@Override
	public void effectStacksDecreased(EffectInstance effect) {
		switch (effect.getSource()) {
			case AbilitySource s ->
					addEvent(new EffectStacksDecreased(now(), s.getAbilityId(), effect.getTarget(), effect.getNumStacks()));
			case TalentSource s ->
					addEvent(new TalentEffectStacksDecreased(now(), s.getTalentId(), effect.getTarget(), effect.getNumStacks()));
			case ItemSource s ->
					addEvent(new ItemEffectStacksDecreased(now(), s.getName(), effect.getTarget(), effect.getNumStacks()));
			default -> throw new IllegalArgumentException();
		}
	}

	@Override
	public void effectChargesIncreased(EffectInstance effect) {
		switch (effect.getSource()) {
			case AbilitySource s ->
					addEvent(new EffectChargesIncreased(now(), s.getAbilityId(), effect.getTarget(), effect.getNumCharges()));
			case TalentSource s ->
					addEvent(new TalentEffectChargesIncreased(now(), s.getTalentId(), effect.getTarget(), effect.getNumCharges()));
			case ItemSource s ->
					addEvent(new ItemEffectChargesIncreased(now(), s.getName(), effect.getTarget(), effect.getNumStacks()));
			default -> throw new IllegalArgumentException();
		}
	}

	@Override
	public void effectChargesDecreased(EffectInstance effect) {
		switch (effect.getSource()) {
			case AbilitySource s ->
					addEvent(new EffectChargesDecreased(now(), s.getAbilityId(), effect.getTarget(), effect.getNumCharges()));
			case TalentSource s ->
					addEvent(new TalentEffectChargesDecreased(now(), s.getTalentId(), effect.getTarget(), effect.getNumCharges()));
			case ItemSource s ->
					addEvent(new ItemEffectChargesDecreased(now(), s.getName(), effect.getTarget(), effect.getNumStacks()));
			default -> throw new IllegalArgumentException();
		}
	}

	@Override
	public void effectExpired(EffectInstance effect) {
		switch (effect.getSource()) {
			case AbilitySource as -> addEvent(new EffectExpired(now(), as.getAbilityId(), effect.getTarget()));
			case TalentSource ts -> addEvent(new TalentEffectExpired(now(), ts.getTalentId(), effect.getTarget()));
			case ItemSource s -> addEvent(new ItemEffectExpired(now(), s.getName(), effect.getTarget()));
			case ItemSetSource s -> addEvent(new ItemEffectExpired(now(), s.getName(), effect.getTarget()));
			default -> throw new IllegalArgumentException();
		}
	}

	@Override
	public void effectRemoved(EffectInstance effect) {
		switch (effect.getSource()) {
			case AbilitySource s -> addEvent(new EffectRemoved(now(), s.getAbilityId(), effect.getTarget()));
			case TalentSource s -> addEvent(new TalentEffectRemoved(now(), s.getTalentId(), effect.getTarget()));
			case ItemSource s -> addEvent(new ItemEffectRemoved(now(), s.getName(), effect.getTarget()));
			case ItemSetSource s -> addEvent(new ItemEffectRemoved(now(), s.getName(), effect.getTarget()));
			default -> throw new IllegalArgumentException();
		}
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
