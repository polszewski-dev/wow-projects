package wow.minmax.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import wow.character.model.character.Raid;
import wow.minmax.config.SimulationConfig;
import wow.minmax.converter.dto.NonPlayerConverter;
import wow.minmax.converter.dto.RaidConverter;
import wow.minmax.model.NonPlayer;
import wow.minmax.model.Player;
import wow.minmax.model.PlayerId;
import wow.minmax.service.RaidService;
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
	private final RaidService raidService;

	private final RaidConverter raidConverter;
	private final NonPlayerConverter nonPlayerConverter;

	private final SimulationConfig simulationConfig;

	@Qualifier("simulatorWebClient")
	private final WebClient webClient;

	@Override
	public StatsDTO simulate(PlayerId playerId) {
		var request = getSimulationRequestDTO(playerId);

		var response = webClient.post()
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(request)
				.retrieve()
				.bodyToMono(SimulationResponseDTO.class)
				.block();

		return response.stats();
	}

	private SimulationRequestDTO getSimulationRequestDTO(PlayerId playerId) {
		var raid = raidService.getRaid(playerId);

		return new SimulationRequestDTO(
				raidConverter.convert(raid),
				nonPlayerConverter.convert(getTarget(raid)),
				simulationConfig.getDuration(),
				simulationConfig.getRngType()
		);
	}

	private static NonPlayer getTarget(Raid<Player> raid) {
		return (NonPlayer) raid.getFirstMember().getTarget();
	}
}
