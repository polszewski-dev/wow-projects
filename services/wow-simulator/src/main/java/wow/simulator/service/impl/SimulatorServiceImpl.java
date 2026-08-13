package wow.simulator.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import wow.character.model.asset.AssetExecution;
import wow.character.model.character.Raid;
import wow.character.service.AssetService;
import wow.character.service.CharacterCalculationService;
import wow.character.service.CharacterService;
import wow.commons.model.Duration;
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
import wow.simulator.script.ScriptParams;
import wow.simulator.service.SimulatorService;
import wow.simulator.simulation.Simulation;
import wow.simulator.simulation.SimulationContext;
import wow.simulator.util.CountdownCounter;

import java.util.List;
import java.util.Map;

/**
 * User: POlszewski
 * Date: 2024-11-10
 */
@Service
@AllArgsConstructor
public class SimulatorServiceImpl implements SimulatorService {
	private final CharacterService characterService;
	private final CharacterCalculationService characterCalculationService;
	private final AssetService assetService;
	private final SpellRepository spellRepository;

	private static final Duration PREP_PHASE_DURATION = Duration.seconds(60);
	private static final Time PREP_PHASE_END_TIME = Time.ZERO.add(PREP_PHASE_DURATION);

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

		executeAssets(raid);

		simulation.updateFor(PREP_PHASE_DURATION.add(duration));
		simulation.finish();
	}

	private Simulation createSimulation(Raid<Player> raid, Unit target, SimulationContext simulationContext) {
		var simulation = new Simulation(simulationContext);

		simulation.add(target);

		target.whenNoActionIdleForever();

		raid.forEach(raidMember -> {
			simulation.add(raidMember);
			raidMember.setTarget(target);
		});

		return simulation;
	}

	private void executeAssets(Raid<Player> raid) {
		applyTemporaryEffects(raid);

		var executionPlan = assetService.getAssetExecutionPlan(raid);

		executeAndThen(
				executionPlan.summonsByPlayer(),
				() -> executeAndThen(
						executionPlan.buffsByPlayer(),
						() -> finalizeBuffStage(raid)
				)
		);
	}

	private void applyTemporaryEffects(Raid<Player> raid) {
		raid.forEachMemberAndPet((Unit memberOrPet) -> {
			memberOrPet.addHiddenEffect(INFINITE_RESOURCES, 1);
			memberOrPet.addHiddenEffect(INFINITE_BUFFS, 1);
		});
	}

	private void finalizeBuffStage(Raid<Player> raid) {
		raid.forEachMemberAndPet((Unit memberOrPet) -> {
			memberOrPet.removeEffect(INFINITE_RESOURCES);
			memberOrPet.removeEffect(INFINITE_BUFFS);

			memberOrPet.addHiddenEffect(BONUS_HP5, 5000);
			memberOrPet.addHiddenEffect(BONUS_MP5, 5000);

			memberOrPet.setHealthToMax();
			memberOrPet.setManaToMax();

			var mainPlayer = raid.getFirstMember();

			if (memberOrPet == mainPlayer || memberOrPet == mainPlayer.getActivePet()) {
				memberOrPet.setupScript(null);
			} else {
				memberOrPet.whenNoActionIdleForever();
			}

			memberOrPet.idleUntil(PREP_PHASE_END_TIME);
		});
	}

	private void executeAndThen(Map<Player, List<AssetExecution<Player>>> executionsByPlayer, Runnable finalAction) {
		if (executionsByPlayer.isEmpty()) {
			finalAction.run();
			return;
		}

		var counter = new CountdownCounter(executionsByPlayer.size(), finalAction);

		for (var entry : executionsByPlayer.entrySet()) {
			var player = entry.getKey();
			var params = new ScriptParams(player, null);
			var executions = entry.getValue();
			var executor = new AssetExecutor(params, executions, counter);

			executor.execute();
		}
	}

	private SimulationContext createSimulationContext(RngType rngType) {
		var clock = new Clock();
		var gameLog = new GameLog();
		var rngFactory = createRngFactory(rngType);
		var scheduler = new Scheduler(clock);

		return new SimulationContext(
				clock, gameLog, rngFactory, scheduler, characterService, characterCalculationService, spellRepository
		);
	}

	private RngFactory createRngFactory(RngType rngType) {
		return switch (rngType) {
			case REAL -> RealRng::new;
			case PREDETERMINED -> PredeterminedRng::new;
		};
	}
}
