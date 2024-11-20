package wow.simulator.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wow.commons.model.Duration;
import wow.commons.repository.spell.SpellRepository;
import wow.simulator.client.dto.ActiveEffectDTO;
import wow.simulator.client.dto.SimulationRequestDTO;
import wow.simulator.client.dto.SimulationResponseDTO;
import wow.simulator.converter.PlayerConverter;
import wow.simulator.converter.StatsConverter;
import wow.simulator.model.effect.impl.NonPeriodicEffectInstance;
import wow.simulator.model.unit.Player;
import wow.simulator.model.unit.Unit;
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

	private final SpellRepository spellRepository;

	private final PlayerConverter playerConverter;
	private final StatsConverter statsConverter;

	@PostMapping
	public SimulationResponseDTO simulate(@RequestBody SimulationRequestDTO request) {
		var player = playerConverter.convert(request.player());
		var duration = Duration.seconds(request.duration());
		var rngType = request.rngType();

		long start = System.currentTimeMillis();

		var stats = simulatorService.simulate(player, duration, rngType, () -> applyActiveEffects(player, request));

		long end = System.currentTimeMillis();

		log.info("Simulation ended after {} seconds", (end - start) / 1000.0);

		return new SimulationResponseDTO(
				statsConverter.convert(stats)
		);
	}

	private void applyActiveEffects(Player player, SimulationRequestDTO request) {
		for (var activeEffect : request.player().activeEffects()) {
			applyEffect(player, activeEffect, player);
		}

		for (var activeEffect : request.player().target().activeEffects()) {
			applyEffect(player.getTarget(), activeEffect, player);
		}
	}

	private void applyEffect(Unit target, ActiveEffectDTO activeEffect, Unit owner) {
		var effect = spellRepository.getEffect(activeEffect.effectId(), owner.getPhaseId()).orElseThrow();
		var effectInstance = new NonPeriodicEffectInstance(
				owner,
				target,
				effect,
				Duration.INFINITE,
				activeEffect.numStacks(),
				1,
				null
		);

		target.addEffect(effectInstance);
	}
}
