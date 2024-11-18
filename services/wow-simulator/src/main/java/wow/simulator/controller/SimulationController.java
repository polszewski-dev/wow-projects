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
import wow.simulator.converter.PlayerConverter;
import wow.simulator.converter.StatsConverter;
import wow.simulator.service.SimulatorService;

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

	private final PlayerConverter playerConverter;
	private final StatsConverter statsConverter;

	@PostMapping
	public SimulationResponseDTO simulate(@RequestBody SimulationRequestDTO request) {
		var player = playerConverter.convert(request);
		var duration = Duration.seconds(request.duration());
		var rngType = request.rngType();

		long start = System.currentTimeMillis();

		var stats = simulatorService.simulate(player, duration, rngType);

		long end = System.currentTimeMillis();

		log.info("Simulation ended after {} seconds", (end - start) / 1000.0);

		return new SimulationResponseDTO(
				statsConverter.convert(stats)
		);
	}
}
