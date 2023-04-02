package wow.minmax.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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

	@GetMapping("{characterId}/list")
	public List<BuffDTO> getBuffs(
			@PathVariable("characterId") CharacterId characterId
	) {
		Character character = playerProfileService.getCharacter(characterId);
		return getBuffs(character);
	}

	@GetMapping("{characterId}/enable/{buffId}/{enabled}")
	public List<BuffDTO> changeBuff(
			@PathVariable("characterId") CharacterId characterId,
			@PathVariable("buffId") int buffId,
			@PathVariable("enabled") boolean enabled
	) {
		Character character = playerProfileService.enableBuff(characterId, buffId, enabled);
		log.info("Changed buff charId: {}, buffId: {}, enabled: {}", characterId, buffId, enabled);
		return getBuffs(character);
	}

	private List<BuffDTO> getBuffs(Character character) {
		Buffs buffs = character.getBuffs();
		List<Buff> availableBuffs = spellService.getBuffs(character);

		return availableBuffs.stream()
				.map(availableBuff -> getBuffDTO(availableBuff, buffs))
				.toList();
	}

	private BuffDTO getBuffDTO(Buff buff, Buffs buffs) {
		BuffDTO dto = buffConverter.convert(buff);
		dto.setEnabled(buffs.hasBuff(buff));
		return dto;
	}
}
