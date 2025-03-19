package wow.minmax.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import wow.character.model.character.Character;
import wow.commons.client.converter.CharacterProfessionConverter;
import wow.commons.client.converter.ConsumableConverter;
import wow.commons.client.converter.EquipmentConverter;
import wow.commons.client.converter.TalentConverter;
import wow.minmax.config.SimulationConfig;
import wow.minmax.converter.dto.ActiveEffectConverter;
import wow.minmax.model.CharacterId;
import wow.minmax.model.Player;
import wow.minmax.service.PlayerCharacterService;
import wow.minmax.service.SimulatorService;
import wow.simulator.client.dto.NonPlayerDTO;
import wow.simulator.client.dto.PlayerDTO;
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

	private final CharacterProfessionConverter characterProfessionConverter;
	private final EquipmentConverter equipmentConverter;
	private final TalentConverter talentConverter;
	private final ActiveEffectConverter activeEffectConverter;
	private final ConsumableConverter consumableConverter;

	private final SimulationConfig simulationConfig;

	private final WebClient webClient;

	@Override
	public SimulationResponseDTO simulate(CharacterId characterId) {
		var request = getSimulationRequestDTO(characterId);

		return webClient.post()
				.contentType(MediaType. APPLICATION_JSON)
				.bodyValue(request)
				.retrieve()
				.bodyToMono(SimulationResponseDTO. class)
				.block();
	}

	private SimulationRequestDTO getSimulationRequestDTO(CharacterId characterId) {
		var player = playerCharacterService.getPlayer(characterId);

		return new SimulationRequestDTO(
				getCharacterDTO(player),
				simulationConfig.getDuration(),
				simulationConfig.getRngType()
		);
	}

	private PlayerDTO getCharacterDTO(Player player) {
		return new PlayerDTO(
				"Player",
				player.getCharacterClassId(),
				player.getRaceId(),
				player.getLevel(),
				player.getPhaseId(),
				characterProfessionConverter.convertList(player.getProfessions().getList()),
				player.getExclusiveFactions().getList(),
				equipmentConverter.convert(player.getEquipment()),
				talentConverter.convertList(player.getTalents().getList()),
				player.getRole(),
				player.getActivePetType(),
				player.getRotation().getTemplate().toString(),
				activeEffectConverter.convertList(player.getBuffs().getList()),
				consumableConverter.convertList(player.getConsumables().getList()),
				getEnemyDTO(player.getTarget())
		);
	}

	private NonPlayerDTO getEnemyDTO(Character target) {
		return new NonPlayerDTO(
				"Target",
				target.getCreatureType(),
				target.getLevel(),
				activeEffectConverter.convertList(target.getBuffs().getList())
		);
	}
}
