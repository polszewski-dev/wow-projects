package wow.simulator.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import wow.character.model.asset.AssetExecution;
import wow.character.model.character.Raid;
import wow.character.service.AssetService;
import wow.character.service.CharacterCalculationService;
import wow.commons.model.Duration;
import wow.commons.model.spell.AbilityId;
import wow.commons.repository.spell.SpellRepository;
import wow.simulator.client.dto.RngType;
import wow.simulator.log.GameLog;
import wow.simulator.log.handler.GameLogHandler;
import wow.simulator.model.rng.PredeterminedRng;
import wow.simulator.model.rng.RealRng;
import wow.simulator.model.rng.RngFactory;
import wow.simulator.model.time.Clock;
import wow.simulator.model.time.Time;
import wow.simulator.model.unit.Player;
import wow.simulator.model.unit.Unit;
import wow.simulator.model.update.Scheduler;
import wow.simulator.script.ScriptExecutor;
import wow.simulator.script.SinglePassScriptExecutor;
import wow.simulator.service.SimulatorService;
import wow.simulator.simulation.Simulation;
import wow.simulator.simulation.SimulationContext;

import java.util.List;

import static wow.character.model.asset.Asset.*;
import static wow.character.model.script.ScriptSectionType.PREPARATION;

/**
 * User: POlszewski
 * Date: 2024-11-10
 */
@Service
@AllArgsConstructor
public class SimulatorServiceImpl implements SimulatorService {
	private final CharacterCalculationService characterCalculationService;
	private final AssetService assetService;
	private final SpellRepository spellRepository;

	private static final Duration PREP_PHASE_DURATION = Duration.seconds(60);

	private static final String BONUS_HP5 = "Bonus Hp5";
	private static final String BONUS_MP5 = "Bonus Mp5";
	private static final String INFINITE_RESOURCES = "Infinite Resources";
	private static final String INFINITE_BUFFS = "Infinite Buffs";

	@Override
	public void simulate(Raid<Player> raid, Unit target, Duration duration, RngType rngType, List<GameLogHandler> handlers) {
		var simulationContext = createSimulationContext(rngType);

		simulate(raid, target, duration, simulationContext, handlers);
	}

	@Override
	public void simulate(Raid<Player> raid, Unit target, Duration duration, SimulationContext simulationContext, List<GameLogHandler> handlers) {
		var simulation = createSimulation(raid, target, simulationContext);

		simulation.addHandlers(handlers);

		buffRaid(raid, target);

		simulation.updateFor(PREP_PHASE_DURATION.add(duration));
		simulation.finish();
	}

	private Simulation createSimulation(Raid<Player> raid, Unit target, SimulationContext simulationContext) {
		var mainPlayer = raid.getFirstMember();
		var scriptExecutor = new ScriptExecutor(mainPlayer, mainPlayer);
		var simulation = new Simulation(simulationContext);

		scriptExecutor.setupPlayer();

		target.whenNoActionIdleForever();

		simulation.add(target);

		raid.forEach(raidMember -> {
			raidMember.setTarget(target);
			simulation.add(raidMember);
			raidMember.addHiddenEffect(BONUS_HP5, 5000);
			raidMember.addHiddenEffect(BONUS_MP5, 5000);
		});

		return simulation;
	}

	private void buffRaid(Raid<Player> raid, Unit targetEnemy) {
		applyHiddenEffects(raid);

		var executionPlan = assetService.getAssetExecutionPlan(raid);

		for (var command : executionPlan) {
			executeBuffCommand(command, targetEnemy);
		}

		idleUntilPrepEnd(raid);
	}

	private void applyHiddenEffects(Raid<Player> raid) {
		var prepPhaseBuffDuration = PREP_PHASE_DURATION.subtract(Duration.millis(1));

		raid.forEach(raidMember -> {
			raidMember.addHiddenEffect(INFINITE_RESOURCES, 1, prepPhaseBuffDuration);
			raidMember.addHiddenEffect(INFINITE_BUFFS, 1, prepPhaseBuffDuration);
		});
	}

	private void idleUntilPrepEnd(Raid<Player> raid) {
		var prepPhaseEndTime = Time.ZERO.add(PREP_PHASE_DURATION);

		raid.forEach(raidMember -> {
			raidMember.idleUntil(prepPhaseEndTime);
			raidMember.immediateAction(this::cleanUp);
		});
	}

	private void executeBuffCommand(AssetExecution<Player> command, Unit targetEnemy) {
		var player = command.player();
		var asset = command.asset();

		switch (asset.buffCommand()) {
			case CastAbility(var target, var abilityId) ->
					execCastAbility(player, abilityId, target, targetEnemy);

			case ExecuteScript(var target, var scriptName) ->
					executeScript(player, scriptName, target);
		}
	}

	private void execCastAbility(Player player, AbilityId abilityId, BuffTarget target, Unit targetEnemy) {
		switch (target) {
			case EACH_RAID_MEMBER ->
					player.getRaid().forEach(member -> player.cast(abilityId, member));

			case EACH_PARTY_FIRST_MEMBER ->
					player.getRaid().getParties().forEach(party -> {
						var firstMember = party.getFirstMember();

						if (firstMember != null) {
							player.cast(abilityId, firstMember);
						}
					});

			case SELF ->
					player.cast(abilityId, player);

			case TARGET_ENEMY ->
					player.cast(abilityId, targetEnemy);
		}
	}

	private void executeScript(Player player, String scriptName, BuffTarget target) {
		if (target != BuffTarget.SELF) {
			throw new IllegalArgumentException();
		}

		var scriptExecutor = new SinglePassScriptExecutor(scriptName, PREPARATION, player, player);

		scriptExecutor.setupPlayer();

		scriptExecutor.execute();
	}

	private void cleanUp(Unit unit) {
		unit.setHealthToMax();
		unit.setManaToMax();
	}

	private SimulationContext createSimulationContext(RngType rngType) {
		var clock = new Clock();
		var gameLog = new GameLog();
		var rngFactory = createRngFactory(rngType);
		var scheduler = new Scheduler(clock);

		return new SimulationContext(
				clock, gameLog, rngFactory, scheduler, characterCalculationService, spellRepository
		);
	}

	private RngFactory createRngFactory(RngType rngType) {
		return switch (rngType) {
			case REAL -> RealRng::new;
			case PREDETERMINED -> PredeterminedRng::new;
		};
	}
}
