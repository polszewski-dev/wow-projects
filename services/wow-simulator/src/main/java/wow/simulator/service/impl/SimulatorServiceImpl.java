package wow.simulator.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import wow.character.service.CharacterCalculationService;
import wow.commons.model.Duration;
import wow.commons.repository.spell.SpellRepository;
import wow.simulator.client.dto.RngType;
import wow.simulator.log.GameLog;
import wow.simulator.log.handler.ConsoleGameLogHandler;
import wow.simulator.log.handler.StatisticsGatheringHandler;
import wow.simulator.model.rng.PredeterminedRng;
import wow.simulator.model.rng.RealRng;
import wow.simulator.model.rng.RngFactory;
import wow.simulator.model.stats.Stats;
import wow.simulator.model.time.Clock;
import wow.simulator.model.time.Time;
import wow.simulator.model.unit.Player;
import wow.simulator.model.update.Scheduler;
import wow.simulator.script.ScriptExecutor;
import wow.simulator.service.SimulatorService;
import wow.simulator.simulation.Simulation;
import wow.simulator.simulation.SimulationContext;

/**
 * User: POlszewski
 * Date: 2024-11-10
 */
@Service
@AllArgsConstructor
public class SimulatorServiceImpl implements SimulatorService {
	private final CharacterCalculationService characterCalculationService;
	private final SpellRepository spellRepository;

	@Override
	public Stats simulate(Player player, Duration duration, RngType rngType, Runnable withinSimulationContext) {
		var target = player.getTarget();
		var scriptExecutor = new ScriptExecutor(player);
		var simulation = createSimulation(rngType);
		var endTime = Time.at(duration.getSeconds());

		scriptExecutor.setupPlayer();

		target.whenNoActionIdleForever();

		var stats = new Stats();

		simulation.addHandler(new ConsoleGameLogHandler());
		simulation.addHandler(new StatisticsGatheringHandler(player, stats));

		simulation.add(player);
		simulation.add(target);

		withinSimulationContext.run();

		player.addHiddenEffect("Bonus Hp5", 5000);
		player.addHiddenEffect("Bonus Mp5", 5000);

		simulation.updateUntil(endTime);
		simulation.finish();

		return stats;
	}

	private Simulation createSimulation(RngType rngType) {
		var clock = new Clock();
		var gameLog = new GameLog();
		var rngFactory = createRngFactory(rngType);
		var scheduler = new Scheduler(clock);
		var simulationContext = new SimulationContext(
				clock, gameLog, rngFactory, scheduler, characterCalculationService, spellRepository
		);

		return new Simulation(simulationContext);
	}

	private RngFactory createRngFactory(RngType rngType) {
		return switch (rngType) {
			case REAL -> RealRng::new;
			case PREDETERMINED -> PredeterminedRng::new;
		};
	}
}
