package wow.simulator.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wow.commons.model.Duration;
import wow.simulator.client.dto.SimulationRequestDTO;
import wow.simulator.client.dto.SimulationResponseDTO;
import wow.simulator.converter.NonPlayerConverter;
import wow.simulator.converter.RaidConverter;
import wow.simulator.converter.StatsConverter;
import wow.simulator.log.handler.ConsoleGameLogHandler;
import wow.simulator.log.handler.GameLogHandler;
import wow.simulator.log.handler.StatisticsGatheringHandler;
import wow.simulator.model.stats.Stats;
import wow.simulator.service.SimulatorService;

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

		simulatorService.simulate(raid, target, duration, rngType, handlers);

		long end = System.currentTimeMillis();

		log.info("Simulation ended after {} seconds", (end - start) / 1000.0);

		return new SimulationResponseDTO(
				statsConverter.convert(stats, player)
		);
	}
}
