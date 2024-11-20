package wow.minmax.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import wow.character.model.character.Character;
import wow.commons.client.converter.BuffConverter;
import wow.commons.client.converter.CharacterProfessionConverter;
import wow.commons.client.converter.EquipmentConverter;
import wow.commons.client.converter.TalentConverter;
import wow.minmax.config.SimulationConfig;
import wow.minmax.model.CharacterId;
import wow.minmax.model.PlayerCharacter;
import wow.minmax.service.PlayerCharacterService;
import wow.minmax.service.SimulatorService;
import wow.simulator.client.dto.CharacterDTO;
import wow.simulator.client.dto.EnemyDTO;
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
	private final BuffConverter buffConverter;

	private final SimulationConfig simulationConfig;

	@Qualifier("simulator")
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
		var character = playerCharacterService.getCharacter(characterId);

		return new SimulationRequestDTO(
				getCharacterDTO(character),
				simulationConfig.getDuration(),
				simulationConfig.getRngType()
		);
	}

	private CharacterDTO getCharacterDTO(PlayerCharacter character) {
		return new CharacterDTO(
				"Player",
				character.getCharacterClassId(),
				character.getRaceId(),
				character.getLevel(),
				character.getPhaseId(),
				characterProfessionConverter.convertList(character.getProfessions().getList()),
				character.getExclusiveFactions().getList(),
				equipmentConverter.convert(character.getEquipment()),
				talentConverter.convertList(character.getTalents().getList()),
				buffConverter.convertList(character.getBuffs().getList()),
				character.getRole(),
				character.getActivePetType(),
				character.getRotation().getTemplate().toString(),
				getEnemyDTO(character.getTarget())
		);
	}

	private EnemyDTO getEnemyDTO(Character target) {
		return new EnemyDTO(
				"Target",
				target.getCreatureType(),
				target.getLevel(),
				buffConverter.convertList(target.getBuffs().getList())
		);
	}
}
