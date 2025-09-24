package wow.estimator.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wow.estimator.client.dto.stats.*;
import wow.estimator.converter.*;
import wow.estimator.service.StatsService;

/**
 * User: POlszewski
 * Date: 2021-12-31
 */
@RestController
@RequestMapping("api/v1/stats")
@AllArgsConstructor
public class StatsController {
	private final StatsService statsService;
	private final PlayerConverter playerConverter;
	private final AbilityStatsConverter abilityStatsConverter;
	private final CharacterStatsConverter characterStatsConverter;
	private final SpecialAbilityStatsConverter specialAbilityStatsConverter;
	private final RotationStatsConverter rotationStatsConverter;
	private final TalentStatsConverter talentStatsConverter;

	@PostMapping("ability")
	public GetAbilityStatsResponseDTO getAbilityStats(@RequestBody GetAbilityStatsRequestDTO request) {
		var player = playerConverter.convertBack(request.player());
		var abilityIds = request.abilityIds();
		var usesCombatRatings = request.usesCombatRatings();
		var equivalentAmount = request.equivalentAmount();

		var stats = statsService.getAbilityStats(player, abilityIds, usesCombatRatings, equivalentAmount);

		return new GetAbilityStatsResponseDTO(abilityStatsConverter.convertList(stats));
	}

	@PostMapping("character")
	public GetCharacterStatsResponseDTO getCharacterStats(@RequestBody GetCharacterStatsRequestDTO request) {
		var player = playerConverter.convertBack(request.player());
		var worldBuffsAllowed = request.worldBuffsAllowed();

		var stats = statsService.getCharacterStats(player, worldBuffsAllowed);

		return new GetCharacterStatsResponseDTO(characterStatsConverter.convert(stats));
	}

	@PostMapping("special")
	public GetSpecialAbilityStatsResponseDTO getSpecialAbilityStats(@RequestBody GetSpecialAbilityStatsRequestDTO request) {
		var player = playerConverter.convertBack(request.player());

		var stats = statsService.getSpecialAbilityStats(player);

		return new GetSpecialAbilityStatsResponseDTO(specialAbilityStatsConverter.convertList(stats));
	}

	@PostMapping("rotation")
	public GetRotationStatsResponseDTO getRotationStats(@RequestBody GetRotationStatsRequestDTO request) {
		var player = playerConverter.convertBack(request.player());

		var rotationStats = statsService.getRotationStats(player, player.getRotation());

		return new GetRotationStatsResponseDTO(rotationStatsConverter.convert(rotationStats));
	}

	@PostMapping("talent")
	public GetTalentStatsResponseDTO getTalentStats(@RequestBody GetTalentStatsRequestDTO request) {
		var player = playerConverter.convertBack(request.player());

		var stats = statsService.getTalentStats(player);

		return new GetTalentStatsResponseDTO(talentStatsConverter.convertList(stats));
	}
}
