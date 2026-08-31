package wow.simulator;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import wow.character.model.equipment.EquippableItem;
import wow.commons.model.Duration;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.character.CharacterClassId;
import wow.commons.model.character.CreatureType;
import wow.commons.model.character.RaceId;
import wow.commons.model.item.ItemId;
import wow.commons.model.pve.PhaseId;
import wow.commons.repository.pve.PhaseRepository;
import wow.commons.repository.spell.SpellRepository;
import wow.simulator.config.SimulatorContext;
import wow.simulator.config.SimulatorContextSource;
import wow.simulator.log.GameLog;
import wow.simulator.model.action.Action;
import wow.simulator.model.time.Clock;
import wow.simulator.model.time.Time;
import wow.simulator.model.unit.NonPlayer;
import wow.simulator.model.unit.Pet;
import wow.simulator.model.unit.Player;
import wow.simulator.model.unit.Unit;
import wow.simulator.model.unit.impl.NonPlayerImpl;
import wow.simulator.model.unit.impl.PlayerImpl;
import wow.simulator.model.update.Scheduler;
import wow.simulator.simulation.Simulation;
import wow.simulator.simulation.SimulationContext;
import wow.simulator.util.*;

import java.util.*;
import java.util.function.IntConsumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.character.CharacterClassId.WARLOCK;
import static wow.commons.model.character.CreatureType.BEAST;
import static wow.commons.model.character.RaceId.ORC;
import static wow.commons.model.pve.PhaseId.TBC_P5;
import static wow.simulator.util.CalcUtils.getPercentOf;
import static wow.simulator.util.CalcUtils.increaseByPct;

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

	@Autowired
	private PhaseRepository phaseRepository;

	@Autowired
	private SpellRepository spellRepository;

	protected SimulationContext getSimulationContext() {
		var clock = new Clock();
		var gameLog = new GameLog();
		var scheduler = new Scheduler(clock);
		return new SimulationContext(clock, gameLog, () -> rng, scheduler, getCharacterService(), getCharacterCalculationService(), getSpellRepository());
	}

	protected PlayerImpl getNakedPlayer(PlayerConfig playerConfig, String name) {
		return getNakedPlayer(playerConfig.characterClassId, playerConfig.raceId, name);
	}

	protected PlayerImpl getNakedPlayer(CharacterClassId characterClassId, RaceId raceId, String name) {
		int level = getLevel();

		var player = getCharacterService().createPlayerCharacter(
				name, characterClassId, raceId, level, phaseId, PlayerImpl::new
		);

		getCharacterService().updateAfterRestrictionChange(player);

		simulationContext.shareSimulationContext(player);

		return player;
	}

	private int getLevel() {
		if (level != null) {
			return level;
		}
		return phaseRepository.getPhase(phaseId).orElseThrow().getMaxLevel();
	}

	protected NonPlayer getEnemy(String name) {
		return getEnemy(name, enemyType);
	}

	protected NonPlayer getEnemy(String name, CreatureType enemyType) {
		int level = getLevel();

		var enemy = getCharacterService().createNonPlayerCharacter(
				name, enemyType, level + enemyLevelDiff, phaseId, NonPlayerImpl::new
		);

		simulationContext.shareSimulationContext(enemy);

		return enemy;
	}

	protected static TestEventListBuilder at(double time) {
		return new TestEventListBuilder(Time.at(time));
	}

	protected static TestEventListBuilder atMillis(long millis) {
		return new TestEventListBuilder(Time.atMillis(millis));
	}

	private static List<TestEvent> eventList(TestEventListBuilder... builders) {
		return Stream.of(builders)
				.flatMap(x -> x.getEvents().stream())
				.toList();
	}

	protected void assertEvents(TestEventListBuilder... expected) {
		assertThat(handler.getEvents()).isEqualTo(eventList(expected));
	}

	protected void assertEvents(Predicate<TestEvent> eventPredicate, TestEventListBuilder... expected) {
		var filtered = handler.getEvents().stream()
				.filter(eventPredicate)
				.toList();

		assertThat(filtered).isEqualTo(eventList(expected));
	}

	protected void assertDamageDone(String abilityName, Unit target, double expectedAmount) {
		assertDamageDone(abilityName, target, player, expectedAmount);
	}

	protected void assertDamageDone(String abilityName, Unit target, Unit caster, double expectedAmount) {
		var totalDamage = handler.getDamageDone(abilityName, target, caster);

		if (Math.abs(totalDamage - (int) expectedAmount) > 1) {
			assertThat(totalDamage).isEqualTo((int) expectedAmount);
		}
	}

	protected void assertDamageDone(String abilityName, double expectedAmount) {
		assertDamageDone(abilityName, target, expectedAmount);
	}

	protected void assertDamageDone(String abilityName, Unit target, double expectedBaseAmount, int pctIncrease) {
		assertDamageDone(abilityName, target, increaseByPct(expectedBaseAmount, pctIncrease));
	}

	protected void assertDamageDone(String abilityName, double expectedBaseAmount, int pctIncrease) {
		assertDamageDone(abilityName, target, increaseByPct(expectedBaseAmount, pctIncrease));
	}

	protected void assertDamageDone(int eventIdx, String abilityName, Unit target, double expectedAmount) {
		var totalDamage = handler.getDamageDone(eventIdx, abilityName, target, player);

		assertThat(Math.abs(totalDamage - (int) expectedAmount)).isLessThanOrEqualTo(1);
	}

	protected void assertDamageDone(int eventIdx, String abilityName, double expectedAmount) {
		assertDamageDone(eventIdx, abilityName, target, expectedAmount);
	}

	protected void assertDamageDone(int eventIdx, String abilityName, double expectedBaseAmount, int pctIncrease) {
		assertDamageDone(eventIdx, abilityName, target, increaseByPct(expectedBaseAmount, pctIncrease));
	}

	protected void assertDamageDone(SpellInfo spellInfo, Unit target, int sp) {
		assertDamageDone(spellInfo.name(), target, spellInfo.damage(sp));
	}

	protected void assertDamageDone(SpellInfo spellInfo, Unit target, int sp, int pctIncrease) {
		assertDamageDone(spellInfo, target, player, sp, pctIncrease);
	}

	protected void assertDamageDone(SpellInfo spellInfo, Unit target, Unit caster, int sp, int pctIncrease) {
		assertDamageDone(spellInfo.name(), target, caster, increaseByPct(spellInfo.damage(sp), pctIncrease));
	}

	protected void assertDamageDone(SpellInfo spellInfo, int sp) {
		assertDamageDone(spellInfo, target, sp);
	}

	protected void assertHealthGained(String spellName, Unit target, double expectedAmount) {
		var totalHealthGained = handler.getHealthGained(spellName, target);

		assertThat(totalHealthGained).isEqualTo((int) expectedAmount);
	}

	protected void assertHealthGained(String abilityName, Unit target, double expectedBaseAmount, int pctIncrease) {
		assertHealthGained(abilityName, target, increaseByPct(expectedBaseAmount, pctIncrease));
	}

	protected void assertHealthGained(int eventIdx, String spellName, Unit target, double expectedAmount) {
		var totalHealthGained = handler.getHealthGained(eventIdx, spellName, target);

		assertThat(totalHealthGained).isEqualTo((int) expectedAmount);
	}

	protected void assertHealthGained(SpellInfo spellInfo, Unit target, int sp) {
		assertHealthGained(spellInfo.name(), target, spellInfo.damage(sp));
	}

	protected void assertManaPaid(String abilityName, Unit target, double expectedAmount) {
		var totalManaPaid = handler.getManaPaid(abilityName, target);

		assertThat(totalManaPaid).isEqualTo((int) expectedAmount);
	}

	protected void assertManaPaid(String abilityName, Unit target, double expectedBaseAmount, int pctIncrease) {
		assertManaPaid(abilityName, target, increaseByPct(expectedBaseAmount, pctIncrease));
	}

	protected void assertManaGained(String abilityName, Unit target, double expectedAmount) {
		var totalMana = handler.getManaGained(abilityName, target);

		assertThat(totalMana).isEqualTo((int) expectedAmount);
	}

	protected void assertManaGained(String abilityName, Unit target, double expectedBaseAmount, int pctIncrease) {
		assertManaGained(abilityName, target, increaseByPct(expectedBaseAmount, pctIncrease));
	}

	protected void assertCastTime(String abilityName, double expectedCastTime) {
		assertCastTime(abilityName, player, expectedCastTime);
	}

	protected void assertCastTime(String abilityName, Unit caster, double expectedCastTime) {
		var actualCastTime = handler.getCastTime(abilityName, caster);

		assertThat(actualCastTime).isEqualTo(expectedCastTime, PRECISION);
	}

	protected void assertCastTimeIsReducedBy(SpellInfo spellInfo, Unit caster, double reduction) {
		assertCastTime(spellInfo.name(), caster, spellInfo.baseCastTime() - reduction);
	}

	protected void assertCooldownIsReducedBy(SpellInfo spellInfo, Unit caster, double reduction) {
		var actualCooldown = handler.getCooldown(spellInfo.name(), caster);
		var expectedCooldown = spellInfo.cooldown() - reduction;

		assertThat(actualCooldown).isEqualTo(expectedCooldown);
	}

	protected void assertEffectDuration(String abilityName, Unit target, double duration) {
		var actualEffectDuration = handler.getEffectDuration(abilityName, target);

		assertThat(actualEffectDuration).isEqualTo(Duration.seconds(duration));
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
		equip(player, itemName);
	}

	protected void equip(Unit unit, String itemName) {
		var item = getItemRepository().getItem(itemName, unit.getPhaseId()).orElseThrow();
		unit.equip(new EquippableItem(item));
	}

	protected void equip(String itemName, ItemSlot itemSlot) {
		equip(player, itemName, itemSlot);
	}

	protected void equip(Unit unit, String itemName, ItemSlot itemSlot) {
		var item = getItemRepository().getItem(itemName, unit.getPhaseId()).orElseThrow();
		unit.equip(new EquippableItem(item), itemSlot);
	}

	protected void equip(int itemId, ItemSlot itemSlot) {
		equip(player, itemId, itemSlot);
	}

	protected void equip(Unit unit, int itemId, ItemSlot itemSlot) {
		var item = getItemRepository().getItem(ItemId.of(itemId), unit.getPhaseId()).orElseThrow();
		unit.equip(new EquippableItem(item), itemSlot);
	}

	protected void enableTalent(String name, int rank) {
		enableTalent(player, name, rank);
	}

	protected void enableTalent(Player player, String name, int rank) {
		player.getTalents().enable(name, rank);
		getCharacterService().updateAfterRestrictionChange(player);
	}

	protected void addSpBonus(int amount) {
		addSpBonus(player, amount);
	}

	protected void addSpBonus(Player player, int amount) {
		if (amount == 0) {
			return;
		}
		player.addHiddenEffect("Bonus Spell Power", amount);
	}

	protected void addSdBonus(int amount) {
		if (amount == 0) {
			return;
		}
		player.addHiddenEffect("Bonus Spell Damage", amount);
	}

	protected void addHealingBonus(int amount) {
		if (amount == 0) {
			return;
		}
		player.addHiddenEffect("Bonus Healing", amount);
	}

	protected void addStaminaBonus(Player player, int amount) {
		if (amount == 0) {
			return;
		}
		player.addHiddenEffect("Bonus Stamina", amount);
		player.setHealthToMax();
	}

	protected void addIntellectBonus(int amount) {
		if (amount == 0) {
			return;
		}
		player.addHiddenEffect("Bonus Intellect", amount);
		player.setManaToMax();
	}

	protected void setHealth(Unit unit, int amount) {
		unit.setCurrentHealth(amount);
	}

	protected void setHealthPct(Unit unit, int pct) {
		unit.setCurrentHealth((int) getPercentOf(pct, (double) unit.getMaxHealth()));
	}

	protected void setMana(Unit unit, int amount) {
		unit.setCurrentMana(amount);
	}

	protected void assertIsIncreasedBy(double newValue, double originalValue, double increase) {
		assertThat(newValue).isEqualTo(originalValue + increase, PRECISION);
	}

	protected void assertIsIncreasedBy(int newValue, int originalValue, int increase) {
		assertThat(newValue).isEqualTo(originalValue + increase);
	}

	protected void assertIsIncreasedByPct(int newValue, int originalValue, int pct) {
		assertThat(newValue).isEqualTo(increaseByPct(originalValue, pct));
	}

	protected void assertIsIncreasedByPct(double newValue, double originalValue, int pct) {
		assertThat(newValue).isEqualTo(increaseByPct(originalValue, pct));
	}

	protected void assertIsIncreasedByPctNonExact(int newValue, int originalValue, double pct) {
		var expected = increaseByPct(originalValue, pct);

		if (Math.abs(newValue - expected) > 2) {
			assertThat(newValue).isEqualTo(expected);
		}
	}

	protected SimulationContext simulationContext;
	protected Clock clock;
	protected Simulation simulation;
	protected Player player;
	protected Unit target;
	protected Pet pet;
	protected TestEventCollectingHandler handler;

	public record PlayerConfig(CharacterClassId characterClassId, RaceId raceId) {}

	protected PlayerConfig playerConfig = new PlayerConfig(WARLOCK, ORC);
	protected Integer level;
	protected PhaseId phaseId = TBC_P5;
	protected CreatureType enemyType = BEAST;
	protected int enemyLevelDiff = 3;

	protected TestRng rng = new TestRng();

	protected void setPlayerConfig(CharacterClassId characterClassId, RaceId raceId) {
		playerConfig = new PlayerConfig(characterClassId, raceId);
	}

	protected void createSimulation() {
		simulationContext = getSimulationContext();
		clock = simulationContext.getClock();

		simulation = new Simulation(simulationContext);
	}

	protected void createDefaultUnits() {
		player = getNakedPlayer(playerConfig, "Player");
		target = getEnemy("Target");

		player.setTarget(target);
	}

	protected void updateUntil(double time) {
		simulation.updateUntil(Time.at(time));
	}

	protected void missesOnlyOnFollowingRolls(int... critRolls) {
		rng.getHitRollData().setRolls(critRolls);
	}

	protected void critsOnlyOnFollowingRolls(int... critRolls) {
		rng.getCritRollData().setRolls(critRolls);
	}

	protected void eventsOnlyOnFollowingRolls(int... eventRolls) {
		rng.getEventRollData().setRolls(eventRolls);
	}

	protected void assertLastHitChance(double value) {
		var lastHitChance = rng.getHitRollData().getRollChances().getLast();

		assertThat(lastHitChance).isEqualTo(value, PRECISION);
	}

	protected void assertLastCritChance(double value) {
		var lastCritChance = rng.getCritRollData().getRollChances().getLast();

		assertThat(lastCritChance).isEqualTo(value, PRECISION);
	}

	protected void assertCritChanceNo(int rollChanceIdx, double value) {
		var lastCritChance = getCritChanceNo(rollChanceIdx);

		assertThat(lastCritChance).isEqualTo(value, PRECISION);
	}

	protected Double getHitChanceNo(int rollChanceIdx) {
		return rng.getHitRollData().getRollChances().get(rollChanceIdx);
	}

	protected Double getCritChanceNo(int rollChanceIdx) {
		return rng.getCritRollData().getRollChances().get(rollChanceIdx);
	}

	protected void assertLastEventChance(double value) {
		var lastEventChance = rng.getEventRollData().getRollChances().getLast();

		assertThat(lastEventChance).isEqualTo(value);
	}

	protected void assertEventChanceNo(int rollChanceIdx, double value) {
		var lastEventChance = rng.getEventRollData().getRollChances().get(rollChanceIdx);

		assertThat(lastEventChance).isEqualTo(value);
	}

	protected static final Offset<Double> PRECISION = Offset.offset(0.0001);

	protected void runAt(double time, Runnable runnable) {
		simulation.getScheduler().add(Time.at(time), runnable);
	}

	public class TestSnapshots<T> {
		private final Map<Double, T> snapshotsByTime = new TreeMap<>();

		public void makeSnapshotsAt(Supplier<T> supplier, double... times) {
			for (var time : times) {
				runAt(time, () -> snapshotsByTime.put(time, supplier.get()));
			}
		}

		public T get(double time) {
			return Objects.requireNonNull(snapshotsByTime.get(time));
		}
	}

	@RequiredArgsConstructor
	protected static class UnitTestContext {
		public final Unit unit;
		public int regeneratedHealth;
		public int regeneratedMana;
	}

	private final Map<Unit, UnitTestContext> contextMap = new HashMap<>();

	protected UnitTestContext getContext(Unit unit) {
		return contextMap.computeIfAbsent(unit, UnitTestContext::new);
	}

	protected int getRegeneratedMana(Unit unit) {
		return getContext(unit).regeneratedMana;
	}

	protected int getManaDifference(Player unit) {
		return unit.getCurrentMana() - getRegeneratedMana(unit);
	}
}