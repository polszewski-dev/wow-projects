package wow.minmax.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wow.minmax.client.dto.stats.*;
import wow.minmax.converter.dto.stats.*;
import wow.minmax.model.CharacterId;
import wow.minmax.service.PlayerCharacterService;
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
	private final PlayerCharacterService playerCharacterService;
	private final StatsService statsService;
	private final SpellStatsConverter spellStatsConverter;
	private final CharacterStatsConverter characterStatsConverter;
	private final SpecialAbilityStatsConverter specialAbilityStatsConverter;
	private final RotationStatsConverter rotationStatsConverter;
	private final TalentStatsConverter talentStatsConverter;

	@GetMapping("{characterId}/spell")
	public List<SpellStatsDTO> getSpellStats(
			@PathVariable("characterId") CharacterId characterId
	) {
		var player = playerCharacterService.getPlayer(characterId);
		var stats = statsService.getSpellStats(player).stats();

		return spellStatsConverter.convertList(stats, player.getPhaseId());
	}

	@GetMapping("{characterId}/character")
	public List<CharacterStatsDTO> getCharacterStats(
			@PathVariable("characterId") CharacterId characterId
	) {
		var player = playerCharacterService.getPlayer(characterId);
		var stats = statsService.getCharacterStats(player).stats();

		return characterStatsConverter.convertList(stats);
	}

	@GetMapping("{characterId}/special")
	public List<SpecialAbilityStatsDTO> getSpecialAbilityStats(
			@PathVariable("characterId") CharacterId characterId
	) {
		var player = playerCharacterService.getPlayer(characterId);
		var stats = statsService.getSpecialAbilityStats(player).stats();

		return specialAbilityStatsConverter.convertList(stats);
	}

	@GetMapping("{characterId}/rotation")
	public RotationStatsDTO getRotationStats(
			@PathVariable("characterId") CharacterId characterId
	) {
		var player = playerCharacterService.getPlayer(characterId);
		var stats = statsService.getRotationStats(player).stats();

		return rotationStatsConverter.convert(stats, player.getPhaseId());
	}

	@GetMapping("{characterId}/talent")
	public List<TalentStatsDTO> getTalentStats(
			@PathVariable("characterId") CharacterId characterId
	) {
		var player = playerCharacterService.getPlayer(characterId);
		var stats = statsService.getTalentStats(player).stats();

		return talentStatsConverter.convertList(stats, player.getPhaseId());
	}
}
