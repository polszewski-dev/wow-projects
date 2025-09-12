package wow.minmax.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import wow.character.model.character.PlayerCharacter;
import wow.minmax.config.SimulationConfig;
import wow.minmax.converter.dto.PlayerConverter;
import wow.minmax.service.PlayerCharacterService;
import wow.minmax.service.SimulatorService;
import wow.simulator.client.dto.SimulationRequestDTO;
import wow.simulator.client.dto.SimulationResponseDTO;
import wow.simulator.client.dto.StatsDTO;

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
	public StatsDTO simulate(PlayerCharacter player) {
		var request = getSimulationRequestDTO(player);

		var response = webClient.post()
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(request)
				.retrieve()
				.bodyToMono(SimulationResponseDTO.class)
				.block();

		return response.stats();
	}

	private SimulationRequestDTO getSimulationRequestDTO(PlayerCharacter player) {
		return new SimulationRequestDTO(
				playerConverter.convert(player),
				simulationConfig.getDuration(),
				simulationConfig.getRngType()
		);
	}
}
