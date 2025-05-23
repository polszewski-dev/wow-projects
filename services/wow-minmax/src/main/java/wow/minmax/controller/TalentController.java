package wow.minmax.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wow.commons.client.converter.TalentConverter;
import wow.commons.client.dto.TalentDTO;
import wow.minmax.model.CharacterId;
import wow.minmax.service.PlayerCharacterService;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2022-12-31
 */
@RestController
@RequestMapping("api/v1/talents")
@AllArgsConstructor
@Slf4j
public class TalentController {
	private final PlayerCharacterService playerCharacterService;
	private final TalentConverter talentConverter;

	@GetMapping("{characterId}")
	public List<TalentDTO> getTalents(
			@PathVariable("characterId") CharacterId characterId
	) {
		var player = playerCharacterService.getPlayer(characterId);

		return player.getTalents().getList().stream()
				.map(talentConverter::convert)
				.toList();
	}
}
