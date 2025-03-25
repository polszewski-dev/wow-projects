package wow.simulator.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wow.commons.client.dto.BuffDTO;
import wow.commons.model.Duration;
import wow.commons.model.spell.EffectReplacementMode;
import wow.commons.repository.spell.BuffRepository;
import wow.simulator.client.dto.SimulationRequestDTO;
import wow.simulator.client.dto.SimulationResponseDTO;
import wow.simulator.converter.PlayerConverter;
import wow.simulator.converter.StatsConverter;
import wow.simulator.model.effect.impl.NonPeriodicEffectInstance;
import wow.simulator.model.unit.Player;
import wow.simulator.model.unit.Unit;
import wow.simulator.service.SimulatorService;

import static wow.commons.util.CollectionUtil.getUniqueResult;

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

	private final BuffRepository buffRepository;

	private final PlayerConverter playerConverter;
	private final StatsConverter statsConverter;

	@PostMapping
	public SimulationResponseDTO simulate(@RequestBody SimulationRequestDTO request) {
		var player = playerConverter.convertBack(request.player());
		var duration = Duration.seconds(request.duration());
		var rngType = request.rngType();

		long start = System.currentTimeMillis();

		var stats = simulatorService.simulate(player, duration, rngType, () -> applyTargetDebuffs(player, request));

		long end = System.currentTimeMillis();

		log.info("Simulation ended after {} seconds", (end - start) / 1000.0);

		return new SimulationResponseDTO(
				statsConverter.convert(stats)
		);
	}

	private void applyTargetDebuffs(Player player, SimulationRequestDTO request) {
		player.getTarget().resetBuffs();

		for (var activeEffect : request.player().target().buffs()) {
			applyEffect(player.getTarget(), activeEffect, player);
		}
	}

	private void applyEffect(Unit target, BuffDTO buffDTO, Unit owner) {
		var matchingBuffs = buffRepository.getBuff(buffDTO.buffId(), buffDTO.rank(), target.getPhaseId());
		var buff = getUniqueResult(matchingBuffs).orElseThrow();
		var effect = buff.getEffect();
		var effectInstance = new NonPeriodicEffectInstance(
				owner,
				target,
				effect,
				Duration.INFINITE,
				buff.getStacks(),
				1,
				null,
				null,
				null
		);

		target.addEffect(effectInstance, EffectReplacementMode.DEFAULT);
	}
}
