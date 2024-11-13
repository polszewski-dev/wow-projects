package wow.simulator;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import wow.character.model.character.NonPlayerCharacter;
import wow.character.model.character.PlayerCharacter;
import wow.character.model.equipment.EquippableItem;
import wow.commons.model.Duration;
import wow.commons.model.buff.BuffId;
import wow.commons.model.item.Item;
import wow.commons.model.spell.Ability;
import wow.commons.model.spell.AbilityId;
import wow.commons.model.spell.CooldownId;
import wow.commons.model.spell.ResourceType;
import wow.commons.model.talent.TalentId;
import wow.simulator.config.SimulatorContext;
import wow.simulator.config.SimulatorContextSource;
import wow.simulator.log.GameLog;
import wow.simulator.log.handler.GameLogHandler;
import wow.simulator.model.action.Action;
import wow.simulator.model.cooldown.CooldownInstance;
import wow.simulator.model.effect.EffectInstance;
import wow.simulator.model.rng.Rng;
import wow.simulator.model.time.Clock;
import wow.simulator.model.time.Time;
import wow.simulator.model.unit.Player;
import wow.simulator.model.unit.Unit;
import wow.simulator.model.unit.action.CastSpellAction;
import wow.simulator.model.unit.action.UnitAction;
import wow.simulator.model.unit.impl.NonPlayerImpl;
import wow.simulator.model.unit.impl.PlayerImpl;
import wow.simulator.simulation.Simulation;
import wow.simulator.simulation.SimulationContext;
import wow.simulator.simulation.TimeAware;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntConsumer;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.character.model.character.CharacterTemplateId.DESTRO_SHADOW;
import static wow.commons.model.character.CharacterClassId.WARLOCK;
import static wow.commons.model.character.CreatureType.BEAST;
import static wow.commons.model.character.RaceId.ORC;
import static wow.commons.model.pve.PhaseId.TBC_P5;
import static wow.simulator.WowSimulatorSpringTest.EventCollectingHandler.*;

/**
 * User: POlszewski
 * Date: 2023-08-09
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = WowSimulatorSpringTestConfig.class)
@TestPropertySource({
		"classpath:wow-commons.properties",
		"classpath:wow-character.properties",
		"classpath:wow-simulator.properties",
		"classpath:application.properties"
})
@Getter
public abstract class WowSimulatorSpringTest implements SimulatorContextSource {
	@Autowired
	protected SimulatorContext simulatorContext;

	protected SimulationContext getSimulationContext() {
		Clock clock = new Clock();
		GameLog gameLog = new GameLog();
		return new SimulationContext(clock, gameLog, () -> rng, getCharacterCalculationService());
	}

	protected PlayerCharacter getNakedCharacter() {
		var character = getCharacterService().createPlayerCharacter(WARLOCK, ORC, 70, TBC_P5);
		getCharacterService().applyCharacterTemplate(character, DESTRO_SHADOW);
		character.resetEquipment();
		character.resetBuffs();
		character.getTalents().reset();
		getCharacterService().updateAfterRestrictionChange(character);
		return character;
	}

	protected NonPlayerCharacter getEnemy() {
		var enemy = getCharacterService().createNonPlayerCharacter(BEAST, 73, TBC_P5);
		enemy.resetBuffs();
		return enemy;
	}

	@Getter
	@Setter
	public static class EventCollectingHandler implements GameLogHandler, TimeAware {
		List<Event> events = new ArrayList<>();
		Clock clock;

		protected interface Event {}

		public record BeginGcd(Time time, Unit caster) implements Event {}
		public record EndGcd(Time time, Unit caster) implements Event {}
		public record BeginCast(Time time, Unit caster, AbilityId spell) implements Event {}
		public record EndCast(Time time, Unit caster, AbilityId spell) implements Event {}
		public record BeginChannel(Time time, Unit caster, AbilityId spell) implements Event {}
		public record EndChannel(Time time, Unit caster, AbilityId spell) implements Event {}
		public record CanNotBeCasted(Time time, Unit caster, AbilityId spell) implements Event {}
		public record CastInterrupted(Time time, Unit caster, AbilityId spell) implements Event {}
		public record ChannelInterrupted(Time time, Unit caster, AbilityId spell) implements Event {}
		public record SpellResisted(Time time, Unit caster, AbilityId spell, Unit target) implements Event {}
		public record IncreasedResource(Time time, int amount, ResourceType type, boolean crit, Unit target, AbilityId spell) implements Event {}
		public record DecreasedResource(Time time, int amount, ResourceType type, boolean crit, Unit target, AbilityId spell) implements Event {}
		public record EffectApplied(Time time, AbilityId spell, Unit target) implements Event {}
		public record EffectStacked(Time time, AbilityId spell, Unit target) implements Event {}
		public record EffectExpired(Time time, AbilityId spell, Unit target) implements Event {}
		public record EffectRemoved(Time time, AbilityId spell, Unit target) implements Event {}
		public record CooldownStarted(Time time, Unit caster, CooldownId cooldownId) implements Event {}
		public record CooldownExpired(Time time, Unit caster, CooldownId cooldownId) implements Event {}
		public record SimulationEnded(Time time) implements Event {}

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
			addEvent(new BeginCast(now(), action.getOwner(), action.getAbilityId()));
		}

		@Override
		public void endCast(CastSpellAction action) {
			addEvent(new EndCast(now(), action.getOwner(), action.getAbilityId()));
		}

		@Override
		public void beginChannel(CastSpellAction action) {
			addEvent(new BeginChannel(now(), action.getOwner(), action.getAbilityId()));
		}

		@Override
		public void endChannel(CastSpellAction action) {
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
		public void channelInterrupted(CastSpellAction action) {
			addEvent(new ChannelInterrupted(now(), action.getOwner(), action.getAbilityId()));
		}

		@Override
		public void spellResisted(CastSpellAction action, Unit target) {
			addEvent(new SpellResisted(now(), action.getOwner(), action.getAbilityId(), target));
		}

		@Override
		public void increasedResource(ResourceType type, Ability ability, Unit target, int amount, int current, int previous, boolean crit) {
			addEvent(new IncreasedResource(now(), amount, type, crit, target, getAbilityId(ability)));
		}

		@Override
		public void decreasedResource(ResourceType type, Ability ability, Unit target, int amount, int current, int previous, boolean crit) {
			addEvent(new DecreasedResource(now(), amount, type, crit, target, getAbilityId(ability)));
		}

		private static AbilityId getAbilityId(Ability ability) {
			return ability != null ? ability.getAbilityId() : null;
		}

		@Override
		public void effectApplied(EffectInstance effect) {
			addEvent(new EffectApplied(now(), effect.getSourceAbilityId(), effect.getTarget()));
		}

		@Override
		public void effectStacked(EffectInstance effect) {
			addEvent(new EffectStacked(now(), effect.getSourceAbilityId(), effect.getTarget()));
		}

		@Override
		public void effectExpired(EffectInstance effect) {
			addEvent(new EffectExpired(now(), effect.getSourceAbilityId(), effect.getTarget()));
		}

		@Override
		public void effectRemoved(EffectInstance effect) {
			addEvent(new EffectRemoved(now(), effect.getSourceAbilityId(), effect.getTarget()));
		}

		@Override
		public void cooldownStarted(CooldownInstance cooldown) {
			addEvent(new CooldownStarted(now(), cooldown.getOwner(), cooldown.getCooldownId()));
		}

		@Override
		public void cooldownExpired(CooldownInstance cooldown) {
			addEvent(new CooldownExpired(now(), cooldown.getOwner(), cooldown.getCooldownId()));
		}

		@Override
		public void simulationStarted() {
			// ignored
		}

		@Override
		public void simulationEnded() {
			// ignored
		}

		private void addEvent(Event event) {
			events.add(event);
		}

		private Time now() {
			return clock.now();
		}
	}

	@AllArgsConstructor
	protected static class EventListBuilder {
		private final Time time;
		private final List<Event> events = new ArrayList<>();

		public EventListBuilder beginGcd(Unit caster) {
			return addEvent(new BeginGcd(time, caster));
		}

		public EventListBuilder endGcd(Unit caster) {
			return addEvent(new EndGcd(time, caster));
		}

		public EventListBuilder beginCast(Unit caster, AbilityId abilityId) {
			return addEvent(new BeginCast(time, caster, abilityId));
		}

		public EventListBuilder endCast(Unit caster, AbilityId abilityId) {
			return addEvent(new EndCast(time, caster, abilityId));
		}

		public EventListBuilder beginChannel(Unit caster, AbilityId abilityId) {
			return addEvent(new BeginChannel(time, caster, abilityId));
		}

		public EventListBuilder endChannel(Unit caster, AbilityId abilityId) {
			return addEvent(new EndChannel(time, caster, abilityId));
		}

		public EventListBuilder canNotBeCasted(Unit caster, AbilityId abilityId) {
			return addEvent(new CanNotBeCasted(time, caster, abilityId));
		}

		public EventListBuilder castInterrupted(Unit caster, AbilityId abilityId) {
			return addEvent(new CastInterrupted(time, caster, abilityId));
		}

		public EventListBuilder channelInterrupted(Unit caster, AbilityId abilityId) {
			return addEvent(new ChannelInterrupted(time, caster, abilityId));
		}

		public EventListBuilder spellResisted(Unit caster, AbilityId abilityId, Unit target) {
			return addEvent(new SpellResisted(time, caster, abilityId, target));
		}

		public EventListBuilder increasedResource(int amount, ResourceType type, Unit target, AbilityId abilityId) {
			return increasedResource(amount, type, false, target, abilityId);
		}

		public EventListBuilder increasedResource(int amount, ResourceType type, boolean crit, Unit target, AbilityId abilityId) {
			return addEvent(new IncreasedResource(time, amount, type, crit, target, abilityId));
		}

		public EventListBuilder decreasedResource(int amount, ResourceType type, Unit target, AbilityId abilityId) {
			return decreasedResource(amount, type, false, target, abilityId);
		}

		public EventListBuilder decreasedResource(int amount, ResourceType type, boolean crit, Unit target, AbilityId abilityId) {
			return addEvent(new DecreasedResource(time, amount, type, crit, target, abilityId));
		}

		public EventListBuilder effectApplied(AbilityId abilityId, Unit target) {
			return addEvent(new EffectApplied(time, abilityId, target));
		}

		public EventListBuilder effectStacked(AbilityId abilityId, Unit target) {
			return addEvent(new EffectStacked(time, abilityId, target));
		}

		public EventListBuilder effectExpired(AbilityId abilityId, Unit target) {
			return addEvent(new EffectExpired(time, abilityId, target));
		}

		public EventListBuilder effectRemoved(AbilityId abilityId, Unit target) {
			return addEvent(new EffectRemoved(time, abilityId, target));
		}

		public EventListBuilder cooldownStarted(Unit caster, AbilityId abilityId) {
			return addEvent(new CooldownStarted(time, caster, CooldownId.of(abilityId)));
		}

		public EventListBuilder cooldownExpired(Unit caster, AbilityId abilityId) {
			return addEvent(new CooldownExpired(time, caster, CooldownId.of(abilityId)));
		}

		public EventListBuilder simulationEnded() {
			return addEvent(new SimulationEnded(time));
		}

		private EventListBuilder addEvent(Event event) {
			events.add(event);
			return this;
		}
	}

	protected static EventListBuilder at(double time) {
		return new EventListBuilder(Time.at(time));
	}

	private static List<Event> eventList(EventListBuilder... builders) {
		return Stream.of(builders)
				.flatMap(x -> x.events.stream())
				.toList();
	}

	protected void assertEvents(EventListBuilder... expected) {
		assertThat(handler.getEvents()).isEqualTo(eventList(expected));
	}

	protected Action newAction(int delay, Runnable runnable) {
		return new Action(clock) {
			@Override
			protected void setUp() {
				fromNowAfter(Duration.seconds(delay), runnable);
			}
		};
	}

	protected Action newTickAction(int numTicks, int interval, IntConsumer consumer) {
		return new Action(clock) {
			@Override
			protected void setUp() {
				fromNowOnEachTick(numTicks, Duration.seconds(interval), consumer);
			}
		};
	}

	protected void equip(String itemName) {
		Item item = getItemRepository().getItem(itemName, player.getPhaseId()).orElseThrow();
		player.equip(new EquippableItem(item));
	}

	protected void enableTalent(TalentId talentId, int rank) {
		player.getTalents().enableTalent(talentId, rank);
		getCharacterService().updateAfterRestrictionChange(player);
	}

	protected void enableBuff(BuffId buffId, int rank) {
		player.getBuffs().enable(buffId, rank);
	}

	protected void setHealth(Unit unit, int amount) {
		unit.decreaseHealth(unit.getCurrentHealth() - amount, false, null);
		clearEvents();
	}

	protected void setMana(Unit unit, int amount) {
		unit.decreaseMana(unit.getCurrentMana() - amount, false, null);
		clearEvents();
	}

	protected void clearEvents() {
		if (handler != null) {
			handler.getEvents().clear();
		}
	}

	protected SimulationContext simulationContext;
	protected Clock clock;
	protected Simulation simulation;
	protected Player player;
	protected Unit target;
	protected EventCollectingHandler handler;

	protected static class TestRng implements Rng {
		public boolean hitRoll = true;
		public boolean critRoll = false;

		@Override
		public boolean hitRoll(double chancePct, AbilityId abilityId) {
			return hitRoll;
		}

		@Override
		public boolean critRoll(double chancePct, AbilityId abilityId) {
			return critRoll;
		}
	};

	protected TestRng rng = new TestRng();

	protected void setupTestObjects() {
		simulationContext = getSimulationContext();
		clock = simulationContext.getClock();

		simulation = new Simulation(simulationContext);

		player = new PlayerImpl("Player", getNakedCharacter());
		target = new NonPlayerImpl("Target", getEnemy());

		player.setTarget(target);

		player.setOnPendingActionQueueEmpty(x -> x.idleUntil(Time.INFINITY));
		target.setOnPendingActionQueueEmpty(x -> x.idleUntil(Time.INFINITY));

		simulationContext.shareSimulationContext(player);
		simulationContext.shareSimulationContext(target);
	}
}