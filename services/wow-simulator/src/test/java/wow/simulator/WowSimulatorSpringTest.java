package wow.simulator;

import lombok.Getter;
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
import wow.commons.model.character.CreatureType;
import wow.commons.model.character.RaceId;
import wow.commons.model.item.Item;
import wow.commons.model.pve.PhaseId;
import wow.commons.model.spell.AbilityId;
import wow.commons.model.spell.CooldownId;
import wow.commons.model.spell.SpellSchool;
import wow.commons.model.talent.TalentId;
import wow.commons.repository.pve.PhaseRepository;
import wow.simulator.config.SimulatorContext;
import wow.simulator.config.SimulatorContextSource;
import wow.simulator.log.GameLog;
import wow.simulator.model.action.Action;
import wow.simulator.model.time.Clock;
import wow.simulator.model.time.Time;
import wow.simulator.model.unit.NonPlayer;
import wow.simulator.model.unit.Player;
import wow.simulator.model.unit.Unit;
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
				characterClassId, raceId, level, phaseId, Player.getFactory(name)
		);

		getCharacterService().updateAfterRestrictionChange(player);

		player.setOnPendingActionQueueEmpty(x -> x.idleUntil(Time.INFINITY));
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
				enemyType, level + enemyLevelDiff, phaseId, NonPlayer.getFactory(name)
		);

		enemy.setOnPendingActionQueueEmpty(x -> x.idleUntil(Time.INFINITY));
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

	protected void assertDamageDone(AbilityId abilityId, Unit target, double expectedAmount) {
		var totalDamage = getDecreasedResourceEvents()
				.filter(x -> x.isDamage(abilityId.getName(), target))
				.mapToInt(DecreasedResource::amount)
				.sum();

		assertThat(Math.abs(totalDamage - (int) expectedAmount)).isLessThanOrEqualTo(1);
	}

	protected void assertDamageDone(AbilityId abilityId, double expectedAmount) {
		assertDamageDone(abilityId, target, expectedAmount);
	}

	protected void assertDamageDone(AbilityId abilityId, Unit target, double expectedBaseAmount, int pctIncrease) {
		assertDamageDone(abilityId, target, increaseByPct(expectedBaseAmount, pctIncrease));
	}

	protected void assertDamageDone(AbilityId abilityId, double expectedBaseAmount, int pctIncrease) {
		assertDamageDone(abilityId, target, increaseByPct(expectedBaseAmount, pctIncrease));
	}

	protected void assertDamageDone(int eventIdx, AbilityId abilityId, Unit target, double expectedAmount) {
		var totalDamage = getDecreasedResourceEvents()
				.filter(x -> x.isDamage(abilityId.getName(), target))
				.skip(eventIdx)
				.findFirst()
				.orElseThrow()
				.amount();

		assertThat(Math.abs(totalDamage - (int) expectedAmount)).isLessThanOrEqualTo(1);
	}

	protected void assertDamageDone(int eventIdx, AbilityId abilityId, double expectedAmount) {
		assertDamageDone(eventIdx, abilityId, target, expectedAmount);
	}

	protected void assertDamageDone(int eventIdx, AbilityId abilityId, double expectedBaseAmount, int pctIncrease) {
		assertDamageDone(eventIdx, abilityId, target, increaseByPct(expectedBaseAmount, pctIncrease));
	}

	private void assertHealthGained(String spellName, Unit target, double expectedAmount) {
		var totalHealthGained = getIncreasedResourceEvents()
				.filter(x -> x.isHealing(spellName, target))
				.mapToInt(IncreasedResource::amount)
				.sum();

		assertThat(totalHealthGained).isEqualTo((int) expectedAmount);
	}

	protected void assertHealthGained(AbilityId abilityId, Unit target, double expectedAmount) {
		assertHealthGained(abilityId.getName(), target, expectedAmount);
	}

	protected void assertHealthGained(TalentId talentId, Unit target, double expectedAmount) {
		assertHealthGained(talentId.getName(), target, expectedAmount);
	}

	protected void assertHealthGained(AbilityId abilityId, Unit target, double expectedBaseAmount, int pctIncrease) {
		assertHealthGained(abilityId, target, increaseByPct(expectedBaseAmount, pctIncrease));
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

	protected void assertHealthGained(int eventIdx, AbilityId abilityId, Unit target, double expectedAmount) {
		assertHealthGained(eventIdx, abilityId.getName(), target, expectedAmount);
	}

	protected void assertManaPaid(AbilityId abilityId, Unit target, double expectedAmount) {
		var totalManaPaid = getDecreasedResourceEvents()
				.filter(x -> x.isManaPaid(abilityId.getName(), target))
				.mapToInt(DecreasedResource::amount)
				.sum();

		assertThat(totalManaPaid).isEqualTo((int) expectedAmount);
	}

	protected void assertManaPaid(AbilityId abilityId, Unit target, double expectedBaseAmount, int pctIncrease) {
		assertManaPaid(abilityId, target, increaseByPct(expectedBaseAmount, pctIncrease));
	}

	protected void assertManaGained(AbilityId abilityId, Unit target, double expectedAmount) {
		var totalMana = getIncreasedResourceEvents()
				.filter(x -> x.isManaGain(abilityId.getName(), target))
				.mapToInt(IncreasedResource::amount)
				.sum();

		assertThat(totalMana).isEqualTo((int) expectedAmount);
	}

	protected void assertManaGained(AbilityId abilityId, Unit target, double expectedBaseAmount, int pctIncrease) {
		assertManaGained(abilityId, target, increaseByPct(expectedBaseAmount, pctIncrease));
	}

	protected void assertCastTime(AbilityId abilityId, double expectedCastTime) {
		var actualCastTime = getBeginCastEvents()
				.filter(x -> x.spell() == abilityId)
				.findFirst()
				.orElseThrow()
				.castTime();

		assertThat(actualCastTime).isEqualTo(Duration.seconds(expectedCastTime));
	}

	protected void assertCastTime(AbilityId abilityId, double expectedBaseCastTime, int pctIncrease) {
		assertCastTime(abilityId, increaseByPct(expectedBaseCastTime, pctIncrease));
	}

	protected void assertCooldown(AbilityId abilityId, double duration) {
		var actualCooldown = getCooldownStartedEvents()
				.filter(x -> x.cooldownId().equals(CooldownId.of(abilityId)))
				.findFirst()
				.orElseThrow()
				.duration();

		assertThat(actualCooldown).isEqualTo(Duration.seconds(duration));
	}

	protected void assertEffectDuration(AbilityId abilityId, Unit target, double duration) {
		var actualEffectDuration = getEffectAppliedEvents()
				.filter(x -> x.spell() == abilityId)
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

	protected void assertEnablingTalentTeachesAbility(TalentId talentId, AbilityId abilityId) {
		assertThat(player.getAbility(abilityId)).isEmpty();
		enableTalent(talentId, 1);
		assertThat(player.getAbility(abilityId)).isPresent();
	}

	protected void assertIsIncreasedByPct(int newValue, int originalValue, int pct) {
		assertThat(newValue).isEqualTo(increaseByPct(originalValue, pct));
	}

	protected static int increaseByPct(int originalValue, int pct) {
		return (int) (originalValue * (100 + pct) / 100.0);
	}

	protected static double increaseByPct(double originalValue, int pct) {
		return originalValue * (100 + pct) / 100.0;
	}

	protected static int getPercentOf(int pct, int value) {
		return (value * pct) / 100;
	}

	protected static double getPercentOf(double pct, double value) {
		return (value * pct) / 100;
	}

	public record SpellInfo(Direct direct, Periodic periodic, int manaCost, double baseCastTime) {
		public record Direct(int min, int max, double coeff) {
			public double damage(double coeffBonus, int sp) {
				return getSpellDmg(min, max, coeff + coeffBonus, sp);
			}
		}

		public record Periodic(int value, double coeff, double duration, int numTicks) {
			public double damage(double coeffBonus, int sp) {
				return getSpellDmg(value, value, coeff + coeffBonus, sp);
			}

			public double tickDamage() {
				return value / (double) numTicks;
			}
		}

		public SpellInfo(int manaCost, double baseCastTime) {
			this(null, null, manaCost, baseCastTime);
		}

		public SpellInfo withDirect(int min, int max, double coeff) {
			return new SpellInfo(new Direct(min, max, coeff), periodic, manaCost, baseCastTime);
		}

		public SpellInfo withPeriodic(int value, double coeff, double duration, int numTicks) {
			return new SpellInfo(direct, new Periodic(value, coeff, duration, numTicks), manaCost, baseCastTime);
		}

		public double damage() {
			return damage(0, 0);
		}

		public double damage(int sp) {
			return damage(0, sp);
		}

		public double damage(double coeffBonus, int sp) {
			return (direct != null ? direct.damage(coeffBonus, sp) : 0) + (periodic != null ? periodic.damage(coeffBonus, sp) : 0);
		}

		public double directDamage() {
			return direct.damage(0, 0);
		}

		public double tickDamage() {
			return periodic.tickDamage();
		}

		public int numTicks() {
			return periodic().numTicks();
		}

		public double duration() {
			return periodic.duration();
		}

		private static double getSpellDmg(int min, int max, double coeff, int sd) {
			return (min + max) / 2.0 + getPercentOf(coeff, sd);
		}
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