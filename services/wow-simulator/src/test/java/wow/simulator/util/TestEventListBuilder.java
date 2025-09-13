package wow.simulator.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import wow.commons.model.AnyDuration;
import wow.commons.model.Duration;
import wow.commons.model.spell.AbilityId;
import wow.commons.model.spell.CooldownId;
import wow.commons.model.spell.ResourceType;
import wow.simulator.model.time.Time;
import wow.simulator.model.unit.Unit;

import java.util.ArrayList;
import java.util.List;

import static wow.simulator.util.EffectType.ABILITY;
import static wow.simulator.util.TestEvent.*;

/**
 * User: POlszewski
 * Date: 2025-01-30
 */
@AllArgsConstructor
@Getter
public class TestEventListBuilder {
	private final Time time;
	private final List<TestEvent> events = new ArrayList<>();

	public TestEventListBuilder beginGcd(Unit caster) {
		return addEvent(new BeginGcd(time, caster));
	}

	public TestEventListBuilder endGcd(Unit caster) {
		return addEvent(new EndGcd(time, caster));
	}

	public TestEventListBuilder beginCast(Unit caster, String abilityName) {
		return beginCast(caster, abilityName, 0);
	}

	public TestEventListBuilder beginCast(Unit caster, String abilityName, double castTime) {
		return addEvent(new BeginCast(time, caster, abilityName, Duration.seconds(castTime)));
	}

	public TestEventListBuilder endCast(Unit caster, String abilityName) {
		return addEvent(new EndCast(time, caster, abilityName));
	}

	public TestEventListBuilder beginChannel(Unit caster, String abilityName, double channelTime) {
		return addEvent(new BeginChannel(time, caster, abilityName, Duration.seconds(channelTime)));
	}

	public TestEventListBuilder endChannel(Unit caster, String abilityName) {
		return addEvent(new EndChannel(time, caster, abilityName));
	}

	public TestEventListBuilder canNotBeCasted(Unit caster, String abilityName) {
		return addEvent(new CanNotBeCasted(time, caster, abilityName));
	}

	public TestEventListBuilder castInterrupted(Unit caster, String abilityName) {
		return addEvent(new CastInterrupted(time, caster, abilityName));
	}

	public TestEventListBuilder channelInterrupted(Unit caster, String abilityName) {
		return addEvent(new ChannelInterrupted(time, caster, abilityName));
	}

	public TestEventListBuilder spellResisted(Unit caster, String abilityName, Unit target) {
		return addEvent(new SpellResisted(time, caster, abilityName, target));
	}

	public TestEventListBuilder increasedResource(int amount, ResourceType type, Unit target, String spellName) {
		return increasedResource(amount, type, false, target, spellName);
	}

	public TestEventListBuilder increasedResource(int amount, ResourceType type, boolean crit, Unit target, String spellName) {
		return addEvent(new IncreasedResource(time, amount, type, crit, target, spellName));
	}

	public TestEventListBuilder decreasedResource(int amount, ResourceType type, Unit target, String spellName) {
		return decreasedResource(amount, type, false, target, spellName);
	}

	public TestEventListBuilder decreasedResource(int amount, ResourceType type, boolean crit, Unit target, String spellName) {
		return addEvent(new DecreasedResource(time, amount, type, crit, target, spellName));
	}

	public TestEventListBuilder effectApplied(String abilityName, Unit target, double duration) {
		return effectApplied(abilityName, ABILITY, target, duration);
	}

	public TestEventListBuilder effectApplied(String abilityName, Unit target, AnyDuration duration) {
		return effectApplied(abilityName, ABILITY, target, duration);
	}

	public TestEventListBuilder effectApplied(String name, EffectType type, Unit target, double duration) {
		return effectApplied(name, type, target, Duration.seconds(duration));
	}

	public TestEventListBuilder effectApplied(String name, EffectType type, Unit target, AnyDuration duration) {
		return addEvent(new EffectApplied(time, name, type, target, duration));
	}

	public TestEventListBuilder effectStacked(String abilityName, Unit target, int numStacks) {
		return effectStacked(abilityName, ABILITY, target, numStacks);
	}

	public TestEventListBuilder effectStacked(String name, EffectType type, Unit target, int numStacks) {
		return addEvent(new EffectStacked(time, name, type, target, numStacks));
	}

	public TestEventListBuilder effectStacksIncreased(String abilityName, Unit target, int numStacks) {
		return effectStacksIncreased(abilityName, ABILITY, target, numStacks);
	}

	public TestEventListBuilder effectStacksIncreased(String name, EffectType type, Unit target, int numStacks) {
		return addEvent(new EffectStacksIncreased(time, name, type, target, numStacks));
	}

	public TestEventListBuilder effectStacksDecreased(String abilityName, Unit target, int numStacks) {
		return effectStacksDecreased(abilityName, ABILITY, target, numStacks);
	}

	private TestEventListBuilder effectStacksDecreased(String name, EffectType type, Unit target, int numStacks) {
		return addEvent(new EffectStacksDecreased(time, name, type, target, numStacks));
	}

	public TestEventListBuilder effectChargesIncreased(String abilityName, Unit target, int numCharges) {
		return effectChargesIncreased(abilityName, ABILITY, target, numCharges);
	}

	public TestEventListBuilder effectChargesIncreased(String name, EffectType type, Unit target, int numCharges) {
		return addEvent(new EffectChargesIncreased(time, name, type, target, numCharges));
	}

	public TestEventListBuilder effectChargesDecreased(String abilityName, Unit target, int numCharges) {
		return effectChargesDecreased(abilityName, ABILITY, target, numCharges);
	}

	public TestEventListBuilder effectChargesDecreased(String name, EffectType type, Unit target, int numCharges) {
		return addEvent(new EffectChargesDecreased(time, name, type, target, numCharges));
	}

	public TestEventListBuilder effectExpired(String abilityName, Unit target) {
		return effectExpired(abilityName, ABILITY, target);
	}

	public TestEventListBuilder effectExpired(String name, EffectType type, Unit target) {
		return addEvent(new EffectExpired(time, name, type, target));
	}

	public TestEventListBuilder effectRemoved(String abilityName, Unit target) {
		return effectRemoved(abilityName, ABILITY, target);
	}

	public TestEventListBuilder effectRemoved(String name, EffectType type, Unit target) {
		return addEvent(new EffectRemoved(time, name, type, target));
	}

	public TestEventListBuilder cooldownStarted(Unit caster, String abilityName, double duration) {
		var abilityId = AbilityId.parse(abilityName);
		return cooldownStarted(caster, CooldownId.of(abilityId), duration);
	}

	public TestEventListBuilder cooldownStarted(Unit caster, CooldownId cooldownId, double duration) {
		return addEvent(new CooldownStarted(time, caster, cooldownId, Duration.seconds(duration)));
	}

	public TestEventListBuilder cooldownExpired(Unit caster, String abilityName) {
		var abilityId = AbilityId.parse(abilityName);
		return cooldownExpired(caster, CooldownId.of(abilityId));
	}

	public TestEventListBuilder cooldownExpired(Unit caster, CooldownId cooldownId) {
		return addEvent(new CooldownExpired(time, caster, cooldownId));
	}

	private TestEventListBuilder addEvent(TestEvent event) {
		events.add(event);
		return this;
	}
}
