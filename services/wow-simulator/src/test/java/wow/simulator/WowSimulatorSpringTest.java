package wow.simulator;

import lombok.Getter;
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
import wow.commons.model.config.Description;
import wow.commons.model.effect.EffectId;
import wow.commons.model.effect.EffectSource;
import wow.commons.model.item.Item;
import wow.commons.model.item.ItemId;
import wow.commons.model.pve.PhaseId;
import wow.commons.model.spell.AbilityId;
import wow.commons.model.spell.CooldownId;
import wow.commons.model.spell.EffectReplacementMode;
import wow.commons.model.spell.SpellSchool;
import wow.commons.repository.pve.PhaseRepository;
import wow.commons.repository.spell.SpellRepository;
import wow.simulator.config.SimulatorContext;
import wow.simulator.config.SimulatorContextSource;
import wow.simulator.log.GameLog;
import wow.simulator.model.action.Action;
import wow.simulator.model.effect.impl.NonPeriodicEffectInstance;
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
import wow.simulator.util.TestEvent;
import wow.simulator.util.TestEventCollectingHandler;
import wow.simulator.util.TestEventListBuilder;
import wow.simulator.util.TestRng;

import java.util.List;
import java.util.function.IntConsumer;
import java.util.function.Predicate;
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
		return new SimulationContext(clock, gameLog, () -> rng, scheduler, getCharacterCalculationService());
	}

	protected Player getNakedPlayer() {
		return getNakedPlayer(characterClassId, "Player");
	}

	protected Player getNakedPlayer(CharacterClassId characterClassId, String name) {
		var raceId = getRaceId(characterClassId);
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
		applyDummyEffect(-20001, amount);
	}

	private void applyDummyEffect(int effectId, int numStacks) {
		var effect = spellRepository.getEffect(EffectId.of(effectId), player.getPhaseId()).orElseThrow();
		var effectInstance = new NonPeriodicEffectInstance(
				player,
				player,
				effect,
				Duration.INFINITE,
				numStacks,
				1,
				new DummyTestSource(),
				null,
				null
		);

		player.addEffect(effectInstance, EffectReplacementMode.DEFAULT);
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
		assertThat(rng.getHitRollData().getRollChances().getLast()).isEqualTo(value);
	}

	protected void assertLastCritChance(double value) {
		assertThat(rng.getCritRollData().getRollChances().getLast()).isEqualTo(value);
	}

	protected void assertLastEventChance(double value) {
		assertThat(rng.getEventRollData().getRollChances().getLast()).isEqualTo(value);
	}

	protected void assertEventChanceNo(int rollChanceIdx, double value) {
		assertThat(rng.getEventRollData().getRollChances().get(rollChanceIdx)).isEqualTo(value);
	}
}