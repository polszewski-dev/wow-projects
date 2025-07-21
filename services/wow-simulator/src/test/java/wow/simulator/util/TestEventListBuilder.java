package wow.simulator.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import wow.commons.model.AnyDuration;
import wow.commons.model.Duration;
import wow.commons.model.spell.AbilityId;
import wow.commons.model.spell.CooldownId;
import wow.commons.model.spell.ResourceType;
import wow.commons.model.talent.TalentId;
import wow.simulator.model.time.Time;
import wow.simulator.model.unit.Unit;

import java.util.ArrayList;
import java.util.List;

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

	public TestEventListBuilder beginCast(Unit caster, AbilityId abilityId) {
		return beginCast(caster, abilityId, 0);
	}

	public TestEventListBuilder beginCast(Unit caster, AbilityId abilityId, double castTime) {
		return addEvent(new BeginCast(time, caster, abilityId, Duration.seconds(castTime)));
	}

	public TestEventListBuilder endCast(Unit caster, AbilityId abilityId) {
		return addEvent(new EndCast(time, caster, abilityId));
	}

	public TestEventListBuilder beginChannel(Unit caster, AbilityId abilityId, double channelTime) {
		return addEvent(new BeginChannel(time, caster, abilityId, Duration.seconds(channelTime)));
	}

	public TestEventListBuilder endChannel(Unit caster, AbilityId abilityId) {
		return addEvent(new EndChannel(time, caster, abilityId));
	}

	public TestEventListBuilder canNotBeCasted(Unit caster, AbilityId abilityId) {
		return addEvent(new CanNotBeCasted(time, caster, abilityId));
	}

	public TestEventListBuilder castInterrupted(Unit caster, AbilityId abilityId) {
		return addEvent(new CastInterrupted(time, caster, abilityId));
	}

	public TestEventListBuilder channelInterrupted(Unit caster, AbilityId abilityId) {
		return addEvent(new ChannelInterrupted(time, caster, abilityId));
	}

	public TestEventListBuilder spellResisted(Unit caster, AbilityId abilityId, Unit target) {
		return addEvent(new SpellResisted(time, caster, abilityId, target));
	}

	public TestEventListBuilder increasedResource(int amount, ResourceType type, Unit target, AbilityId abilityId) {
		return increasedResource(amount, type, false, target, abilityId);
	}

	public TestEventListBuilder increasedResource(int amount, ResourceType type, boolean crit, Unit target, AbilityId abilityId) {
		return increasedResource(amount, type, crit, target, abilityId.getName());
	}

	public TestEventListBuilder increasedResource(int amount, ResourceType type, boolean crit, Unit target, String spellName) {
		return addEvent(new IncreasedResource(time, amount, type, crit, target, spellName));
	}

	public TestEventListBuilder increasedResource(int amount, ResourceType type, Unit target, String spellName) {
		return increasedResource(amount, type, false, target, spellName);
	}

	public TestEventListBuilder decreasedResource(int amount, ResourceType type, Unit target, AbilityId abilityId) {
		return decreasedResource(amount, type, false, target, abilityId);
	}

	public TestEventListBuilder decreasedResource(int amount, ResourceType type, boolean crit, Unit target, AbilityId abilityId) {
		return addEvent(new DecreasedResource(time, amount, type, crit, target, abilityId.getName()));
	}

	public TestEventListBuilder decreasedResource(int amount, ResourceType type, Unit target, String itemName) {
		return decreasedResource(amount, type, false, target, itemName);
	}

	public TestEventListBuilder decreasedResource(int amount, ResourceType type, boolean crit, Unit target, String itemName) {
		return addEvent(new DecreasedResource(time, amount, type, crit, target, itemName));
	}

	public TestEventListBuilder effectApplied(AbilityId abilityId, Unit target, double duration) {
		return addEvent(new EffectApplied(time, abilityId, target, Duration.seconds(duration)));
	}

	public TestEventListBuilder effectApplied(AbilityId abilityId, Unit target, AnyDuration duration) {
		return addEvent(new EffectApplied(time, abilityId, target, duration));
	}

	public TestEventListBuilder effectApplied(TalentId talentId, Unit target, double duration) {
		return addEvent(new TalentEffectApplied(time, talentId, target, Duration.seconds(duration)));
	}

	public TestEventListBuilder effectApplied(TalentId talentId, Unit target, AnyDuration duration) {
		return addEvent(new TalentEffectApplied(time, talentId, target, duration));
	}

	public TestEventListBuilder effectApplied(String itemName, Unit target, double duration) {
		return addEvent(new ItemEffectApplied(time, itemName, target, Duration.seconds(duration)));
	}

	public TestEventListBuilder effectApplied(String itemName, Unit target, AnyDuration duration) {
		return addEvent(new ItemEffectApplied(time, itemName, target, duration));
	}

	public TestEventListBuilder effectStacked(AbilityId abilityId, Unit target, int numStacks) {
		return addEvent(new EffectStacked(time, abilityId, target, numStacks));
	}

	public TestEventListBuilder effectStacked(TalentId talentId, Unit target, int numStacks) {
		return addEvent(new TalentEffectStacked(time, talentId, target, numStacks));
	}

	public TestEventListBuilder effectStacked(String itemName, Unit target, int numStacks) {
		return addEvent(new ItemEffectStacked(time, itemName, target, numStacks));
	}

	public TestEventListBuilder effectStacksIncreased(AbilityId abilityId, Unit target, int numStacks) {
		return addEvent(new EffectStacksIncreased(time, abilityId, target, numStacks));
	}

	public TestEventListBuilder effectStacksIncreased(TalentId talentId, Unit target, int numStacks) {
		return addEvent(new TalentEffectStacksIncreased(time, talentId, target, numStacks));
	}

	public TestEventListBuilder effectStacksIncreased(String itemName, Unit target, int numStacks) {
		return addEvent(new ItemEffectStacksIncreased(time, itemName, target, numStacks));
	}

	public TestEventListBuilder effectStacksDecreased(AbilityId abilityId, Unit target, int numStacks) {
		return addEvent(new EffectStacksDecreased(time, abilityId, target, numStacks));
	}

	public TestEventListBuilder effectStacksDecreased(TalentId talentId, Unit target, int numStacks) {
		return addEvent(new TalentEffectStacksDecreased(time, talentId, target, numStacks));
	}

	public TestEventListBuilder effectChargesIncreased(AbilityId abilityId, Unit target, int numCharges) {
		return addEvent(new EffectChargesIncreased(time, abilityId, target, numCharges));
	}

	public TestEventListBuilder effectChargesIncreased(TalentId talentId, Unit target, int numCharges) {
		return addEvent(new TalentEffectChargesIncreased(time, talentId, target, numCharges));
	}

	public TestEventListBuilder effectChargesDecreased(AbilityId abilityId, Unit target, int numCharges) {
		return addEvent(new EffectChargesDecreased(time, abilityId, target, numCharges));
	}

	public TestEventListBuilder effectChargesDecreased(TalentId talentId, Unit target, int numCharges) {
		return addEvent(new TalentEffectChargesDecreased(time, talentId, target, numCharges));
	}

	public TestEventListBuilder effectExpired(AbilityId abilityId, Unit target) {
		return addEvent(new EffectExpired(time, abilityId, target));
	}

	public TestEventListBuilder effectExpired(TalentId talentId, Unit target) {
		return addEvent(new TalentEffectExpired(time, talentId, target));
	}

	public TestEventListBuilder effectExpired(String itemName, Unit target) {
		return addEvent(new ItemEffectExpired(time, itemName, target));
	}

	public TestEventListBuilder effectRemoved(AbilityId abilityId, Unit target) {
		return addEvent(new EffectRemoved(time, abilityId, target));
	}

	public TestEventListBuilder effectRemoved(TalentId talentId, Unit target) {
		return addEvent(new TalentEffectRemoved(time, talentId, target));
	}

	public TestEventListBuilder effectRemoved(String itemName, Unit target) {
		return addEvent(new ItemEffectRemoved(time, itemName, target));
	}

	public TestEventListBuilder cooldownStarted(Unit caster, AbilityId abilityId, double duration) {
		return cooldownStarted(caster, CooldownId.of(abilityId), duration);
	}

	public TestEventListBuilder cooldownStarted(Unit caster, CooldownId cooldownId, double duration) {
		return addEvent(new CooldownStarted(time, caster, cooldownId, Duration.seconds(duration)));
	}

	public TestEventListBuilder cooldownExpired(Unit caster, AbilityId abilityId) {
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
