package wow.minmax.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import wow.character.model.character.BuffListType;
import wow.character.model.character.Character;
import wow.commons.model.buff.Buff;
import wow.commons.client.converter.BuffConverter;
import wow.minmax.model.CharacterId;
import wow.commons.client.dto.BuffDTO;
import wow.minmax.service.PlayerCharacterService;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2022-12-31
 */
@RestController
@RequestMapping("api/v1/buffs")
@AllArgsConstructor
@Slf4j
public class BuffController {
	private final PlayerCharacterService playerCharacterService;
	private final BuffConverter buffConverter;

	@GetMapping("{characterId}/{buffListType}")
	public List<BuffDTO> getBuffs(
			@PathVariable("characterId") CharacterId characterId,
			@PathVariable("buffListType") BuffListType buffListType
	) {
		var character = playerCharacterService.getCharacter(characterId);
		return getBuffs(character, buffListType);
	}

	@PutMapping("{characterId}/{buffListType}")
	public List<BuffDTO> enableBuff(
			@PathVariable("characterId") CharacterId characterId,
			@PathVariable("buffListType") BuffListType buffListType,
			@RequestBody BuffDTO buff
	) {
		var buffId = buff.getBuffId();
		var rank = buff.getRank();
		var enabled = buff.isEnabled();
		var character = playerCharacterService.enableBuff(characterId, buffListType, buffId, rank, enabled);

		log.info("Changed buff charId: {}, list: {}, buffId: {}, rank: {}, enabled: {}", characterId, buffListType, buffId, rank, enabled);
		return getBuffs(character, buffListType);
	}

	private List<BuffDTO> getBuffs(Character character, BuffListType buffListType) {
		var buffs = character.getBuffList(buffListType);

		return buffs.getAvailableHighestRanks().stream()
				.map(buff -> getBuffDTO(buff, buffs.has(buff.getBuffId())))
				.toList();
	}

	private BuffDTO getBuffDTO(Buff buff, boolean enabled) {
		var dto = buffConverter.convert(buff);
		dto.setEnabled(enabled);
		return dto;
	}
}
