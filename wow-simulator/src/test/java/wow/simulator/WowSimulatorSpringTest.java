package wow.simulator;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import wow.character.model.character.Character;
import wow.character.model.character.Enemy;
import wow.character.model.equipment.EquippableItem;
import wow.commons.model.Duration;
import wow.commons.model.buffs.BuffId;
import wow.commons.model.item.Item;
import wow.commons.model.pve.PhaseId;
import wow.commons.model.spells.ResourceType;
import wow.commons.model.spells.Spell;
import wow.commons.model.spells.SpellId;
import wow.commons.model.talents.TalentId;
import wow.simulator.config.SimulatorContext;
import wow.simulator.config.SimulatorContextSource;
import wow.simulator.log.GameLog;
import wow.simulator.log.handler.GameLogHandler;
import wow.simulator.model.action.Action;
import wow.simulator.model.rng.Rng;
import wow.simulator.model.time.Clock;
import wow.simulator.model.time.Time;
import wow.simulator.model.unit.Player;
import wow.simulator.model.unit.Target;
import wow.simulator.model.unit.Unit;
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
import static wow.simulator.WowSimulatorSpringTest.EventCollectingHandler.*;

/**
 * User: POlszewski
 * Date: 2023-08-09
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = WowSimulatorSpringTestConfig.class)
@Getter
public abstract class WowSimulatorSpringTest implements SimulatorContextSource {
	@Autowired
	protected SimulatorContext simulatorContext;

	protected SimulationContext getSimulationContext() {
		Clock clock = new Clock();
		GameLog gameLog = new GameLog();
		return new SimulationContext(clock, gameLog, () -> rng, getCharacterCalculationService());
	}

	protected Character getNakedCharacter() {
		Character character = getCharacterService().createCharacter(WARLOCK, ORC, 70, PhaseId.TBC_P5);
		Enemy enemy = new Enemy(BEAST, 3);
		character.setTargetEnemy(enemy);
		getCharacterService().applyCharacterTemplate(character, DESTRO_SHADOW);
		character.getEquipment().reset();
		character.getBuffs().reset();
		character.getTalents().reset();
		enemy.getDebuffs().reset();
		getCharacterService().updateAfterRestrictionChange(character);
		return character;
	}

	@Getter
	@Setter
	public static class EventCollectingHandler implements GameLogHandler, TimeAware {
		List<Event> events = new ArrayList<>();
		Clock clock;

		protected interface Event {}

		public record BeginGcd(Time time, Unit caster) implements Event {}
		public record EndGcd(Time time, Unit caster) implements Event {}
		public record BeginCast(Time time, Unit caster, SpellId spell, Unit target) implements Event {}
		public record EndCast(Time time, Unit caster, SpellId spell, Unit target) implements Event {}
		public record CanNotBeCasted(Time time, Unit caster, SpellId spell, Unit target) implements Event {}
		public record CastInterrupted(Time time, Unit caster, SpellId spell, Unit target) implements Event {}
		public record SpellMissed(Time time, Unit caster, SpellId spell, Unit target) implements Event {}
		public record IncreasedResource(Time time, int amount, ResourceType type, boolean crit, Unit target, SpellId spell) implements Event {}
		public record DecreasedResource(Time time, int amount, ResourceType type, boolean crit, Unit target, SpellId spell) implements Event {}
		public record SimulationEnded(Time time) implements Event {}

		@Override
		public void beginGcd(Unit caster, Action action) {
			addEvent(new BeginGcd(now(), caster));
		}

		@Override
		public void endGcd(Unit caster, Action action) {
			addEvent(new EndGcd(now(), caster));
		}

		@Override
		public void beginCast(Unit caster, Spell spell, Unit target, Action action) {
			addEvent(new BeginCast(now(), caster, getSpellId(spell), target));
		}

		@Override
		public void endCast(Unit caster, Spell spell, Unit target, Action action) {
			addEvent(new EndCast(now(), caster, getSpellId(spell), target));
		}

		@Override
		public void canNotBeCasted(Unit caster, Spell spell, Unit target, Action action) {
			addEvent(new CanNotBeCasted(now(), caster, getSpellId(spell), target));
		}

		@Override
		public void castInterrupted(Unit caster, Spell spell, Unit target, Action action) {
			addEvent(new CastInterrupted(now(), caster, getSpellId(spell), target));
		}

		@Override
		public void spellMissed(Unit caster, Spell spell, Unit target) {
			addEvent(new SpellMissed(now(), caster, getSpellId(spell), target));
		}

		@Override
		public void increasedResource(ResourceType type, Spell spell, Unit target, int amount, int current, int previous, boolean crit) {
			addEvent(new IncreasedResource(now(), amount, type, crit, target, getSpellId(spell)));
		}

		@Override
		public void decreasedResource(ResourceType type, Spell spell, Unit target, int amount, int current, int previous, boolean crit) {
			addEvent(new DecreasedResource(now(), amount, type, crit, target, getSpellId(spell)));
		}

		private SpellId getSpellId(Spell spell) {
			return spell != null ? spell.getSpellId() : null;
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

		public EventListBuilder beginCast(Unit caster, SpellId spellId, Unit target) {
			return addEvent(new BeginCast(time, caster, spellId, target));
		}

		public EventListBuilder endCast(Unit caster, SpellId spellId, Unit target) {
			return addEvent(new EndCast(time, caster, spellId, target));
		}

		public EventListBuilder canNotBeCasted(Unit caster, SpellId spellId, Unit target) {
			return addEvent(new CanNotBeCasted(time, caster, spellId, target));
		}

		public EventListBuilder castInterrupted(Unit caster, SpellId spellId, Unit target) {
			return addEvent(new CastInterrupted(time, caster, spellId, target));
		}

		public EventListBuilder spellMissed(Unit caster, SpellId spellId, Unit target) {
			return addEvent(new SpellMissed(time, caster, spellId, target));
		}

		public EventListBuilder increasedResource(int amount, ResourceType type, Unit target, SpellId spellId) {
			return increasedResource(amount, type, false, target, spellId);
		}

		public EventListBuilder increasedResource(int amount, ResourceType type, boolean crit, Unit target, SpellId spellId) {
			return addEvent(new IncreasedResource(time, amount, type, crit, target, spellId));
		}

		public EventListBuilder decreasedResource(int amount, ResourceType type, Unit target, SpellId spellId) {
			return decreasedResource(amount, type, false, target, spellId);
		}

		public EventListBuilder decreasedResource(int amount, ResourceType type, boolean crit, Unit target, SpellId spellId) {
			return addEvent(new DecreasedResource(time, amount, type, crit, target, spellId));
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
		Item item = getItemRepository().getItem(itemName, player.getCharacter().getPhaseId()).orElseThrow();
		player.equip(new EquippableItem(item));
	}

	protected void enableTalent(TalentId talentId, int rank) {
		player.getCharacter().getTalents().enableTalent(talentId, rank);
		getCharacterService().updateAfterRestrictionChange(player.getCharacter());
	}

	protected void enableBuff(BuffId buffId, int rank) {
		player.getCharacter().getBuffs().enable(buffId, rank);
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
	protected Target target;
	protected EventCollectingHandler handler;

	protected static class TestRng implements Rng {
		public boolean hitRoll = true;
		public boolean critRoll = false;

		@Override
		public boolean hitRoll(double chance, SpellId spellId) {
			return hitRoll;
		}

		@Override
		public boolean critRoll(double chance, SpellId spellId) {
			return critRoll;
		}
	};

	protected TestRng rng = new TestRng();

	protected void setupTestObjects() {
		simulationContext = getSimulationContext();
		clock = simulationContext.getClock();

		simulation = new Simulation(simulationContext);

		player = new Player("Player", getNakedCharacter());
		target = new Target("Target", player.getCharacter().getTargetEnemy());

		player.setTarget(target);

		player.setSimulationContext(simulationContext);
		target.setSimulationContext(simulationContext);

		player.setOnPendingActionQueueEmpty(x -> x.idleUntil(Time.INFINITY));
		target.setOnPendingActionQueueEmpty(x -> x.idleUntil(Time.INFINITY));
	}
}