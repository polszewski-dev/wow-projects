package wow.simulator;

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
import wow.commons.model.item.Item;
import wow.commons.model.pve.PhaseId;
import wow.commons.model.spells.ResourceType;
import wow.commons.model.spells.Spell;
import wow.simulator.config.SimulatorContext;
import wow.simulator.config.SimulatorContextSource;
import wow.simulator.log.GameLog;
import wow.simulator.log.handler.GameLogHandler;
import wow.simulator.model.action.Action;
import wow.simulator.model.action.ActionId;
import wow.simulator.model.time.Clock;
import wow.simulator.model.time.Time;
import wow.simulator.model.unit.Player;
import wow.simulator.model.unit.Target;
import wow.simulator.model.unit.Unit;
import wow.simulator.simulation.SimulationContext;
import wow.simulator.simulation.TimeAware;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntConsumer;

import static wow.character.model.character.CharacterTemplateId.DESTRO_SHADOW;
import static wow.commons.model.character.CharacterClassId.WARLOCK;
import static wow.commons.model.character.CreatureType.BEAST;
import static wow.commons.model.character.RaceId.ORC;

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
		return new SimulationContext(clock, gameLog, getCharacterCalculationService());
	}

	protected Character getNakedCharacter() {
		Character character = getCharacterService().createCharacter(WARLOCK, ORC, 70, PhaseId.TBC_P5);
		Enemy enemy = new Enemy(BEAST, 3);
		character.setTargetEnemy(enemy);
		getCharacterService().applyCharacterTemplate(character, DESTRO_SHADOW);
		character.getEquipment().reset();
		character.getBuffs().reset();
		character.getTalents().reset();
		getCharacterService().updateAfterRestrictionChange(character);
		return character;
	}

	@Getter
	@Setter
	public static class EventCollectingHandler implements GameLogHandler, TimeAware {
		List<String> events = new ArrayList<>();
		Clock clock;

		@Override
		public void beginGcd(Unit caster, Spell spell, Unit target, ActionId actionId) {
			addEvent("beginGcd: caster = %s, spell = %s, target = %s", caster, spell, target);
		}

		@Override
		public void endGcd(Unit caster, Spell spell, Unit target, ActionId actionId) {
			addEvent("endGcd: caster = %s, spell = %s, target = %s", caster, spell, target);
		}

		@Override
		public void beginCast(Unit caster, Spell spell, Unit target, ActionId actionId) {
			addEvent("beginCast: caster = %s, spell = %s, target = %s", caster, spell, target);
		}

		@Override
		public void endCast(Unit caster, Spell spell, Unit target, ActionId actionId) {
			addEvent("endCast: caster = %s, spell = %s, target = %s", caster, spell, target);
		}

		@Override
		public void canNotBeCasted(Unit caster, Spell spell, Unit target, ActionId actionId) {
			addEvent("canNotBeCasted: caster = %s, spell = %s, target = %s", caster, spell, target);
		}

		@Override
		public void increasedResource(ResourceType type, Spell spell, Unit target, int amount, int current, int previous) {
			addEvent("increasedResource: target = %s, spell = %s, amount = %s, type = %s", target, spell, amount, type);
		}

		@Override
		public void decreasedResource(ResourceType type, Spell spell, Unit target, int amount, int current, int previous) {
			addEvent("decreasedResource: target = %s, spell = %s, amount = %s, type = %s", target, spell, amount, type);
		}

		@Override
		public void simulationEnded() {
			addEvent("simulationEnded");
		}

		private void addEvent(String str, Object... args) {
			events.add("%s> %s".formatted(clock.now(), str.formatted(args)));
		}
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

	protected void setHealth(Unit unit, int amount) {
		unit.decreaseHealth(unit.getCurrentHealth() - amount, null);
	}

	protected void setMana(Unit unit, int amount) {
		unit.decreaseMana(unit.getCurrentMana() - amount, null);
	}

	protected SimulationContext simulationContext;
	protected Clock clock;
	protected Player player;
	protected Target target;

	protected void setupTestObjects() {
		simulationContext = getSimulationContext();
		clock = simulationContext.getClock();

		player = new Player("Player", getNakedCharacter());
		target = new Target("Target", player.getCharacter().getTargetEnemy());

		player.setTarget(target);

		player.setSimulationContext(simulationContext);
		target.setSimulationContext(simulationContext);

		player.setOnPendingActionQueueEmpty(x -> x.idleUntil(Time.INFINITY));
		target.setOnPendingActionQueueEmpty(x -> x.idleUntil(Time.INFINITY));
	}
}