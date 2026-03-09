package wow.minmax.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wow.minmax.client.dto.stats.*;
import wow.minmax.converter.dto.stats.*;
import wow.minmax.model.PlayerId;
import wow.minmax.service.PlayerService;
import wow.minmax.service.StatsService;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2021-12-31
 */
@RestController
@RequestMapping("api/v1/stats")
@AllArgsConstructor
public class StatsController {
	private final PlayerService playerService;
	private final StatsService statsService;
	private final AbilityStatsConverter abilityStatsConverter;
	private final CharacterStatsConverter characterStatsConverter;
	private final SpecialAbilityStatsConverter specialAbilityStatsConverter;
	private final RotationStatsConverter rotationStatsConverter;
	private final TalentStatsConverter talentStatsConverter;

	@GetMapping("{playerId}/ability")
	public List<AbilityStatsDTO> getAbilityStats(
			@PathVariable("playerId") PlayerId playerId
	) {
		var player = playerService.getPlayer(playerId);
		var stats = statsService.getAbilityStats(player).stats();

		return abilityStatsConverter.convertList(stats, player.getPhaseId());
	}

	@GetMapping("{playerId}/character")
	public List<CharacterStatsDTO> getCharacterStats(
			@PathVariable("playerId") PlayerId playerId
	) {
		var player = playerService.getPlayer(playerId);
		var stats = statsService.getCharacterStats(player).stats();

		return characterStatsConverter.convertList(stats);
	}

	@GetMapping("{playerId}/special")
	public List<SpecialAbilityStatsDTO> getSpecialAbilityStats(
			@PathVariable("playerId") PlayerId playerId
	) {
		var player = playerService.getPlayer(playerId);
		var stats = statsService.getSpecialAbilityStats(player).stats();

		return specialAbilityStatsConverter.convertList(stats);
	}

	@GetMapping("{playerId}/rotation")
	public RotationStatsDTO getRotationStats(
			@PathVariable("playerId") PlayerId playerId
	) {
		var player = playerService.getPlayer(playerId);
		var stats = statsService.getRotationStats(player).stats();

		return rotationStatsConverter.convert(stats, player.getPhaseId());
	}

	@GetMapping("{playerId}/talent")
	public List<TalentStatsDTO> getTalentStats(
			@PathVariable("playerId") PlayerId playerId
	) {
		var player = playerService.getPlayer(playerId);
		var stats = statsService.getTalentStats(player).stats();

		return talentStatsConverter.convertList(stats, player.getPhaseId());
	}
}
