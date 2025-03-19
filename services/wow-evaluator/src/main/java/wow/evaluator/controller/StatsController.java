package wow.evaluator.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wow.evaluator.client.dto.stats.*;
import wow.evaluator.converter.*;
import wow.evaluator.service.StatsService;

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
	private final SpellStatsConverter spellStatsConverter;
	private final CharacterStatsConverter characterStatsConverter;
	private final SpecialAbilityStatsConverter specialAbilityStatsConverter;
	private final RotationStatsConverter rotationStatsConverter;
	private final TalentStatsConverter talentStatsConverter;

	@PostMapping("spell")
	public GetSpellStatsResponseDTO getSpellStats(@RequestBody GetSpellStatsRequestDTO request) {
		var player = playerConverter.convertBack(request.player());
		var spells = request.spells();
		var usesCombatRatings = request.usesCombatRatings();
		var equivalentAmount = request.equivalentAmount();

		var stats = statsService.getSpellStats(player, spells, usesCombatRatings, equivalentAmount);

		return new GetSpellStatsResponseDTO(spellStatsConverter.convertList(stats));
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
