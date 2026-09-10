package wow.simulator.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wow.character.service.CharacterCalculationService;
import wow.character.service.CharacterService;
import wow.commons.model.Duration;
import wow.commons.repository.spell.SpellRepository;
import wow.simulator.client.dto.RngType;
import wow.simulator.client.dto.SimulationRequestDTO;
import wow.simulator.client.dto.SimulationResponseDTO;
import wow.simulator.converter.NonPlayerConverter;
import wow.simulator.converter.RaidConverter;
import wow.simulator.converter.StatsConverter;
import wow.simulator.log.GameLog;
import wow.simulator.log.handler.ConsoleGameLogHandler;
import wow.simulator.log.handler.GameLogHandler;
import wow.simulator.log.handler.StatisticsGatheringHandler;
import wow.simulator.model.rng.PredeterminedRng;
import wow.simulator.model.rng.RealRng;
import wow.simulator.model.rng.RngFactory;
import wow.simulator.model.stats.Stats;
import wow.simulator.model.time.Clock;
import wow.simulator.model.update.Scheduler;
import wow.simulator.scenario.InfiniteTargetHealthScenario;
import wow.simulator.service.SimulatorService;
import wow.simulator.simulation.SimulationContext;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2024-11-09
 */
@RestController
@RequestMapping("api/v1/simulations")
@AllArgsConstructor
@Slf4j
public class SimulationController {
	private final SimulatorService simulatorService;

	private final CharacterService characterService;
	private final CharacterCalculationService characterCalculationService;
	private final SpellRepository spellRepository;

	private final RaidConverter raidConverter;
	private final NonPlayerConverter nonPlayerConverter;
	private final StatsConverter statsConverter;

	@PostMapping
	public SimulationResponseDTO simulate(@RequestBody SimulationRequestDTO request) {
		var raid = raidConverter.convertBack(request.raid());
		var player = raid.getFirstMember();
		var target = nonPlayerConverter.convertBack(request.target());
		var duration = Duration.seconds(request.duration());
		var rngType = request.rngType();
		var stats = new Stats();
		var handlers = List.<GameLogHandler>of(
				new ConsoleGameLogHandler(),
				new StatisticsGatheringHandler(player, stats)
		);

		long start = System.currentTimeMillis();

		var scenario = new InfiniteTargetHealthScenario(duration, () -> createSimulationContext(rngType), simulatorService);

		scenario.execute(raid, target, handlers);

		long end = System.currentTimeMillis();

		log.info("Simulation ended after {} seconds", (end - start) / 1000.0);

		return new SimulationResponseDTO(
				statsConverter.convert(stats, player)
		);
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
