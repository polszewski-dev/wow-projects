package wow.simulator.util;

import lombok.Getter;
import lombok.Setter;
import wow.commons.model.AnyDuration;
import wow.commons.model.effect.AbilitySource;
import wow.commons.model.item.ItemSetSource;
import wow.commons.model.item.ItemSource;
import wow.commons.model.spell.AbilityId;
import wow.commons.model.spell.CooldownId;
import wow.commons.model.spell.ResourceType;
import wow.commons.model.spell.Spell;
import wow.commons.model.talent.TalentSource;
import wow.simulator.log.handler.GameLogHandler;
import wow.simulator.model.cooldown.CooldownInstance;
import wow.simulator.model.effect.EffectInstance;
import wow.simulator.model.time.Clock;
import wow.simulator.model.time.Time;
import wow.simulator.model.unit.Player;
import wow.simulator.model.unit.Unit;
import wow.simulator.model.unit.action.CastSpellAction;
import wow.simulator.model.unit.action.ChannelSpellAction;
import wow.simulator.model.unit.action.UnitAction;
import wow.simulator.simulation.TimeAware;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

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

	private record ExtraArgs(Unit caster) {}

	private Map<TestEvent, ExtraArgs> eventsExtraArgs = new HashMap<>();

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
	public void increasedResource(ResourceType type, Spell spell, Unit target, int amount, int current, int previous, boolean crit, Unit caster) {
		if (spell == null && ignoreRegen) {
			return;
		}
		var event = addEvent(new IncreasedResource(now(), amount, type, crit, target, getAbilityId(spell)));
		addArgs(event, caster);
	}

	@Override
	public void decreasedResource(ResourceType type, Spell spell, Unit target, int amount, int current, int previous, boolean crit, Unit caster) {
		var event = addEvent(new DecreasedResource(now(), amount, type, crit, target, getAbilityId(spell)));
		addArgs(event, caster);
	}

	private void addArgs(TestEvent event, Unit caster) {
		eventsExtraArgs.put(event, new ExtraArgs(caster));
	}

	private static String getAbilityId(Spell spell) {
		return spell != null ? spell.getName() : null;
	}

	@Override
	public void effectApplied(EffectInstance effect) {
		var time = now();
		var type = getEffectType(effect);
		var target = effect.getTarget();
		var duration = effect.getDuration();

		addEvent(new EffectApplied(time, effect.getName(), type, target, duration));
	}

	@Override
	public void effectStacked(EffectInstance effect) {
		var time = now();
		var type = getEffectType(effect);
		var target = effect.getTarget();
		var numStacks = effect.getNumStacks();

		addEvent(new EffectStacked(time, effect.getName(), type, target, numStacks));
	}

	@Override
	public void effectStacksIncreased(EffectInstance effect) {
		var time = now();
		var type = getEffectType(effect);
		var target = effect.getTarget();
		var numStacks = effect.getNumStacks();

		addEvent(new EffectStacksIncreased(time, effect.getName(), type, target, numStacks));
	}

	@Override
	public void effectStacksDecreased(EffectInstance effect) {
		var time = now();
		var type = getEffectType(effect);
		var target = effect.getTarget();
		var numStacks = effect.getNumStacks();

		addEvent(new EffectStacksDecreased(time, effect.getName(), type, target, numStacks));
	}

	@Override
	public void effectChargesIncreased(EffectInstance effect) {
		var time = now();
		var type = getEffectType(effect);
		var target = effect.getTarget();
		var numCharges = effect.getNumCharges();

		addEvent(new EffectChargesIncreased(time, effect.getName(), type, target, numCharges));
	}

	@Override
	public void effectChargesDecreased(EffectInstance effect) {
		var time = now();
		var type = getEffectType(effect);
		var target = effect.getTarget();
		var numCharges = effect.getNumCharges();

		addEvent(new EffectChargesDecreased(time, effect.getName(), type, target, numCharges));
	}

	@Override
	public void effectExpired(EffectInstance effect) {
		var time = now();
		var type = getEffectType(effect);
		var target = effect.getTarget();

		addEvent(new EffectExpired(time, effect.getName(), type, target));
	}

	@Override
	public void effectRemoved(EffectInstance effect) {
		var time = now();
		var type = getEffectType(effect);
		var target = effect.getTarget();

		addEvent(new EffectRemoved(time, effect.getName(), type, target));
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

	private TestEvent addEvent(TestEvent event) {
		events.add(event);
		return event;
	}

	private Time now() {
		return clock.now();
	}

	private boolean casterIs(TestEvent event, Unit caster) {
		var extraArgs = eventsExtraArgs.get(event);

		return extraArgs != null && extraArgs.caster() == caster;
	}

	public int getDamageDone(String abilityName, Unit target, Unit caster) {
		return getDamageEvents(abilityName, target, caster)
				.mapToInt(DecreasedResource::amount)
				.sum();
	}

	public int getDamageDone(int eventIdx, String abilityName, Unit target, Unit caster) {
		return getDamageEvents(abilityName, target, caster)
				.skip(eventIdx)
				.findFirst()
				.orElseThrow()
				.amount();
	}

	public int getHealthGained(String abilityName, Unit target) {
		return getHealthGainedEvents(abilityName, target)
				.mapToInt(IncreasedResource::amount)
				.sum();
	}

	public int getHealthGained(int eventIdx, String abilityName, Unit target) {
		return getHealthGainedEvents(abilityName, target)
				.skip(eventIdx)
				.findFirst()
				.orElseThrow()
				.amount();
	}

	public int getManaPaid(String abilityName, Unit target) {
		return getManaPaidEvents(abilityName, target)
				.mapToInt(DecreasedResource::amount)
				.sum();
	}

	public int getManaPaid(int eventIdx, String abilityName, Player player) {
		return getManaPaidEvents(abilityName, player)
				.skip(eventIdx)
				.findFirst()
				.orElseThrow()
				.amount();
	}

	public int getManaGained(String abilityName, Unit target) {
		return getManaGainedEvents(abilityName, target)
				.mapToInt(IncreasedResource::amount)
				.sum();
	}

	public double getCastTime(String abilityName, Player player) {
		return getBeginCastEvents(abilityName, player)
				.findFirst()
				.orElseThrow()
				.castTime()
				.getSeconds();
	}

	public double getCooldown(String abilityName, Player player) {
		return getCooldownStartedEvents(abilityName, player)
				.findFirst()
				.orElseThrow()
				.duration()
				.getSeconds();
	}

	public AnyDuration getEffectDuration(String abilityName, Unit target) {
		return getEffectAppliedEvents(abilityName, target)
				.findFirst()
				.orElseThrow()
				.duration();
	}

	public Stream<DecreasedResource> getDamageEvents(String abilityName, Unit target, Unit caster) {
		return getDecreasedResourceEvents()
				.filter(x -> x.isDamage(abilityName, target))
				.filter(event -> casterIs(event, caster));
	}

	public Stream<DecreasedResource> getManaPaidEvents(String abilityName, Unit target) {
		return getDecreasedResourceEvents()
				.filter(x -> x.isManaPaid(abilityName, target));
	}

	private Stream<DecreasedResource> getDecreasedResourceEvents() {
		return events.stream()
				.filter(x -> x instanceof DecreasedResource)
				.map(x -> (DecreasedResource) x);
	}

	public Stream<IncreasedResource> getHealthGainedEvents(String abilityName, Unit target) {
		return getIncreasedResourceEvents()
				.filter(x -> x.isHealing(abilityName, target));
	}

	public Stream<IncreasedResource> getManaGainedEvents(String abilityName, Unit target) {
		return getIncreasedResourceEvents()
				.filter(x -> x.isManaGain(abilityName, target));
	}

	private Stream<IncreasedResource> getIncreasedResourceEvents() {
		return events.stream()
				.filter(x -> x instanceof IncreasedResource)
				.map(x -> (IncreasedResource) x);
	}

	public Stream<BeginCast> getBeginCastEvents(String abilityName, Player caster) {
		return getBeginCastEvents()
				.filter(x -> x.caster() == caster && x.spell().equals(abilityName));
	}

	private Stream<BeginCast> getBeginCastEvents() {
		return events.stream()
				.filter(x -> x instanceof BeginCast)
				.map(x -> (BeginCast) x);
	}

	public Stream<CooldownStarted> getCooldownStartedEvents(String abilityName, Player caster) {
		var abilityId = AbilityId.parse(abilityName);
		var cooldownId = CooldownId.of(abilityId);

		return getCooldownStartedEvents()
				.filter(x -> x.cooldownId().equals(cooldownId))
				.filter(x -> x.caster() == caster);
	}

	private Stream<CooldownStarted> getCooldownStartedEvents() {
		return events.stream()
				.filter(x -> x instanceof CooldownStarted)
				.map(x -> (CooldownStarted) x);
	}

	public Stream<EffectApplied> getEffectAppliedEvents(String abilityName, Unit target) {
		return getEffectAppliedEvents()
				.filter(x -> x.name().equals(abilityName))
				.filter(x -> x.target() == target);
	}

	private Stream<EffectApplied> getEffectAppliedEvents() {
		return events.stream()
				.filter(x -> x instanceof EffectApplied)
				.map(x -> (EffectApplied) x);
	}
}
