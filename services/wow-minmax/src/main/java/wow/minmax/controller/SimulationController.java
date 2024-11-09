package wow.minmax.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wow.minmax.service.SimulatorService;

/**
 * User: POlszewski
 * Date: 2024-11-09
 */
@RestController
@RequestMapping("api/v1/simulations")
@AllArgsConstructor
public class SimulationController {
	private final SimulatorService simulatorService;

	@GetMapping
	public String hello() {
		return simulatorService.hello();
	}
}
