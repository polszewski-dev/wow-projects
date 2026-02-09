package wow.minmax.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wow.minmax.client.dto.simulation.SimulationStatsDTO;
import wow.minmax.converter.dto.simulation.SimulationStatsConverter;
import wow.minmax.model.CharacterId;
import wow.minmax.service.PlayerService;
import wow.minmax.service.SimulatorService;

/**
 * User: POlszewski
 * Date: 2024-11-09
 */
@RestController
@RequestMapping("api/v1/simulations")
@AllArgsConstructor
public class SimulationController {
	private final PlayerService playerService;
	private final SimulatorService simulatorService;
	private final SimulationStatsConverter simulationStatsConverter;

	@GetMapping("{characterId}")
	public SimulationStatsDTO simulate(
			@PathVariable("characterId") CharacterId characterId
	) {
		var player = playerService.getPlayer(characterId);
		var stats = simulatorService.simulate(player);

		return simulationStatsConverter.convert(stats, player.getPhaseId());
	}
}
