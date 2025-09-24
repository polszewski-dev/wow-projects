package wow.minmax.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import wow.character.model.character.PlayerCharacter;
import wow.estimator.client.dto.stats.*;
import wow.minmax.converter.dto.PlayerConverter;
import wow.minmax.repository.MinmaxConfigRepository;
import wow.minmax.service.PlayerCharacterService;
import wow.minmax.service.StatsService;

import static wow.minmax.model.config.CharacterFeature.COMBAT_RATINGS;
import static wow.minmax.model.config.CharacterFeature.WORLD_BUFFS;

/**
 * User: POlszewski
 * Date: 2025-03-17
 */
@Service
@AllArgsConstructor
public class StatsServiceImpl implements StatsService {
	private final PlayerCharacterService playerCharacterService;
	private final MinmaxConfigRepository minmaxConfigRepository;
	private final PlayerConverter playerConverter;

	@Qualifier("statsWebClient")
	private final WebClient webClient;

	@Override
	public GetAbilityStatsResponseDTO getAbilityStats(PlayerCharacter player) {
		var viewConfig = playerCharacterService.getViewConfig(player);
		var usesCombatRatings = minmaxConfigRepository.hasFeature(player, COMBAT_RATINGS);

		var request = new GetAbilityStatsRequestDTO(
				playerConverter.convert(player),
				viewConfig.relevantSpells(),
				usesCombatRatings,
				viewConfig.equivalentAmount()
		);

		return webClient
				.post()
				.uri("/ability")
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(request)
				.retrieve()
				.bodyToMono(GetAbilityStatsResponseDTO.class)
				.block();
	}

	@Override
	public GetCharacterStatsResponseDTO getCharacterStats(PlayerCharacter player) {
		var worldBuffsAllowed = minmaxConfigRepository.hasFeature(player, WORLD_BUFFS);

		var request = new GetCharacterStatsRequestDTO(
				playerConverter.convert(player),
				worldBuffsAllowed
		);

		return webClient
				.post()
				.uri("/character")
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(request)
				.retrieve()
				.bodyToMono(GetCharacterStatsResponseDTO.class)
				.block();
	}

	@Override
	public GetSpecialAbilityStatsResponseDTO getSpecialAbilityStats(PlayerCharacter player) {
		var request = new GetSpecialAbilityStatsRequestDTO(
				playerConverter.convert(player)
		);

		return webClient
				.post()
				.uri("/special")
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(request)
				.retrieve()
				.bodyToMono(GetSpecialAbilityStatsResponseDTO.class)
				.block();
	}

	@Override
	public GetRotationStatsResponseDTO getRotationStats(PlayerCharacter player) {
		var request = new GetRotationStatsRequestDTO(
				playerConverter.convert(player)
		);

		return webClient
				.post()
				.uri("/rotation")
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(request)
				.retrieve()
				.bodyToMono(GetRotationStatsResponseDTO.class)
				.block();
	}

	@Override
	public GetTalentStatsResponseDTO getTalentStats(PlayerCharacter player) {
		var request = new GetTalentStatsRequestDTO(
				playerConverter.convert(player)
		);

		return webClient
				.post()
				.uri("/talent")
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(request)
				.retrieve()
				.bodyToMono(GetTalentStatsResponseDTO.class)
				.block();
	}
}
