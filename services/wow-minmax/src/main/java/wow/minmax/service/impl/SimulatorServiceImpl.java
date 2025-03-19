package wow.minmax.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import wow.minmax.config.SimulationConfig;
import wow.minmax.converter.dto.PlayerConverter;
import wow.minmax.model.CharacterId;
import wow.minmax.service.PlayerCharacterService;
import wow.minmax.service.SimulatorService;
import wow.simulator.client.dto.SimulationRequestDTO;
import wow.simulator.client.dto.SimulationResponseDTO;

/**
 * User: POlszewski
 * Date: 2024-11-09
 */
@Service
@RequiredArgsConstructor
public class SimulatorServiceImpl implements SimulatorService {
	private final PlayerCharacterService playerCharacterService;

	private final PlayerConverter playerConverter;

	private final SimulationConfig simulationConfig;

	@Qualifier("simulatorWebClient")
	private final WebClient webClient;

	@Override
	public SimulationResponseDTO simulate(CharacterId characterId) {
		var request = getSimulationRequestDTO(characterId);

		return webClient.post()
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(request)
				.retrieve()
				.bodyToMono(SimulationResponseDTO.class)
				.block();
	}

	private SimulationRequestDTO getSimulationRequestDTO(CharacterId characterId) {
		var player = playerCharacterService.getPlayer(characterId);

		return new SimulationRequestDTO(
				playerConverter.convert(player),
				simulationConfig.getDuration(),
				simulationConfig.getRngType()
		);
	}
}
