package wow.minmax.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wow.character.model.character.BuffListType;
import wow.character.model.character.Buffs;
import wow.character.model.character.Character;
import wow.character.service.SpellService;
import wow.commons.model.buffs.Buff;
import wow.minmax.converter.dto.BuffConverter;
import wow.minmax.model.CharacterId;
import wow.minmax.model.dto.BuffDTO;
import wow.minmax.service.PlayerProfileService;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2022-12-31
 */
@RestController
@RequestMapping("api/v1/buff")
@AllArgsConstructor
@Slf4j
public class BuffController {
	private final PlayerProfileService playerProfileService;
	private final SpellService spellService;
	private final BuffConverter buffConverter;

	@GetMapping("{characterId}/{buffListType}/list")
	public List<BuffDTO> getBuffs(
			@PathVariable("characterId") CharacterId characterId,
			@PathVariable("buffListType") BuffListType buffListType
	) {
		Character character = playerProfileService.getCharacter(characterId);
		return getBuffs(character, buffListType);
	}

	@GetMapping("{characterId}/{buffListType}/enable/{buffId}/{enabled}")
	public List<BuffDTO> enableBuff(
			@PathVariable("characterId") CharacterId characterId,
			@PathVariable("buffListType") BuffListType buffListType,
			@PathVariable("buffId") int buffId,
			@PathVariable("enabled") boolean enabled
	) {
		Character character = playerProfileService.enableBuff(characterId, buffListType, buffId, enabled);
		log.info("Changed buff charId: {}, list: {}, buffId: {}, enabled: {}", characterId, buffListType, buffId, enabled);
		return getBuffs(character, buffListType);
	}

	private List<BuffDTO> getBuffs(Character character, BuffListType buffListType) {
		Buffs buffs = character.getBuffList(buffListType);

		return getBuffDTOs(buffs, character, buffListType);
	}

	private List<BuffDTO> getBuffDTOs(Buffs buffs, Character character, BuffListType buffListType) {
		return spellService.getBuffs(character).stream()
				.filter(buffListType.getFilter())
				.map(buff -> getBuffDTO(buff, buffs))
				.toList();
	}

	private BuffDTO getBuffDTO(Buff buff, Buffs buffs) {
		BuffDTO dto = buffConverter.convert(buff);
		dto.setEnabled(buffs.has(buff));
		return dto;
	}
}
