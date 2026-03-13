package wow.minmax.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import wow.character.model.character.Raid;
import wow.estimator.client.dto.stats.*;
import wow.minmax.converter.dto.NonPlayerConverter;
import wow.minmax.converter.dto.RaidConverter;
import wow.minmax.model.NonPlayer;
import wow.minmax.model.Player;
import wow.minmax.repository.MinmaxConfigRepository;
import wow.minmax.service.PlayerService;
import wow.minmax.service.RaidService;
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
	private final PlayerService playerService;
	private final RaidService raidService;
	private final MinmaxConfigRepository minmaxConfigRepository;
	private final RaidConverter raidConverter;
	private final NonPlayerConverter nonPlayerConverter;

	@Qualifier("statsWebClient")
	private final WebClient webClient;

	@Override
	public GetAbilityStatsResponseDTO getAbilityStats(Player player) {
		var raid = raidService.getRaid(player);
		var viewConfig = playerService.getViewConfig(player);
		var usesCombatRatings = minmaxConfigRepository.hasFeature(player, COMBAT_RATINGS);

		var request = new GetAbilityStatsRequestDTO(
				raidConverter.convert(raid),
				nonPlayerConverter.convert(getTarget(raid)),
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
	public GetCharacterStatsResponseDTO getCharacterStats(Player player) {
		var raid = raidService.getRaid(player);
		var worldBuffsAllowed = minmaxConfigRepository.hasFeature(player, WORLD_BUFFS);

		var request = new GetCharacterStatsRequestDTO(
				raidConverter.convert(raid),
				nonPlayerConverter.convert(getTarget(raid)),
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
	public GetSpecialAbilityStatsResponseDTO getSpecialAbilityStats(Player player) {
		var raid = raidService.getRaid(player);

		var request = new GetSpecialAbilityStatsRequestDTO(
				raidConverter.convert(raid),
				nonPlayerConverter.convert(getTarget(raid))
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
	public GetRotationStatsResponseDTO getRotationStats(Player player) {
		var raid = raidService.getRaid(player);

		var request = new GetRotationStatsRequestDTO(
				raidConverter.convert(raid),
				nonPlayerConverter.convert(getTarget(raid))
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
	public GetTalentStatsResponseDTO getTalentStats(Player player) {
		var raid = raidService.getRaid(player);

		var request = new GetTalentStatsRequestDTO(
				raidConverter.convert(raid),
				nonPlayerConverter.convert(getTarget(raid))
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

	private static NonPlayer getTarget(Raid<Player> raid) {
		return (NonPlayer) raid.getFirstMember().getTarget();
	}
}
