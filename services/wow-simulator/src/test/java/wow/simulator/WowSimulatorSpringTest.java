package wow.simulator;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import wow.character.model.equipment.EquippableItem;
import wow.commons.model.Duration;
import wow.commons.model.buff.BuffId;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.character.CharacterClassId;
import wow.commons.model.effect.AbilitySource;
import wow.commons.model.item.Item;
import wow.commons.model.item.ItemSource;
import wow.commons.model.spell.AbilityId;
import wow.commons.model.spell.CooldownId;
import wow.commons.model.spell.ResourceType;
import wow.commons.model.spell.Spell;
import wow.commons.model.talent.TalentId;
import wow.commons.model.talent.TalentSource;
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
import wow.simulator.model.unit.NonPlayer;
import wow.simulator.model.unit.Player;
import wow.simulator.model.unit.Unit;
import wow.simulator.model.unit.action.CastSpellAction;
import wow.simulator.model.unit.action.UnitAction;
import wow.simulator.simulation.Simulation;
import wow.simulator.simulation.SimulationContext;
import wow.simulator.simulation.TimeAware;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntConsumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.character.model.character.CharacterTemplateId.DESTRO_SHADOW;
import static wow.character.model.character.CharacterTemplateId.SHADOW;
import static wow.commons.model.character.CharacterClassId.PRIEST;
import static wow.commons.model.character.CharacterClassId.WARLOCK;
import static wow.commons.model.character.CreatureType.BEAST;
import static wow.commons.model.character.RaceId.ORC;
import static wow.commons.model.character.RaceId.UNDEAD;
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

	protected Player getNakedPlayer() {
		return getNakedPlayer(characterClassId, "Player");
	}

	protected Player getNakedPlayer(CharacterClassId characterClassId, String name) {
		var raceId = ORC;
		var charTemplateId = DESTRO_SHADOW;

		if (characterClassId == PRIEST) {
			raceId = UNDEAD;
			charTemplateId = SHADOW;
		}

		var player = getCharacterService().createPlayerCharacter(
				characterClassId, raceId, 70, TBC_P5, Player.getFactory(name)
		);

		getCharacterService().applyCharacterTemplate(player, charTemplateId);
		player.resetEquipment();
		player.resetBuffs();
		player.getTalents().reset();
		player.getConsumables().reset();
		getCharacterService().updateAfterRestrictionChange(player);

		player.setOnPendingActionQueueEmpty(x -> x.idleUntil(Time.INFINITY));
		simulationContext.shareSimulationContext(player);

		return player;
	}

	protected NonPlayer getEnemy() {
		return getEnemy("Target");
	}

	protected NonPlayer getEnemy(String name) {
		var enemy = getCharacterService().createNonPlayerCharacter(BEAST, 73, TBC_P5, NonPlayer.getFactory(name));

		enemy.resetBuffs();

		enemy.setOnPendingActionQueueEmpty(x -> x.idleUntil(Time.INFINITY));
		simulationContext.shareSimulationContext(enemy);

		return enemy;
	}

	@Getter
	@Setter
	public static class EventCollectingHandler implements GameLogHandler, TimeAware {
		List<Event> events = new ArrayList<>();
		Clock clock;

		public interface Event {
			Time time();

			default boolean nameContains(String cooldown) {
				return getClass().getSimpleName().toLowerCase().contains(cooldown.toLowerCase());
			}

			default boolean isDamage() {
				return this instanceof DecreasedResource dr && dr.type == ResourceType.HEALTH;
			}

			default boolean isTalentEffect() {
				return nameContains("TalentEffect");
			}

			default boolean isEffect() {
				return nameContains("Effect");
			}

			default boolean isCooldown() {
				return nameContains("Cooldown");
			}

			default boolean isSpellResisted() {
				return this instanceof SpellResisted;
			}

			default boolean isEffectApplied() {
				return this instanceof EffectApplied;
			}
		}

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
		public record IncreasedResource(Time time, int amount, ResourceType type, boolean crit, Unit target, String spell) implements Event {}
		public record DecreasedResource(Time time, int amount, ResourceType type, boolean crit, Unit target, String spell) implements Event {}
		public record EffectApplied(Time time, AbilityId spell, Unit target) implements Event {}
		public record TalentEffectApplied(Time time, TalentId talentId, Unit target) implements Event {}
		public record ItemEffectApplied(Time time, String itemName, Unit target) implements Event {}
		public record EffectStacked(Time time, AbilityId spell, Unit target, int numStacks) implements Event {}
		public record TalentEffectStacked(Time time, TalentId talentId, Unit target, int numStacks) implements Event {}
		public record ItemEffectStacked(Time time, String itemName, Unit target, int numStacks) implements Event {}
		public record EffectStacksIncreased(Time time, AbilityId spell, Unit target, int numStacks) implements Event {}
		public record TalentEffectStacksIncreased(Time time, TalentId talentId, Unit target, int numStacks) implements Event {}
		public record ItemEffectStacksIncreased(Time time, String itemName, Unit target, int numStacks) implements Event {}
		public record EffectStacksDecreased(Time time, AbilityId spell, Unit target, int numStacks) implements Event {}
		public record TalentEffectStacksDecreased(Time time, TalentId talentId, Unit target, int numStacks) implements Event {}
		public record ItemEffectStacksDecreased(Time time, String itemName, Unit target, int numStacks) implements Event {}
		public record EffectChargesDecreased(Time time, AbilityId spell, Unit target, int numCharges) implements Event {}
		public record TalentEffectChargesDecreased(Time time, TalentId talentId, Unit target, int numCharges) implements Event {}
		public record ItemEffectChargesDecreased(Time time, String itemName, Unit target, int numCharges) implements Event {}
		public record EffectExpired(Time time, AbilityId spell, Unit target) implements Event {}
		public record TalentEffectExpired(Time time, TalentId talentId, Unit target) implements Event {}
		public record ItemEffectExpired(Time time, String itemName, Unit target) implements Event {}
		public record EffectRemoved(Time time, AbilityId spell, Unit target) implements Event {}
		public record TalentEffectRemoved(Time time, TalentId talentId, Unit target) implements Event {}
		public record ItemEffectRemoved(Time time, String itemName, Unit target) implements Event {}
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
		public void spellHit(CastSpellAction action, Unit target) {
			// ignore
		}

		@Override
		public void spellResisted(CastSpellAction action, Unit target) {
			addEvent(new SpellResisted(now(), action.getOwner(), action.getAbilityId(), target));
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
						addEvent(new EffectApplied(now(), s.getAbilityId(), effect.getTarget()));
				case TalentSource s ->
						addEvent(new TalentEffectApplied(now(), s.getTalentId(), effect.getTarget()));
				case ItemSource s ->
						addEvent(new ItemEffectApplied(now(), s.getName(), effect.getTarget()));
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
				case AbilitySource as ->
						addEvent(new EffectExpired(now(), as.getAbilityId(), effect.getTarget()));
				case TalentSource ts ->
						addEvent(new TalentEffectExpired(now(), ts.getTalentId(), effect.getTarget()));
				case ItemSource s ->
						addEvent(new ItemEffectExpired(now(), s.getName(), effect.getTarget()));
				default -> throw new IllegalArgumentException();
			}
		}

		@Override
		public void effectRemoved(EffectInstance effect) {
			switch (effect.getSource()) {
				case AbilitySource s ->
						addEvent(new EffectRemoved(now(), s.getAbilityId(), effect.getTarget()));
				case TalentSource s ->
						addEvent(new TalentEffectRemoved(now(), s.getTalentId(), effect.getTarget()));
				case ItemSource s ->
						addEvent(new ItemEffectRemoved(now(), s.getName(), effect.getTarget()));
				default -> throw new IllegalArgumentException();
			}
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
			return increasedResource(amount, type, crit, target, abilityId.getName());
		}

		public EventListBuilder increasedResource(int amount, ResourceType type, boolean crit, Unit target, String spellName) {
			return addEvent(new IncreasedResource(time, amount, type, crit, target, spellName));
		}

		public EventListBuilder increasedResource(int amount, ResourceType type, Unit target, String spellName) {
			return increasedResource(amount, type, false, target, spellName);
		}

		public EventListBuilder decreasedResource(int amount, ResourceType type, Unit target, AbilityId abilityId) {
			return decreasedResource(amount, type, false, target, abilityId);
		}

		public EventListBuilder decreasedResource(int amount, ResourceType type, boolean crit, Unit target, AbilityId abilityId) {
			return addEvent(new DecreasedResource(time, amount, type, crit, target, abilityId.getName()));
		}

		public EventListBuilder decreasedResource(int amount, ResourceType type, Unit target, String itemName) {
			return decreasedResource(amount, type, false, target,itemName);
		}

		public EventListBuilder decreasedResource(int amount, ResourceType type, boolean crit, Unit target, String itemName) {
			return addEvent(new DecreasedResource(time, amount, type, crit, target, itemName));
		}

		public EventListBuilder effectApplied(AbilityId abilityId, Unit target) {
			return addEvent(new EffectApplied(time, abilityId, target));
		}

		public EventListBuilder effectApplied(TalentId talentId, Unit target) {
			return addEvent(new TalentEffectApplied(time, talentId, target));
		}

		public EventListBuilder effectApplied(String itemName, Unit target) {
			return addEvent(new ItemEffectApplied(time, itemName, target));
		}

		public EventListBuilder effectStacked(AbilityId abilityId, Unit target, int numStacks) {
			return addEvent(new EffectStacked(time, abilityId, target, numStacks));
		}

		public EventListBuilder effectStacked(TalentId talentId, Unit target, int numStacks) {
			return addEvent(new TalentEffectStacked(time, talentId, target, numStacks));
		}

		public EventListBuilder effectStacked(String itemName, Unit target, int numStacks) {
			return addEvent(new ItemEffectStacked(time, itemName, target, numStacks));
		}

		public EventListBuilder effectStacksIncreased(AbilityId abilityId, Unit target, int numStacks) {
			return addEvent(new EffectStacksIncreased(time, abilityId, target, numStacks));
		}

		public EventListBuilder effectStacksIncreased(TalentId talentId, Unit target, int numStacks) {
			return addEvent(new TalentEffectStacksIncreased(time, talentId, target, numStacks));
		}

		public EventListBuilder effectStacksIncreased(String itemName, Unit target, int numStacks) {
			return addEvent(new ItemEffectStacksIncreased(time, itemName, target, numStacks));
		}

		public EventListBuilder effectStacksDecreased(AbilityId abilityId, Unit target, int numStacks) {
			return addEvent(new EffectStacksDecreased(time, abilityId, target, numStacks));
		}

		public EventListBuilder effectStacksDecreased(TalentId talentId, Unit target, int numStacks) {
			return addEvent(new TalentEffectStacksDecreased(time, talentId, target, numStacks));
		}

		public EventListBuilder effectChargesDecreased(AbilityId abilityId, Unit target, int numCharges) {
			return addEvent(new EffectChargesDecreased(time, abilityId, target, numCharges));
		}

		public EventListBuilder effectChargesDecreased(TalentId talentId, Unit target, int numCharges) {
			return addEvent(new TalentEffectChargesDecreased(time, talentId, target, numCharges));
		}

		public EventListBuilder effectExpired(AbilityId abilityId, Unit target) {
			return addEvent(new EffectExpired(time, abilityId, target));
		}

		public EventListBuilder effectExpired(TalentId talentId, Unit target) {
			return addEvent(new TalentEffectExpired(time, talentId, target));
		}

		public EventListBuilder effectExpired(String itemName, Unit target) {
			return addEvent(new ItemEffectExpired(time, itemName, target));
		}

		public EventListBuilder effectRemoved(AbilityId abilityId, Unit target) {
			return addEvent(new EffectRemoved(time, abilityId, target));
		}

		public EventListBuilder effectRemoved(TalentId talentId, Unit target) {
			return addEvent(new TalentEffectRemoved(time, talentId, target));
		}

		public EventListBuilder effectRemoved(String itemName, Unit target) {
			return addEvent(new ItemEffectRemoved(time, itemName, target));
		}

		public EventListBuilder cooldownStarted(Unit caster, AbilityId abilityId) {
			return cooldownStarted(caster, CooldownId.of(abilityId));
		}

		public EventListBuilder cooldownStarted(Unit caster, CooldownId cooldownId) {
			return addEvent(new CooldownStarted(time, caster, cooldownId));
		}

		public EventListBuilder cooldownExpired(Unit caster, AbilityId abilityId) {
			return cooldownExpired(caster, CooldownId.of(abilityId));
		}

		public EventListBuilder cooldownExpired(Unit caster, CooldownId cooldownId) {
			return addEvent(new CooldownExpired(time, caster, cooldownId));
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

	protected void assertEvents(Predicate<Event> eventPredicate, EventListBuilder... expected) {
		var filtered = handler.getEvents().stream()
				.filter(eventPredicate)
				.toList();

		assertThat(filtered).isEqualTo(eventList(expected));
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

	protected void equip(String itemName, ItemSlot itemSlot) {
		Item item = getItemRepository().getItem(itemName, player.getPhaseId()).orElseThrow();
		player.equip(new EquippableItem(item), itemSlot);
	}

	protected void equip(int itemId, ItemSlot itemSlot) {
		Item item = getItemRepository().getItem(itemId, player.getPhaseId()).orElseThrow();
		player.equip(new EquippableItem(item), itemSlot);
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

	protected CharacterClassId characterClassId = WARLOCK;

	protected static class TestRng implements Rng {
		public boolean hitRoll = true;
		public boolean critRoll = false;
		public boolean eventRoll = false;

		@Override
		public boolean hitRoll(double chancePct, Spell spell) {
			return hitRoll;
		}

		@Override
		public boolean critRoll(double chancePct, Spell spell) {
			return critRoll;
		}

		@Override
		public boolean eventRoll(double chancePct, wow.commons.model.effect.component.Event event) {
			return eventRoll;
		}
	};

	protected TestRng rng = new TestRng();

	protected void setupTestObjects() {
		simulationContext = getSimulationContext();
		clock = simulationContext.getClock();

		simulation = new Simulation(simulationContext);

		player = getNakedPlayer();
		target = getEnemy();

		player.setTarget(target);
	}
}