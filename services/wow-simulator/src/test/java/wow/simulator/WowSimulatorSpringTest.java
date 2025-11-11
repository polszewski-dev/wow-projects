package wow.simulator;

import lombok.Getter;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import wow.character.model.equipment.EquippableItem;
import wow.character.model.snapshot.StatSummary;
import wow.commons.model.Duration;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.character.CharacterClassId;
import wow.commons.model.character.CreatureType;
import wow.commons.model.character.RaceId;
import wow.commons.model.config.Description;
import wow.commons.model.effect.EffectSource;
import wow.commons.model.item.Item;
import wow.commons.model.item.ItemId;
import wow.commons.model.pve.PhaseId;
import wow.commons.model.spell.AbilityId;
import wow.commons.model.spell.CooldownId;
import wow.commons.model.spell.SpellSchool;
import wow.commons.repository.pve.PhaseRepository;
import wow.commons.repository.spell.SpellRepository;
import wow.simulator.config.SimulatorContext;
import wow.simulator.config.SimulatorContextSource;
import wow.simulator.log.GameLog;
import wow.simulator.model.action.Action;
import wow.simulator.model.time.Clock;
import wow.simulator.model.time.Time;
import wow.simulator.model.unit.NonPlayer;
import wow.simulator.model.unit.Player;
import wow.simulator.model.unit.Unit;
import wow.simulator.model.unit.impl.NonPlayerImpl;
import wow.simulator.model.unit.impl.PlayerImpl;
import wow.simulator.model.update.Scheduler;
import wow.simulator.simulation.Simulation;
import wow.simulator.simulation.SimulationContext;
import wow.simulator.util.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.function.IntConsumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.character.CharacterClassId.WARLOCK;
import static wow.commons.model.character.CreatureType.BEAST;
import static wow.commons.model.character.RaceId.ORC;
import static wow.commons.model.character.RaceId.UNDEAD;
import static wow.commons.model.pve.PhaseId.TBC_P5;
import static wow.simulator.model.time.Time.TIME_IN_INFINITY;
import static wow.simulator.util.CalcUtils.increaseByPct;
import static wow.simulator.util.TestEvent.*;

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
		return new SimulationContext(clock, gameLog, () -> rng, scheduler, getCharacterCalculationService(), getSpellRepository());
	}

	protected Player getNakedPlayer() {
		return getNakedPlayer(characterClassId, "Player");
	}

	protected Player getNakedPlayer(CharacterClassId characterClassId, String name) {
		var raceId = getRaceId(characterClassId);
		return getNakedPlayer(characterClassId, raceId, name);
	}

	protected PlayerImpl getNakedPlayer(CharacterClassId characterClassId, RaceId raceId, String name) {
		int level = getLevel();

		var player = getCharacterService().createPlayerCharacter(
				name, characterClassId, raceId, level, phaseId, PlayerImpl::new
		);

		getCharacterService().updateAfterRestrictionChange(player);

		player.setOnPendingActionQueueEmpty(x -> x.idleUntil(TIME_IN_INFINITY));
		simulationContext.shareSimulationContext(player);

		return player;
	}

	private RaceId getRaceId(CharacterClassId characterClassId) {
		if (raceId != null) {
			return raceId;
		}
		return switch (characterClassId) {
			case PRIEST -> UNDEAD;
			case WARLOCK -> ORC;
			default -> throw new IllegalArgumentException();
		};
	}

	private int getLevel() {
		if (level != null) {
			return level;
		}
		return phaseRepository.getPhase(phaseId).orElseThrow().getMaxLevel();
	}

	protected NonPlayer getEnemy() {
		return getEnemy("Target");
	}

	protected NonPlayer getEnemy(String name) {
		int level = getLevel();

		var enemy = getCharacterService().createNonPlayerCharacter(
				name, enemyType, level + enemyLevelDiff, phaseId, NonPlayerImpl::new
		);

		enemy.setOnPendingActionQueueEmpty(x -> x.idleUntil(TIME_IN_INFINITY));
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

	private Stream<DecreasedResource> getDecreasedResourceEvents() {
		return handler.getEvents().stream()
				.filter(x -> x instanceof DecreasedResource)
				.map(x -> (DecreasedResource) x);
	}

	private Stream<IncreasedResource> getIncreasedResourceEvents() {
		return handler.getEvents().stream()
				.filter(x -> x instanceof IncreasedResource)
				.map(x -> (IncreasedResource) x);
	}

	private Stream<BeginCast> getBeginCastEvents() {
		return handler.getEvents().stream()
				.filter(x -> x instanceof BeginCast)
				.map(x -> (BeginCast) x);
	}

	private Stream<CooldownStarted> getCooldownStartedEvents() {
		return handler.getEvents().stream()
				.filter(x -> x instanceof CooldownStarted)
				.map(x -> (CooldownStarted) x);
	}

	private Stream<EffectApplied> getEffectAppliedEvents() {
		return handler.getEvents().stream()
				.filter(x -> x instanceof EffectApplied)
				.map(x -> (EffectApplied) x);
	}

	protected void assertDamageDone(String abilityName, Unit target, double expectedAmount) {
		var totalDamage = getDecreasedResourceEvents()
				.filter(x -> x.isDamage(abilityName, target))
				.mapToInt(DecreasedResource::amount)
				.sum();

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
		var totalDamage = getDecreasedResourceEvents()
				.filter(x -> x.isDamage(abilityName, target))
				.skip(eventIdx)
				.findFirst()
				.orElseThrow()
				.amount();

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
		assertDamageDone(spellInfo.name(), target, increaseByPct(spellInfo.damage(sp), pctIncrease));
	}

	protected void assertDamageDone(SpellInfo spellInfo, int sp) {
		assertDamageDone(spellInfo, target, sp);
	}

	protected void assertHealthGained(String spellName, Unit target, double expectedAmount) {//
		var totalHealthGained = getIncreasedResourceEvents()
				.filter(x -> x.isHealing(spellName, target))
				.mapToInt(IncreasedResource::amount)
				.sum();

		assertThat(totalHealthGained).isEqualTo((int) expectedAmount);
	}

	protected void assertHealthGained(String abilityName, Unit target, double expectedBaseAmount, int pctIncrease) {
		assertHealthGained(abilityName, target, increaseByPct(expectedBaseAmount, pctIncrease));
	}

	protected void assertHealthGained(int eventIdx, String spellName, Unit target, double expectedAmount) {
		var totalHealthGained = getIncreasedResourceEvents()
				.filter(x -> x.isHealing(spellName, target))
				.skip(eventIdx)
				.findFirst()
				.orElseThrow()
				.amount();

		assertThat(totalHealthGained).isEqualTo((int) expectedAmount);
	}

	protected void assertHealthGained(SpellInfo spellInfo, Unit target, int sp) {
		assertHealthGained(spellInfo.name(), target, spellInfo.damage(sp));
	}

	protected void assertManaPaid(String abilityName, Unit target, double expectedAmount) {
		var totalManaPaid = getDecreasedResourceEvents()
				.filter(x -> x.isManaPaid(abilityName, target))
				.mapToInt(DecreasedResource::amount)
				.sum();

		assertThat(totalManaPaid).isEqualTo((int) expectedAmount);
	}

	protected void assertManaPaid(String abilityName, Unit target, double expectedBaseAmount, int pctIncrease) {
		assertManaPaid(abilityName, target, increaseByPct(expectedBaseAmount, pctIncrease));
	}

	protected void assertManaGained(String abilityName, Unit target, double expectedAmount) {
		var totalMana = getIncreasedResourceEvents()
				.filter(x -> x.isManaGain(abilityName, target))
				.mapToInt(IncreasedResource::amount)
				.sum();

		assertThat(totalMana).isEqualTo((int) expectedAmount);
	}

	protected void assertManaGained(String abilityName, Unit target, double expectedBaseAmount, int pctIncrease) {
		assertManaGained(abilityName, target, increaseByPct(expectedBaseAmount, pctIncrease));
	}

	protected void assertCastTime(String abilityName, double expectedCastTime) {
		var actualCastTime = getBeginCastEvents()
				.filter(x -> x.spell().equals(abilityName))
				.findFirst()
				.orElseThrow()
				.castTime();

		assertThat(actualCastTime).isEqualTo(Duration.seconds(expectedCastTime));
	}

	protected void assertCastTime(String abilityName, double expectedBaseCastTime, int pctIncrease) {
		assertCastTime(abilityName, increaseByPct(expectedBaseCastTime, pctIncrease));
	}

	protected void assertCooldown(String abilityName, double duration) {
		var abilityId = AbilityId.parse(abilityName);
		var actualCooldown = getCooldownStartedEvents()
				.filter(x -> x.cooldownId().equals(CooldownId.of(abilityId)))
				.findFirst()
				.orElseThrow()
				.duration();

		assertThat(actualCooldown).isEqualTo(Duration.seconds(duration));
	}

	protected void assertEffectDuration(String abilityName, Unit target, double duration) {
		var actualEffectDuration = getEffectAppliedEvents()
				.filter(x -> x.name().equals(abilityName))
				.filter(x -> x.target() == target)
				.findFirst()
				.orElseThrow()
				.duration();

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
		Item item = getItemRepository().getItem(itemName, player.getPhaseId()).orElseThrow();
		player.equip(new EquippableItem(item));
	}

	protected void equip(String itemName, ItemSlot itemSlot) {
		Item item = getItemRepository().getItem(itemName, player.getPhaseId()).orElseThrow();
		player.equip(new EquippableItem(item), itemSlot);
	}

	protected void equip(int itemId, ItemSlot itemSlot) {
		Item item = getItemRepository().getItem(ItemId.of(itemId), player.getPhaseId()).orElseThrow();
		player.equip(new EquippableItem(item), itemSlot);
	}

	protected void enableTalent(String name, int rank) {
		player.getTalents().enable(name, rank);
		getCharacterService().updateAfterRestrictionChange(player);
	}

	protected void enableBuff(String name, int rank) {
		player.getBuffs().enable(name, rank);
	}

	protected void addSpBonus(int amount) {
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

	protected void addIntellectBonus(int amount) {
		if (amount == 0) {
			return;
		}
		player.addHiddenEffect("Bonus Intellect", amount);
		player.setManaToMax();
	}

	public record DummyTestSource() implements EffectSource {
		@Override
		public int getPriority() {
			return 0;
		}

		@Override
		public Description getDescription() {
			return new Description("Test", null, null);
		}
	}

	protected void setHealth(Unit unit, int amount) {
		unit.setCurrentHealth(amount);
	}

	protected void setMana(Unit unit, int amount) {
		unit.setCurrentMana(amount);
	}

	protected void assertEnablingTalentTeachesAbility(String talentName, String abilityName) {
		assertThat(player.getAbility(abilityName)).isEmpty();
		enableTalent(talentName, 1);
		assertThat(player.getAbility(abilityName)).isPresent();
	}

	protected void assertIsIncreasedByPct(int newValue, int originalValue, int pct) {
		assertThat(newValue).isEqualTo(increaseByPct(originalValue, pct));
	}

	protected SimulationContext simulationContext;
	protected Clock clock;
	protected Simulation simulation;
	protected Player player;
	protected Unit target;
	protected TestEventCollectingHandler handler;

	protected CharacterClassId characterClassId = WARLOCK;
	protected RaceId raceId;
	protected Integer level;
	protected PhaseId phaseId = TBC_P5;
	protected CreatureType enemyType = BEAST;
	protected int enemyLevelDiff = 3;

	protected int totalSpellDamage;
	protected int totalShadowSpellDamage;
	protected int totalFireSpellDamage;

	protected TestRng rng = new TestRng();

	protected void setupTestObjects() {
		simulationContext = getSimulationContext();
		clock = simulationContext.getClock();

		simulation = new Simulation(simulationContext);

		player = getNakedPlayer();
		target = getEnemy();

		player.setTarget(target);

		makeSnapshotsUntil(180);
	}

	protected void updateUntil(double time) {
		this.totalSpellDamage = player.getStats().getSpellDamage();
		this.totalShadowSpellDamage = player.getStats().getSpellDamage(SpellSchool.SHADOW);
		this.totalFireSpellDamage = player.getStats().getSpellDamage(SpellSchool.FIRE);

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

	protected void assertHitChanceNo(int rollChanceIdx, double value) {
		var lastHitChance = rng.getHitRollData().getRollChances().get(rollChanceIdx);

		assertThat(lastHitChance).isEqualTo(value, PRECISION);
	}

	protected void assertLastCritChance(double value) {
		var lastCritChance = rng.getCritRollData().getRollChances().getLast();

		assertThat(lastCritChance).isEqualTo(value, PRECISION);
	}

	protected void assertCritChanceNo(int rollChanceIdx, double value) {
		var lastCritChance = rng.getCritRollData().getRollChances().get(rollChanceIdx);

		assertThat(lastCritChance).isEqualTo(value, PRECISION);
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

	private final TestSnapshots<StatSummary> statSnapshots = new TestSnapshots<>();

	protected void snapshotStatsAt(double... times) {
		statSnapshots.makeSnapshotsAt(() -> player.getStats(), times);
	}

	protected StatSummary statsAt(double time) {
		return statSnapshots.get(time);
	}

	protected double timeBefore = 0;
	protected double timeAfter = 5;

	protected void assertStaminaIncreasedBy(int amount) {
		var staminaBefore = statsAt(timeBefore).getStamina();
		var staminaAfter = statsAt(timeAfter).getStamina();

		assertThat(staminaAfter).isEqualTo(staminaBefore + amount);
	}

	protected void assertStaminaIncreasedByPct(int pctIncrease) {
		var staminaBefore = statsAt(timeBefore).getStamina();
		var staminaAfter = statsAt(timeAfter).getStamina();

		assertThat(staminaAfter).isEqualTo(increaseByPct(staminaBefore, pctIncrease));
	}

	protected void assertIntellectIncreasedBy(int amount) {
		var intellectBefore = statsAt(timeBefore).getIntellect();
		var intellectAfter = statsAt(timeAfter).getIntellect();

		assertThat(intellectAfter).isEqualTo(intellectBefore + amount);
	}

	protected void assertIntellectIncreasedByPct(int pctIncrease) {
		var intellectBefore = statsAt(timeBefore).getIntellect();
		var intellectAfter = statsAt(timeAfter).getIntellect();

		assertThat(intellectAfter).isEqualTo(increaseByPct(intellectBefore, pctIncrease));
	}

	protected void assertSpiritIncreasedBy(int amount) {
		var spiritBefore = statsAt(timeBefore).getSpirit();
		var spiritAfter = statsAt(timeAfter).getSpirit();

		assertThat(spiritAfter).isEqualTo(spiritBefore + amount);
	}

	protected void assertSpiritIncreasedByPct(int pctIncrease) {
		var spiritBefore = statsAt(timeBefore).getSpirit();
		var spiritAfter = statsAt(timeAfter).getSpirit();

		assertThat(spiritAfter).isEqualTo(increaseByPct(spiritBefore, pctIncrease));
	}

	protected void assertBaseStatsIncreasedBy(int amount) {
		assertStaminaIncreasedBy(amount);
		assertIntellectIncreasedBy(amount);
		assertSpiritIncreasedBy(amount);
	}

	protected void assertBaseStatsIncreasedByPct(int pctIncrease) {
		assertStaminaIncreasedByPct(pctIncrease);
		assertIntellectIncreasedByPct(pctIncrease);
		assertSpiritIncreasedByPct(pctIncrease);
	}

	protected void assertSpellPowerIncreasedBy(int amount) {
		var spBefore = statsAt(timeBefore).getSpellPower();
		var spAfter = statsAt(timeAfter).getSpellPower();

		assertThat(spAfter).isEqualTo(spBefore + amount);
	}

	protected void assertSpellHastePctIncreasedBy(int amount) {
		var hasteBefore = statsAt(timeBefore).getSpellHastePct();
		var hasteAfter = statsAt(timeAfter).getSpellHastePct();

		assertThat(hasteAfter).isEqualTo(hasteBefore + amount);
	}

	protected void assertMp5IncreasedBy(int amount) {
		var mp5Before = statsAt(timeBefore).getInterruptedManaRegen();
		var mp5After = statsAt(timeAfter).getInterruptedManaRegen();

		assertThat(mp5After).isEqualTo(mp5Before + amount);
	}

	protected void makeSnapshotsUntil(double timeUntil) {
		for (var time = 0; time <= timeUntil; ++time) {
			snapshotStatsAt(time);
		}
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
}