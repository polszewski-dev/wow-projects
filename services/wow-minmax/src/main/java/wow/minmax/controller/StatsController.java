package wow.minmax.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wow.estimator.client.dto.stats.*;
import wow.minmax.model.CharacterId;
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
	private final StatsService statsService;

	@GetMapping("{characterId}/spell")
	public List<SpellStatsDTO> getSpellStats(
			@PathVariable("characterId") CharacterId characterId
	) {
		return statsService.getSpellStats(characterId).stats();
	}

	@GetMapping("{characterId}/character")
	public List<CharacterStatsDTO> getCharacterStats(
			@PathVariable("characterId") CharacterId characterId
	) {
		return statsService.getCharacterStats(characterId).stats();
	}

	@GetMapping("{characterId}/special")
	public List<SpecialAbilityStatsDTO> getSpecialAbilityStats(
			@PathVariable("characterId") CharacterId characterId
	) {
		return statsService.getSpecialAbilityStats(characterId).stats();
	}

	@GetMapping("{characterId}/rotation")
	public RotationStatsDTO getRotationStats(
			@PathVariable("characterId") CharacterId characterId
	) {
		return statsService.getRotationStats(characterId).stats();
	}

	@GetMapping("{characterId}/talent")
	public List<TalentStatsDTO> getTalentStats(
			@PathVariable("characterId") CharacterId characterId
	) {
		return statsService.getTalentStats(characterId).stats();
	}
}
